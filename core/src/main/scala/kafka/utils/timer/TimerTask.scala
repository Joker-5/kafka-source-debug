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

/**
 * Kafka 延时任务实现类
 */
trait TimerTask extends Runnable {

  /**
   * 定时任务超时时间，通常由 request.timeout.ms 参数指定
   */
  val delayMs: Long // timestamp in millisecond

  /**
   * 每个 TimerTask 定时任务都需要与一个链表对象 TimerTaskEntry 做关联，
   * 也就是说，每个定时任务都需要知道其在哪个 Bucket TimerTaskList 链表的哪个链表节点 TimerTaskEntry 上
   */
  private[this] var timerTaskEntry: TimerTaskEntry = null

  /**
   * 取消定时任务
   */
  def cancel(): Unit = {
    synchronized {
      // 从延迟调度中移除自己，表示没有超时，结束本轮超时检测，
      // 直接从与之绑定的 timerTaskEntry 中移除即可
      if (timerTaskEntry != null) timerTaskEntry.remove()
      timerTaskEntry = null
    }
  }

  /**
   * 将定时任务与 TimerTaskEntry 做关联
   * @param entry
   */
  private[timer] def setTimerTaskEntry(entry: TimerTaskEntry): Unit = {
    // synchronized 加锁，保证线程安全性
    synchronized {
      // if this timerTask is already held by an existing timer task entry,
      // we will remove such an entry first.
      // 首先需要判断当前的定时任务是否已经与其他 TimerTaskEntry 绑定了，
      // 如果是的话需要先将之前的绑定取消
      if (timerTaskEntry != null && timerTaskEntry != entry)
        timerTaskEntry.remove()

      timerTaskEntry = entry
    }
  }

  private[timer] def getTimerTaskEntry: TimerTaskEntry = timerTaskEntry

}
