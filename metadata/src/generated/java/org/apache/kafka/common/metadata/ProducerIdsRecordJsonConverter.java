/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// THIS CODE IS AUTOMATICALLY GENERATED.  DO NOT EDIT.

package org.apache.kafka.common.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.ProducerIdsRecord.*;

public class ProducerIdsRecordJsonConverter {
    public static ProducerIdsRecord read(JsonNode _node, short _version) {
        ProducerIdsRecord _object = new ProducerIdsRecord();
        JsonNode _brokerIdNode = _node.get("brokerId");
        if (_brokerIdNode == null) {
            throw new RuntimeException("ProducerIdsRecord: unable to locate field 'brokerId', which is mandatory in version " + _version);
        } else {
            _object.brokerId = MessageUtil.jsonNodeToInt(_brokerIdNode, "ProducerIdsRecord");
        }
        JsonNode _brokerEpochNode = _node.get("brokerEpoch");
        if (_brokerEpochNode == null) {
            throw new RuntimeException("ProducerIdsRecord: unable to locate field 'brokerEpoch', which is mandatory in version " + _version);
        } else {
            _object.brokerEpoch = MessageUtil.jsonNodeToLong(_brokerEpochNode, "ProducerIdsRecord");
        }
        JsonNode _producerIdsEndNode = _node.get("producerIdsEnd");
        if (_producerIdsEndNode == null) {
            throw new RuntimeException("ProducerIdsRecord: unable to locate field 'producerIdsEnd', which is mandatory in version " + _version);
        } else {
            _object.producerIdsEnd = MessageUtil.jsonNodeToLong(_producerIdsEndNode, "ProducerIdsRecord");
        }
        return _object;
    }
    public static JsonNode write(ProducerIdsRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("brokerId", new IntNode(_object.brokerId));
        _node.set("brokerEpoch", new LongNode(_object.brokerEpoch));
        _node.set("producerIdsEnd", new LongNode(_object.producerIdsEnd));
        return _node;
    }
    public static JsonNode write(ProducerIdsRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
