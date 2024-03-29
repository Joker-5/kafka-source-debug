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

package kafka.api

object Request {
  val OrdinaryConsumerId: Int = -1
  val DebuggingConsumerId: Int = -2
  val FutureLocalReplicaId: Int = -3
  
  
  /**
   * Broker ids are non-negative int.
   * 
   * 判断 Broker ID 是否合法，一个合法的 BrokerId 必须 >= 0
   * @param brokerId
   * @return
   */
  def isValidBrokerId(brokerId: Int): Boolean = brokerId >= 0

  def describeReplicaId(replicaId: Int): String = {
    replicaId match {
      case OrdinaryConsumerId => "consumer"
      case DebuggingConsumerId => "debug consumer"
      case FutureLocalReplicaId => "future local replica"
      case id if isValidBrokerId(id) => s"replica [$id]"
      case id => s"invalid replica [$id]"
    }
  }
}
