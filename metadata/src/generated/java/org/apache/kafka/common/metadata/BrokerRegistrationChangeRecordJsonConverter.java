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
import com.fasterxml.jackson.databind.node.ShortNode;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.BrokerRegistrationChangeRecord.*;

public class BrokerRegistrationChangeRecordJsonConverter {
    public static BrokerRegistrationChangeRecord read(JsonNode _node, short _version) {
        BrokerRegistrationChangeRecord _object = new BrokerRegistrationChangeRecord();
        JsonNode _brokerIdNode = _node.get("brokerId");
        if (_brokerIdNode == null) {
            throw new RuntimeException("BrokerRegistrationChangeRecord: unable to locate field 'brokerId', which is mandatory in version " + _version);
        } else {
            _object.brokerId = MessageUtil.jsonNodeToInt(_brokerIdNode, "BrokerRegistrationChangeRecord");
        }
        JsonNode _brokerEpochNode = _node.get("brokerEpoch");
        if (_brokerEpochNode == null) {
            throw new RuntimeException("BrokerRegistrationChangeRecord: unable to locate field 'brokerEpoch', which is mandatory in version " + _version);
        } else {
            _object.brokerEpoch = MessageUtil.jsonNodeToLong(_brokerEpochNode, "BrokerRegistrationChangeRecord");
        }
        JsonNode _fencedNode = _node.get("fenced");
        if (_fencedNode == null) {
            _object.fenced = (byte) 0;
        } else {
            _object.fenced = MessageUtil.jsonNodeToByte(_fencedNode, "BrokerRegistrationChangeRecord");
        }
        return _object;
    }
    public static JsonNode write(BrokerRegistrationChangeRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("brokerId", new IntNode(_object.brokerId));
        _node.set("brokerEpoch", new LongNode(_object.brokerEpoch));
        if (_object.fenced != (byte) 0) {
            _node.set("fenced", new ShortNode(_object.fenced));
        }
        return _node;
    }
    public static JsonNode write(BrokerRegistrationChangeRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
