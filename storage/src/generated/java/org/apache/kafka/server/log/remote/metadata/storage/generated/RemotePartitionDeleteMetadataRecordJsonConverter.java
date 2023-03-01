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

package  org.apache.kafka.server.log.remote.metadata.storage.generated;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.protocol.MessageUtil;

import static  org.apache.kafka.server.log.remote.metadata.storage.generated.RemotePartitionDeleteMetadataRecord.*;

public class RemotePartitionDeleteMetadataRecordJsonConverter {
    public static RemotePartitionDeleteMetadataRecord read(JsonNode _node, short _version) {
        RemotePartitionDeleteMetadataRecord _object = new RemotePartitionDeleteMetadataRecord();
        JsonNode _topicIdPartitionNode = _node.get("topicIdPartition");
        if (_topicIdPartitionNode == null) {
            throw new RuntimeException("RemotePartitionDeleteMetadataRecord: unable to locate field 'topicIdPartition', which is mandatory in version " + _version);
        } else {
            _object.topicIdPartition = TopicIdPartitionEntryJsonConverter.read(_topicIdPartitionNode, _version);
        }
        JsonNode _brokerIdNode = _node.get("brokerId");
        if (_brokerIdNode == null) {
            throw new RuntimeException("RemotePartitionDeleteMetadataRecord: unable to locate field 'brokerId', which is mandatory in version " + _version);
        } else {
            _object.brokerId = MessageUtil.jsonNodeToInt(_brokerIdNode, "RemotePartitionDeleteMetadataRecord");
        }
        JsonNode _eventTimestampMsNode = _node.get("eventTimestampMs");
        if (_eventTimestampMsNode == null) {
            throw new RuntimeException("RemotePartitionDeleteMetadataRecord: unable to locate field 'eventTimestampMs', which is mandatory in version " + _version);
        } else {
            _object.eventTimestampMs = MessageUtil.jsonNodeToLong(_eventTimestampMsNode, "RemotePartitionDeleteMetadataRecord");
        }
        JsonNode _remotePartitionDeleteStateNode = _node.get("remotePartitionDeleteState");
        if (_remotePartitionDeleteStateNode == null) {
            throw new RuntimeException("RemotePartitionDeleteMetadataRecord: unable to locate field 'remotePartitionDeleteState', which is mandatory in version " + _version);
        } else {
            _object.remotePartitionDeleteState = MessageUtil.jsonNodeToByte(_remotePartitionDeleteStateNode, "RemotePartitionDeleteMetadataRecord");
        }
        return _object;
    }
    public static JsonNode write(RemotePartitionDeleteMetadataRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("topicIdPartition", TopicIdPartitionEntryJsonConverter.write(_object.topicIdPartition, _version, _serializeRecords));
        _node.set("brokerId", new IntNode(_object.brokerId));
        _node.set("eventTimestampMs", new LongNode(_object.eventTimestampMs));
        _node.set("remotePartitionDeleteState", new ShortNode(_object.remotePartitionDeleteState));
        return _node;
    }
    public static JsonNode write(RemotePartitionDeleteMetadataRecord _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class TopicIdPartitionEntryJsonConverter {
        public static TopicIdPartitionEntry read(JsonNode _node, short _version) {
            TopicIdPartitionEntry _object = new TopicIdPartitionEntry();
            JsonNode _nameNode = _node.get("name");
            if (_nameNode == null) {
                throw new RuntimeException("TopicIdPartitionEntry: unable to locate field 'name', which is mandatory in version " + _version);
            } else {
                if (!_nameNode.isTextual()) {
                    throw new RuntimeException("TopicIdPartitionEntry expected a string type, but got " + _node.getNodeType());
                }
                _object.name = _nameNode.asText();
            }
            JsonNode _idNode = _node.get("id");
            if (_idNode == null) {
                throw new RuntimeException("TopicIdPartitionEntry: unable to locate field 'id', which is mandatory in version " + _version);
            } else {
                if (!_idNode.isTextual()) {
                    throw new RuntimeException("TopicIdPartitionEntry expected a JSON string type, but got " + _node.getNodeType());
                }
                _object.id = Uuid.fromString(_idNode.asText());
            }
            JsonNode _partitionNode = _node.get("partition");
            if (_partitionNode == null) {
                throw new RuntimeException("TopicIdPartitionEntry: unable to locate field 'partition', which is mandatory in version " + _version);
            } else {
                _object.partition = MessageUtil.jsonNodeToInt(_partitionNode, "TopicIdPartitionEntry");
            }
            return _object;
        }
        public static JsonNode write(TopicIdPartitionEntry _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("name", new TextNode(_object.name));
            _node.set("id", new TextNode(_object.id.toString()));
            _node.set("partition", new IntNode(_object.partition));
            return _node;
        }
        public static JsonNode write(TopicIdPartitionEntry _object, short _version) {
            return write(_object, _version, true);
        }
    }
}
