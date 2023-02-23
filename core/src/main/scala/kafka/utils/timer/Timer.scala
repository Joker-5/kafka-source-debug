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
import org.apache.kafka.common.utils.{KafkaThread, Time}

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.{DelayQueue, Executors, TimeUnit}

/**
 * 定义管理延迟操作相关方法的接口
 */
trait Timer {
  /**
    * Add a new task to this executor. It will be executed after the task's delay
    * (beginning from the time of submission)
    * 
    * 将指定定时任务插入到时间轮中
    * 
    * @param timerTask the task to add
    */
  def add(timerTask: TimerTask): Unit

  /**
    * Advance the internal clock, executing any tasks whose expiration has been
    * reached within the duration of the passed timeout.
    * 
    * 向前推进时钟，执行已达过期时间的延迟任务
    * 
    * @param timeoutMs
    * @return whether or not any tasks were executed
    */
  def advanceClock(timeoutMs: Long): Boolean

  /**
    * Get the number of tasks pending execution
    * 
    * 获取当前总定时任务数
    * 
    * @return the number of tasks
    */
  def size: Int

  /**
    * Shutdown the timer service, leaving pending tasks unexecuted
    * 
    * 关闭定时器
    * 
    */
  def shutdown(): Unit
}

/**
 * Timer 的实现类，封装了分层时间轮对象，为 Purgatory 提供延迟请求管理功能
 * @param executorName Purgatory 的名字
 * @param tickMs tick 一次的时长
 * @param wheelSize 时间轮上 Bucket 数量
 * @param startMs SystemTimer 定时器的启动时间，以 ms 为单位
 */
@threadsafe
class SystemTimer(executorName: String,
                  tickMs: Long = 1,
                  wheelSize: Int = 20,
                  startMs: Long = Time.SYSTEM.hiResClockMs) extends Timer {

  // timeout timer
  /**
   * 单线程线程池(FixedThreadPool)，用于异步执行提交的定时任务逻辑
   */
  private[this] val taskExecutor = Executors.newFixedThreadPool(1,
    (runnable: Runnable) => KafkaThread.nonDaemon("executor-" + executorName, runnable))

  /**
   * 保存定时器下的所有 Bucket 对象
   */
  private[this] val delayQueue = new DelayQueue[TimerTaskList]()
  private[this] val taskCounter = new AtomicInteger(0)
  /**
   * 分层时间轮实现类
   */
  private[this] val timingWheel = new TimingWheel(
    tickMs = tickMs,
    wheelSize = wheelSize,
    startMs = startMs,
    taskCounter = taskCounter,
    delayQueue
  )

  // Locks used to protect data structures while ticking
  private[this] val readWriteLock = new ReentrantReadWriteLock()
  private[this] val readLock = readWriteLock.readLock()
  private[this] val writeLock = readWriteLock.writeLock()

  /**
   * 将定时任务插入到时间轮中
   * @param timerTask the task to add
   */
  def add(timerTask: TimerTask): Unit = {
    // 获取读锁，在没有线程持有写锁的情况下，多个线程能够同时向时间轮添加定时任务
    readLock.lock()
    try {
      // 调用 addTimerTaskEntry 执行具体的插入逻辑，TimerTask 被封装进 TimerTaskEntry 中
      addTimerTaskEntry(new TimerTaskEntry(timerTask, timerTask.delayMs + Time.SYSTEM.hiResClockMs))
    } finally {
      // 释放读锁
      readLock.unlock()
    }
  }

  /**
   * 将定时任务添加到时间轮中的具体逻辑
   * @param timerTaskEntry 待添加定时任务
   */
  private def addTimerTaskEntry(timerTaskEntry: TimerTaskEntry): Unit = {
    // 根据 timerTaskEntry 的状态决定执行具体的逻辑：
    // 1）未过期且未取消，将其添加到时间轮中
    // 2）已取消，什么也不做
    // 3）已过期，提交到线程池，等待执行
    
    // 尝试将延迟任务添加到时间轮中
    if (!timingWheel.add(timerTaskEntry)) {
      // Already expired or cancelled
      // 定时任务已过期
      if (!timerTaskEntry.cancelled) {
        // 该task对应于DelayedOperation中的run方法
        taskExecutor.submit(timerTaskEntry.timerTask)
      }
    }
  }

  /*
   * Advances the clock if there is an expired bucket. If there isn't any expired bucket when called,
   * waits up to timeoutMs before giving up.
   * 
   * 驱动时钟向前推进
   */
  def advanceClock(timeoutMs: Long): Boolean = {
    // 获取 delayQueue 中下一个已过期的 Bucket
    var bucket = delayQueue.poll(timeoutMs, TimeUnit.MILLISECONDS)
    if (bucket != null) {
      // 获取写锁，一旦有线程持有写锁，则任何线程执行 add 或 advanceClock 方法时都会被阻塞
      writeLock.lock()
      try {
        while (bucket != null) {
          // 推动时间轮指针前进，滚动到 Bucket 的过期时间点
          timingWheel.advanceClock(bucket.getExpiration)
          // 将 Bucket 下的所有定时任务重新写回到时间轮中
          bucket.flush(addTimerTaskEntry)
          // 读取下一个 Bucket
          bucket = delayQueue.poll()
        }
      } finally {
        // 释放写锁
        writeLock.unlock()
      }
      true
    } else {
      false
    }
  }

  def size: Int = taskCounter.get

  override def shutdown(): Unit = {
    taskExecutor.shutdown()
  }

}
