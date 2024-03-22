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

package kafka.server

import kafka.log.Log
import org.apache.kafka.common.KafkaException

object LogOffsetMetadata {
  val UnknownOffsetMetadata = LogOffsetMetadata(-1, 0, 0)
  val UnknownFilePosition = -1

  class OffsetOrdering extends Ordering[LogOffsetMetadata] {
    override def compare(x: LogOffsetMetadata, y: LogOffsetMetadata): Int = {
      x.offsetDiff(y).toInt
    }
  }

}

/**
 * A log offset structure, including:
 *  1. the message offset
 *  2. the base message offset of the located segment
 *  3. the physical position on the located segment
 *
 * 负责维护消息位移元数据的 POJO 类
 *
 * @param messageOffset 消息位移值，高水位对应的就是这个值
 * @param segmentBaseOffset 位移值所在 LogSegment 的起始位移，用于辅助计算两条消息在物理磁盘文件中位置的差值（相隔多少字节）。
 *                          这个计算有个前提条件：两条消息必须在同一个 LogSegment 对象上，否则计算没有任何意义。
 *                          因此，该字段可以判断两条消息是否属于同一个 LogSegment
 * @param relativePositionInSegment 位移值所在 LogSegment 的物理磁盘位置，用于计算两个位移值之间的物理磁盘位置差值。
 *                                  可以用来判断读取日志时的数据大小限制
 */
case class LogOffsetMetadata(messageOffset: Long,
                             segmentBaseOffset: Long = Log.UnknownOffset,
                             relativePositionInSegment: Int = LogOffsetMetadata.UnknownFilePosition) {

  /**
   * check if this offset is already on an older segment compared with the given offset
   *
   * 判断给定的 LogOffsetMetadata 是否在新的 LogSegment 上
   *
   * @param that
   * @return
   */
  def onOlderSegment(that: LogOffsetMetadata): Boolean = {
    if (messageOffsetOnly)
      throw new KafkaException(s"$this cannot compare its segment info with $that since it only has message offset info")

    this.segmentBaseOffset < that.segmentBaseOffset
  }


  /**
   * check if this offset is on the same segment with the given offset
   * 判断两个给定的 LogOffsetMetadata 是否在同一个 LogSegment 上
   *
   * @param that
   * @return
   */
  def onSameSegment(that: LogOffsetMetadata): Boolean = {
    if (messageOffsetOnly)
      throw new KafkaException(s"$this cannot compare its segment info with $that since it only has message offset info")
    // 根据 segmentBaseOffset 判断，如果二者相等，则意味着在同一个 LogSegment 上
    this.segmentBaseOffset == that.segmentBaseOffset
  }


  /**
   * compute the number of messages between this offset to the given offset
   *
   * 计算两个给定 LogOffsetMetadata 位移之间的差值
   * @param that
   * @return
   */
  def offsetDiff(that: LogOffsetMetadata): Long = {
    this.messageOffset - that.messageOffset
  }

  /**
   * compute the number of bytes between this offset to the given offset
   * if they are on the same segment and this offset precedes the given offset
   * 计算两个给定 LogOffsetMetadata 之间在物理磁盘上相差多少字节
   *
   * @param that
   * @return
   */
  def positionDiff(that: LogOffsetMetadata): Int = {
    // 二者必须在同一个 LogSegment 上，计算差值才有意义
    if (!onSameSegment(that))
      throw new KafkaException(s"$this cannot compare its segment position with $that since they are not on the same segment")
    // 计算所需要的信息必须存在
    if (messageOffsetOnly)
      throw new KafkaException(s"$this cannot compare its segment position with $that since it only has message offset info")

    this.relativePositionInSegment - that.relativePositionInSegment
  }

  /**
   * decide if the offset metadata only contains message offset info
   * 判断 LogOffsetMetadata 是否只包含消息位移值信息
   *
   * @return
   */
  def messageOffsetOnly: Boolean = {
    segmentBaseOffset == Log.UnknownOffset && relativePositionInSegment == LogOffsetMetadata.UnknownFilePosition
  }

  override def toString = s"(offset=$messageOffset segment=[$segmentBaseOffset:$relativePositionInSegment])"

}
