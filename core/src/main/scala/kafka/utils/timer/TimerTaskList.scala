/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kafka.utils.timer

import kafka.utils.threadsafe
import org.apache.kafka.common.utils.Time

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import java.util.concurrent.{Delayed, TimeUnit}
import scala.math._

/**
 * 存储延时任务的双向循环链表 Bucket(Bucket 就相当于钟表的一格)
 * @param taskCounter 链表中的总延时任务数
 */
@threadsafe
private[timer] class TimerTaskList(taskCounter: AtomicInteger) extends Delayed {

  // TimerTaskList forms a doubly linked cyclic list using a dummy root entry
  // root.next points to the head
  // root.prev points to the tail
  private[this] val root = new TimerTaskEntry(null, -1)
  root.next = root
  root.prev = root

  /**
   * Bucket 的过期时间戳，同一层 Bucket 的时间间隔都是相同的，
   * 只有当前时间超过了 Bucket 的起始时间，也就是 expiration 字段，Bucket 才算过期
   */
  private[this] val expiration = new AtomicLong(-1L)

  // Set the bucket's expiration time
  // Returns true if the expiration time is changed
  /**
   * 使用 CAS 来设置 Bucket 的过期时间戳，
   * 之所以要用 CAS，是因为目前 Kafka 是用一个 DelayQueue 来管理所有 Bucket，也就是 TimerTaskList 对象，
   * 随着时间推进，原有的 Bucket 也会不断过期，当这些 Bucket 过期后，Kafka 会重用这些 Bucket，
   * 重用的方式就是重新设置 Bucket 的过期时间，并将其再次加入到 DelayQueue 中，
   * 这里比较的目的就是判断这个 Bucket 是否要被插入到 DelayQueue 中
   * 
   * @param expirationMs Bucket 的过期时间戳
   * @return
   */
  def setExpiration(expirationMs: Long): Boolean = {
    expiration.getAndSet(expirationMs) != expirationMs
  }

  // Get the bucket's expiration time
  def getExpiration: Long = expiration.get

  // Apply the supplied function to each of tasks in this list
  def foreach(f: (TimerTask)=>Unit): Unit = {
    synchronized {
      var entry = root.next
      while (entry ne root) {
        val nextEntry = entry.next

        if (!entry.cancelled) f(entry.timerTask)

        entry = nextEntry
      }
    }
  }

  // Add a timer task entry to this list
  /**
   * 将延时任务添加到 Bucket 中，简单链表插入方法
   * @param timerTaskEntry
   */
  def add(timerTaskEntry: TimerTaskEntry): Unit = {
    var done = false
    while (!done) {
      // Remove the timer task entry if it is already in any other list
      // We do this outside of the sync block below to avoid deadlocking.
      // We may retry until timerTaskEntry.list becomes null.
      timerTaskEntry.remove()

      synchronized {
        timerTaskEntry.synchronized {
          if (timerTaskEntry.list == null) {
            // put the timer task entry to the end of the list. (root.prev points to the tail entry)
            val tail = root.prev
            timerTaskEntry.next = root
            timerTaskEntry.prev = tail
            timerTaskEntry.list = this
            tail.next = timerTaskEntry
            root.prev = timerTaskEntry
            taskCounter.incrementAndGet()
            done = true
          }
        }
      }
    }
  }

  // Remove the specified timer task entry from this list
  /**
   * 将指定延时任务从 Bucket 中移除，普通链表删除方法
   * @param timerTaskEntry
   */
  def remove(timerTaskEntry: TimerTaskEntry): Unit = {
    synchronized {
      timerTaskEntry.synchronized {
        if (timerTaskEntry.list eq this) {
          timerTaskEntry.next.prev = timerTaskEntry.prev
          timerTaskEntry.prev.next = timerTaskEntry.next
          timerTaskEntry.next = null
          timerTaskEntry.prev = null
          timerTaskEntry.list = null
          taskCounter.decrementAndGet()
        }
      }
    }
  }

  // Remove all task entries and apply the supplied function to each of them
  /**
   * 清空链表中的所有元素，用于将高层次时间轮 Bucket 上的定时任务重新插入回低层次的 Bucket 中。
   * @param f
   */
  def flush(f: TimerTaskEntry => Unit): Unit = {
    synchronized {
      var head = root.next
      // 遍历循环双向链表
      while (head ne root) {
        remove(head)
        // 执行传入参数 f 中的逻辑
        f(head)
        head = root.next
      }
      // 将过期时间设置清空
      expiration.set(-1L)
    }
  }

  def getDelay(unit: TimeUnit): Long = {
    unit.convert(max(getExpiration - Time.SYSTEM.hiResClockMs, 0), TimeUnit.MILLISECONDS)
  }

  def compareTo(d: Delayed): Int = {
    val other = d.asInstanceOf[TimerTaskList]
    java.lang.Long.compare(getExpiration, other.getExpiration)
  }

}

/**
 * 表示 Bucket TimerTaskList 链表下的一个元素
 * @param timerTask 与当前节点绑定的定时任务
 * @param expirationMs 定时任务过期时间
 */
private[timer] class TimerTaskEntry(val timerTask: TimerTask, val expirationMs: Long) extends Ordered[TimerTaskEntry] {

  

  /**
   * 与之绑定的 TimerTaskList 链表实例，
   * 之所以需要用 @volatile 保证其内存可见性，是因为 Kafka 的延时请求可能会被其他线程从一个链表搬移到另一个链表中
   */
  @volatile
  var list: TimerTaskList = null
  /**
   * 后继节点
   */
  var next: TimerTaskEntry = null
  /**
   * 前驱节点
   */
  var prev: TimerTaskEntry = null

  // if this timerTask is already held by an existing timer task entry,
  // setTimerTaskEntry will remove it.
  // 关联定时任务
  if (timerTask != null) timerTask.setTimerTaskEntry(this)

  def cancelled: Boolean = {
    timerTask.getTimerTaskEntry != this
  }

  /**
   * 将当前节点从双向链表中移除
   */
  def remove(): Unit = {
    var currentList = list
    // If remove is called when another thread is moving the entry from a task entry list to another,
    // this may fail to remove the entry due to the change of value of list. Thus, we retry until the list becomes null.
    // In a rare case, this thread sees null and exits the loop, but the other thread insert the entry to another list later.
    // 用 while 循环而不是 if 判断来保证在多线程环境下节点确实被移除出去了
    while (currentList != null) {
      currentList.remove(this)
      currentList = list
    }
  }

  override def compare(that: TimerTaskEntry): Int = {
    java.lang.Long.compare(expirationMs, that.expirationMs)
  }
}

