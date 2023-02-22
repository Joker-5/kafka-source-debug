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

import kafka.utils.nonthreadsafe

import java.util.concurrent.DelayQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 
 * Hierarchical Timing Wheels
 *
 * A simple timing wheel is a circular list of buckets of timer tasks. Let u be the time unit.
 * A timing wheel with size n has n buckets and can hold timer tasks in n * u time interval.
 * Each bucket holds timer tasks that fall into the corresponding time range. At the beginning,
 * the first bucket holds tasks for [0, u), the second bucket holds tasks for [u, 2u), …,
 * the n-th bucket for [u * (n -1), u * n). Every interval of time unit u, the timer ticks and
 * moved to the next bucket then expire all timer tasks in it. So, the timer never insert a task
 * into the bucket for the current time since it is already expired. The timer immediately runs
 * the expired task. The emptied bucket is then available for the next round, so if the current
 * bucket is for the time t, it becomes the bucket for [t + u * n, t + (n + 1) * u) after a tick.
 * A timing wheel has O(1) cost for insert/delete (start-timer/stop-timer) whereas priority queue
 * based timers, such as java.util.concurrent.DelayQueue and java.util.Timer, have O(log n)
 * insert/delete cost.
 *
 * A major drawback of a simple timing wheel is that it assumes that a timer request is within
 * the time interval of n * u from the current time. If a timer request is out of this interval,
 * it is an overflow. A hierarchical timing wheel deals with such overflows. It is a hierarchically
 * organized timing wheels. The lowest level has the finest time resolution. As moving up the
 * hierarchy, time resolutions become coarser. If the resolution of a wheel at one level is u and
 * the size is n, the resolution of the next level should be n * u. At each level overflows are
 * delegated to the wheel in one level higher. When the wheel in the higher level ticks, it reinsert
 * timer tasks to the lower level. An overflow wheel can be created on-demand. When a bucket in an
 * overflow bucket expires, all tasks in it are reinserted into the timer recursively. The tasks
 * are then moved to the finer grain wheels or be executed. The insert (start-timer) cost is O(m)
 * where m is the number of wheels, which is usually very small compared to the number of requests
 * in the system, and the delete (stop-timer) cost is still O(1).
 *
 * Example
 * Let's say that u is 1 and n is 3. If the start time is c,
 * then the buckets at different levels are:
 *
 * level    buckets
 * 1        [c,c]   [c+1,c+1]  [c+2,c+2]
 * 2        [c,c+2] [c+3,c+5]  [c+6,c+8]
 * 3        [c,c+8] [c+9,c+17] [c+18,c+26]
 *
 * The bucket expiration is at the time of bucket beginning.
 * So at time = c+1, buckets [c,c], [c,c+2] and [c,c+8] are expired.
 * Level 1's clock moves to c+1, and [c+3,c+3] is created.
 * Level 2 and level3's clock stay at c since their clocks move in unit of 3 and 9, respectively.
 * So, no new buckets are created in level 2 and 3.
 *
 * Note that bucket [c,c+2] in level 2 won't receive any task since that range is already covered in level 1.
 * The same is true for the bucket [c,c+8] in level 3 since its range is covered in level 2.
 * This is a bit wasteful, but simplifies the implementation.
 *
 * 1        [c+1,c+1]  [c+2,c+2]  [c+3,c+3]
 * 2        [c,c+2]    [c+3,c+5]  [c+6,c+8]
 * 3        [c,c+8]    [c+9,c+17] [c+18,c+26]
 *
 * At time = c+2, [c+1,c+1] is newly expired.
 * Level 1 moves to c+2, and [c+4,c+4] is created,
 *
 * 1        [c+2,c+2]  [c+3,c+3]  [c+4,c+4]
 * 2        [c,c+2]    [c+3,c+5]  [c+6,c+8]
 * 3        [c,c+8]    [c+9,c+17] [c+18,c+18]
 *
 * At time = c+3, [c+2,c+2] is newly expired.
 * Level 2 moves to c+3, and [c+5,c+5] and [c+9,c+11] are created.
 * Level 3 stay at c.
 *
 * 1        [c+3,c+3]  [c+4,c+4]  [c+5,c+5]
 * 2        [c+3,c+5]  [c+6,c+8]  [c+9,c+11]
 * 3        [c,c+8]    [c+9,c+17] [c+8,c+11]
 *
 * The hierarchical timing wheels works especially well when operations are completed before they time out.
 * Even when everything times out, it still has advantageous when there are many items in the timer.
 * Its insert cost (including reinsert) and delete cost are O(m) and O(1), respectively while priority
 * queue based timers takes O(log N) for both insert and delete where N is the number of items in the queue.
 *
 * This class is not thread-safe. There should not be any add calls while advanceClock is executing.
 * It is caller's responsibility to enforce it. Simultaneous add calls are thread-safe.
 * 
 * 
 * Kafka 延时任务的分层时间轮实现，
 * 一个时间轮下有多个作为 Bucket 的双向循环链表 TimerTaskList，
 * 每个 TimerTaskList 存有多个延时任务节点 TimerTaskEntry，
 * 每个 TimerTaskEntry 与唯一的延时任务 TimerTask 相关联
 *
 * @param tickMs tick 一次的时长，类似钟表向前前进一格的时间，第一层时间轮的 tickMs 被固定为了 1 ms
 * @param wheelSize 时间轮上的 Bucket 数量，第一层时间轮的 Bucket 数量是 20
 * @param startMs 时间轮被创建的起始时间戳
 * @param taskCounter 当前层时间轮上的总定时任务数
 * @param queue 将所有 Bucket 按照过期时间排序的延时队列，Kafka 需要依靠这个队列获取并移除那些已过期的 Bucket
 */
@nonthreadsafe
private[timer] class TimingWheel(tickMs: Long, wheelSize: Int, startMs: Long, taskCounter: AtomicInteger, queue: DelayQueue[TimerTaskList]) {

  /**
   * 当前层时间轮的总时长，时长为 tickMs * wheelSize
   */
  private[this] val interval = tickMs * wheelSize
  /**
   * 时间轮下的所有 Bucket 对象
   */
  private[this] val buckets = Array.tabulate[TimerTaskList](wheelSize) { _ => new TimerTaskList(taskCounter) }

  /**
   * 当前时间戳，其值是 小于当前时间的最大 tick 时长的整数倍
   */
  private[this] var currentTime = startMs - (startMs % tickMs) // rounding down to multiple of tickMs

  // overflowWheel can potentially be updated and read by two concurrent threads through add().
  // Therefore, it needs to be volatile due to the issue of Double-Checked Locking pattern with JVM
  /**
   * 时间轮对象，Kafka 是按需创建时间轮的，只有当定时任务在之前的层数放不下时，才会创建下一层时间轮
   */
  @volatile private[this] var overflowWheel: TimingWheel = null

  /**
   * 在本层时间轮的基础上，创建上一层时间轮
   */
  private[this] def addOverflowWheel(): Unit = {
    synchronized {
      // 只有上一层时间轮未创建时才会继续创建
      if (overflowWheel == null) {
        overflowWheel = new TimingWheel(
          // 新一层的 tick 时长 = 下层时间轮的总时长(结合钟表的例子很容易理解)
          tickMs = interval,
          // 每一层的 Bucket 数量都是相同的
          wheelSize = wheelSize,
          startMs = currentTime,
          taskCounter = taskCounter,
          queue
        )
      }
    }
  }

  /**
   * 将定时任务添加到时间轮中
   * @param timerTaskEntry 待添加的定时任务
   * @return
   */
  def add(timerTaskEntry: TimerTaskEntry): Boolean = {
    // 获取定时任务的过期时间
    val expiration = timerTaskEntry.expirationMs
    // 如果定时任务已经被取消了，直接返回
    if (timerTaskEntry.cancelled) {
      // Cancelled
      false
    // 如果定时任务已经过期了，直接返回  
    } else if (expiration < currentTime + tickMs) {
      // Already expired
      false
    // 如果定时任务的超时时间在本层时间轮的覆盖时间范围内，继续执行后续操作 
    } else if (expiration < currentTime + interval) {
      // Put in its own bucket
      // 采用多级时间轮结构，如果第一级时间轮放不下的话，则尝试将其放到下一级时间轮中（粒度更粗）
      // 计算定时任务要被放到哪个 Bucket 中
      val virtualId = expiration / tickMs
      val bucket = buckets((virtualId % wheelSize.toLong).toInt)
      // 将定时任务添加到指定 Bucket
      bucket.add(timerTaskEntry)

      // Set the bucket expiration time
      // 设置 Bucket 的过期时间
      if (bucket.setExpiration(virtualId * tickMs)) {
        // The bucket needs to be enqueued because it was an expired bucket
        // We only need to enqueue the bucket when its expiration time has changed, i.e. the wheel has advanced
        // and the previous buckets gets reused; further calls to set the expiration within the same wheel cycle
        // will pass in the same value and hence return false, thus the bucket with the same expiration will not
        // be enqueued multiple times.
        // 如果过期时间发生变化，说明 Bucket 是新建或是被重用了，需要将其添加到 DelayQueue 中
        queue.offer(bucket)
      }
      true
    // 本层时间轮无法容纳定时任务，交给上层时间轮处理
    } else {
      // Out of the interval. Put it into the parent timer
      // 上层时间轮不存在，需要创建
      if (overflowWheel == null) addOverflowWheel()
      // 重复执行 add 方法，尝试将定时任务添加到上一层时间轮中，如果还放不下则需要继续创建更上层的时间轮，
      // 直至添加成功
      overflowWheel.add(timerTaskEntry)
    }
  }

  // Try to advance the clock
  /**
   * 向前推进时钟，推进时钟的动作是由 Kafka 后台专属的 Reaper 线程发起的。
   * @param timeMs 将时钟推进到 timeMs
   */
  def advanceClock(timeMs: Long): Unit = {
    // 向前推进的 timeMs 必须超过一个 Bucket 的时间范围，不然将没有意义
    if (timeMs >= currentTime + tickMs) {
      currentTime = timeMs - (timeMs % tickMs)

      // Try to advance the clock of the overflow wheel if present
      // 为上一层时间轮做时钟推进
      if (overflowWheel != null) overflowWheel.advanceClock(currentTime)
    }
  }
}
