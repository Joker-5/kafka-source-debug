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

import static  org.apache.kafka.server.log.remote.metadata.storage.generated.RemoteLogSegmentMetadataUpdateRecord.*;

public class RemoteLogSegmentMetadataUpdateRecordJsonConverter {
    public static RemoteLogSegmentMetadataUpdateRecord read(JsonNode _node, short _version) {
        RemoteLogSegmentMetadataUpdateRecord _object = new RemoteLogSegmentMetadataUpdateRecord();
        JsonNode _remoteLogSegmentIdNode = _node.get("remoteLogSegmentId");
        if (_remoteLogSegmentIdNode == null) {
            throw new RuntimeException("RemoteLogSegmentMetadataUpdateRecord: unable to locate field 'remoteLogSegmentId', which is mandatory in version " + _version);
        } else {
            _object.remoteLogSegmentId = RemoteLogSegmentIdEntryJsonConverter.read(_remoteLogSegmentIdNode, _version);
        }
        JsonNode _brokerIdNode = _node.get("brokerId");
        if (_brokerIdNode == null) {
            throw new RuntimeException("RemoteLogSegmentMetadataUpdateRecord: unable to locate field 'brokerId', which is mandatory in version " + _version);
        } else {
            _object.brokerId = MessageUtil.jsonNodeToInt(_brokerIdNode, "RemoteLogSegmentMetadataUpdateRecord");
        }
        JsonNode _eventTimestampMsNode = _node.get("eventTimestampMs");
        if (_eventTimestampMsNode == null) {
            throw new RuntimeException("RemoteLogSegmentMetadataUpdateRecord: unable to locate field 'eventTimestampMs', which is mandatory in version " + _version);
        } else {
            _object.eventTimestampMs = MessageUtil.jsonNodeToLong(_eventTimestampMsNode, "RemoteLogSegmentMetadataUpdateRecord");
        }
        JsonNode _remoteLogSegmentStateNode = _node.get("remoteLogSegmentState");
        if (_remoteLogSegmentStateNode == null) {
            throw new RuntimeException("RemoteLogSegmentMetadataUpdateRecord: unable to locate field 'remoteLogSegmentState', which is mandatory in version " + _version);
        } else {
            _object.remoteLogSegmentState = MessageUtil.jsonNodeToByte(_remoteLogSegmentStateNode, "RemoteLogSegmentMetadataUpdateRecord");
        }
        return _object;
    }
    public static JsonNode write(RemoteLogSegmentMetadataUpdateRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("remoteLogSegmentId", RemoteLogSegmentIdEntryJsonConverter.write(_object.remoteLogSegmentId, _version, _serializeRecords));
        _node.set("brokerId", new IntNode(_object.brokerId));
        _node.set("eventTimestampMs", new LongNode(_object.eventTimestampMs));
        _node.set("remoteLogSegmentState", new ShortNode(_object.remoteLogSegmentState));
        return _node;
    }
    public static JsonNode write(RemoteLogSegmentMetadataUpdateRecord _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class RemoteLogSegmentIdEntryJsonConverter {
        public static RemoteLogSegmentIdEntry read(JsonNode _node, short _version) {
            RemoteLogSegmentIdEntry _object = new RemoteLogSegmentIdEntry();
            JsonNode _topicIdPartitionNode = _node.get("topicIdPartition");
            if (_topicIdPartitionNode == null) {
                throw new RuntimeException("RemoteLogSegmentIdEntry: unable to locate field 'topicIdPartition', which is mandatory in version " + _version);
            } else {
                _object.topicIdPartition = TopicIdPartitionEntryJsonConverter.read(_topicIdPartitionNode, _version);
            }
            JsonNode _idNode = _node.get("id");
            if (_idNode == null) {
                throw new RuntimeException("RemoteLogSegmentIdEntry: unable to locate field 'id', which is mandatory in version " + _version);
            } else {
                if (!_idNode.isTextual()) {
                    throw new RuntimeException("RemoteLogSegmentIdEntry expected a JSON string type, but got " + _node.getNodeType());
                }
                _object.id = Uuid.fromString(_idNode.asText());
            }
            return _object;
        }
        public static JsonNode write(RemoteLogSegmentIdEntry _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("topicIdPartition", TopicIdPartitionEntryJsonConverter.write(_object.topicIdPartition, _version, _serializeRecords));
            _node.set("id", new TextNode(_object.id.toString()));
            return _node;
        }
        public static JsonNode write(RemoteLogSegmentIdEntry _object, short _version) {
            return write(_object, _version, true);
        }
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
