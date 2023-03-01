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

package org.apache.kafka.common.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.message.OffsetFetchResponseData.*;

public class OffsetFetchResponseDataJsonConverter {
    public static OffsetFetchResponseData read(JsonNode _node, short _version) {
        OffsetFetchResponseData _object = new OffsetFetchResponseData();
        JsonNode _throttleTimeMsNode = _node.get("throttleTimeMs");
        if (_throttleTimeMsNode == null) {
            if (_version >= 3) {
                throw new RuntimeException("OffsetFetchResponseData: unable to locate field 'throttleTimeMs', which is mandatory in version " + _version);
            } else {
                _object.throttleTimeMs = 0;
            }
        } else {
            _object.throttleTimeMs = MessageUtil.jsonNodeToInt(_throttleTimeMsNode, "OffsetFetchResponseData");
        }
        JsonNode _topicsNode = _node.get("topics");
        if (_topicsNode == null) {
            if (_version <= 7) {
                throw new RuntimeException("OffsetFetchResponseData: unable to locate field 'topics', which is mandatory in version " + _version);
            } else {
                _object.topics = new ArrayList<OffsetFetchResponseTopic>(0);
            }
        } else {
            if (!_topicsNode.isArray()) {
                throw new RuntimeException("OffsetFetchResponseData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<OffsetFetchResponseTopic> _collection = new ArrayList<OffsetFetchResponseTopic>(_topicsNode.size());
            _object.topics = _collection;
            for (JsonNode _element : _topicsNode) {
                _collection.add(OffsetFetchResponseTopicJsonConverter.read(_element, _version));
            }
        }
        JsonNode _errorCodeNode = _node.get("errorCode");
        if (_errorCodeNode == null) {
            if ((_version >= 2) && (_version <= 7)) {
                throw new RuntimeException("OffsetFetchResponseData: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = (short) 0;
            }
        } else {
            _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "OffsetFetchResponseData");
        }
        JsonNode _groupsNode = _node.get("groups");
        if (_groupsNode == null) {
            if (_version >= 8) {
                throw new RuntimeException("OffsetFetchResponseData: unable to locate field 'groups', which is mandatory in version " + _version);
            } else {
                _object.groups = new ArrayList<OffsetFetchResponseGroup>(0);
            }
        } else {
            if (!_groupsNode.isArray()) {
                throw new RuntimeException("OffsetFetchResponseData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<OffsetFetchResponseGroup> _collection = new ArrayList<OffsetFetchResponseGroup>(_groupsNode.size());
            _object.groups = _collection;
            for (JsonNode _element : _groupsNode) {
                _collection.add(OffsetFetchResponseGroupJsonConverter.read(_element, _version));
            }
        }
        return _object;
    }
    public static JsonNode write(OffsetFetchResponseData _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        if (_version >= 3) {
            _node.set("throttleTimeMs", new IntNode(_object.throttleTimeMs));
        }
        if (_version <= 7) {
            ArrayNode _topicsArray = new ArrayNode(JsonNodeFactory.instance);
            for (OffsetFetchResponseTopic _element : _object.topics) {
                _topicsArray.add(OffsetFetchResponseTopicJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("topics", _topicsArray);
        } else {
            if (!_object.topics.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topics at version " + _version);
            }
        }
        if ((_version >= 2) && (_version <= 7)) {
            _node.set("errorCode", new ShortNode(_object.errorCode));
        }
        if (_version >= 8) {
            ArrayNode _groupsArray = new ArrayNode(JsonNodeFactory.instance);
            for (OffsetFetchResponseGroup _element : _object.groups) {
                _groupsArray.add(OffsetFetchResponseGroupJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("groups", _groupsArray);
        } else {
            if (!_object.groups.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default groups at version " + _version);
            }
        }
        return _node;
    }
    public static JsonNode write(OffsetFetchResponseData _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class OffsetFetchResponseGroupJsonConverter {
        public static OffsetFetchResponseGroup read(JsonNode _node, short _version) {
            OffsetFetchResponseGroup _object = new OffsetFetchResponseGroup();
            if (_version < 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponseGroup");
            }
            JsonNode _groupIdNode = _node.get("groupId");
            if (_groupIdNode == null) {
                throw new RuntimeException("OffsetFetchResponseGroup: unable to locate field 'groupId', which is mandatory in version " + _version);
            } else {
                if (!_groupIdNode.isTextual()) {
                    throw new RuntimeException("OffsetFetchResponseGroup expected a string type, but got " + _node.getNodeType());
                }
                _object.groupId = _groupIdNode.asText();
            }
            JsonNode _topicsNode = _node.get("topics");
            if (_topicsNode == null) {
                throw new RuntimeException("OffsetFetchResponseGroup: unable to locate field 'topics', which is mandatory in version " + _version);
            } else {
                if (!_topicsNode.isArray()) {
                    throw new RuntimeException("OffsetFetchResponseGroup expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<OffsetFetchResponseTopics> _collection = new ArrayList<OffsetFetchResponseTopics>(_topicsNode.size());
                _object.topics = _collection;
                for (JsonNode _element : _topicsNode) {
                    _collection.add(OffsetFetchResponseTopicsJsonConverter.read(_element, _version));
                }
            }
            JsonNode _errorCodeNode = _node.get("errorCode");
            if (_errorCodeNode == null) {
                throw new RuntimeException("OffsetFetchResponseGroup: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "OffsetFetchResponseGroup");
            }
            return _object;
        }
        public static JsonNode write(OffsetFetchResponseGroup _object, short _version, boolean _serializeRecords) {
            if (_version < 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponseGroup");
            }
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("groupId", new TextNode(_object.groupId));
            ArrayNode _topicsArray = new ArrayNode(JsonNodeFactory.instance);
            for (OffsetFetchResponseTopics _element : _object.topics) {
                _topicsArray.add(OffsetFetchResponseTopicsJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("topics", _topicsArray);
            _node.set("errorCode", new ShortNode(_object.errorCode));
            return _node;
        }
        public static JsonNode write(OffsetFetchResponseGroup _object, short _version) {
            return write(_object, _version, true);
        }
    }
    
    public static class OffsetFetchResponsePartitionJsonConverter {
        public static OffsetFetchResponsePartition read(JsonNode _node, short _version) {
            OffsetFetchResponsePartition _object = new OffsetFetchResponsePartition();
            JsonNode _partitionIndexNode = _node.get("partitionIndex");
            if (_partitionIndexNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartition: unable to locate field 'partitionIndex', which is mandatory in version " + _version);
            } else {
                _object.partitionIndex = MessageUtil.jsonNodeToInt(_partitionIndexNode, "OffsetFetchResponsePartition");
            }
            JsonNode _committedOffsetNode = _node.get("committedOffset");
            if (_committedOffsetNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartition: unable to locate field 'committedOffset', which is mandatory in version " + _version);
            } else {
                _object.committedOffset = MessageUtil.jsonNodeToLong(_committedOffsetNode, "OffsetFetchResponsePartition");
            }
            JsonNode _committedLeaderEpochNode = _node.get("committedLeaderEpoch");
            if (_committedLeaderEpochNode == null) {
                if (_version >= 5) {
                    throw new RuntimeException("OffsetFetchResponsePartition: unable to locate field 'committedLeaderEpoch', which is mandatory in version " + _version);
                } else {
                    _object.committedLeaderEpoch = -1;
                }
            } else {
                _object.committedLeaderEpoch = MessageUtil.jsonNodeToInt(_committedLeaderEpochNode, "OffsetFetchResponsePartition");
            }
            JsonNode _metadataNode = _node.get("metadata");
            if (_metadataNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartition: unable to locate field 'metadata', which is mandatory in version " + _version);
            } else {
                if (_metadataNode.isNull()) {
                    _object.metadata = null;
                } else {
                    if (!_metadataNode.isTextual()) {
                        throw new RuntimeException("OffsetFetchResponsePartition expected a string type, but got " + _node.getNodeType());
                    }
                    _object.metadata = _metadataNode.asText();
                }
            }
            JsonNode _errorCodeNode = _node.get("errorCode");
            if (_errorCodeNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartition: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "OffsetFetchResponsePartition");
            }
            return _object;
        }
        public static JsonNode write(OffsetFetchResponsePartition _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("partitionIndex", new IntNode(_object.partitionIndex));
            _node.set("committedOffset", new LongNode(_object.committedOffset));
            if (_version >= 5) {
                _node.set("committedLeaderEpoch", new IntNode(_object.committedLeaderEpoch));
            }
            if (_object.metadata == null) {
                _node.set("metadata", NullNode.instance);
            } else {
                _node.set("metadata", new TextNode(_object.metadata));
            }
            _node.set("errorCode", new ShortNode(_object.errorCode));
            return _node;
        }
        public static JsonNode write(OffsetFetchResponsePartition _object, short _version) {
            return write(_object, _version, true);
        }
    }
    
    public static class OffsetFetchResponsePartitionsJsonConverter {
        public static OffsetFetchResponsePartitions read(JsonNode _node, short _version) {
            OffsetFetchResponsePartitions _object = new OffsetFetchResponsePartitions();
            JsonNode _partitionIndexNode = _node.get("partitionIndex");
            if (_partitionIndexNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartitions: unable to locate field 'partitionIndex', which is mandatory in version " + _version);
            } else {
                _object.partitionIndex = MessageUtil.jsonNodeToInt(_partitionIndexNode, "OffsetFetchResponsePartitions");
            }
            JsonNode _committedOffsetNode = _node.get("committedOffset");
            if (_committedOffsetNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartitions: unable to locate field 'committedOffset', which is mandatory in version " + _version);
            } else {
                _object.committedOffset = MessageUtil.jsonNodeToLong(_committedOffsetNode, "OffsetFetchResponsePartitions");
            }
            JsonNode _committedLeaderEpochNode = _node.get("committedLeaderEpoch");
            if (_committedLeaderEpochNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartitions: unable to locate field 'committedLeaderEpoch', which is mandatory in version " + _version);
            } else {
                _object.committedLeaderEpoch = MessageUtil.jsonNodeToInt(_committedLeaderEpochNode, "OffsetFetchResponsePartitions");
            }
            JsonNode _metadataNode = _node.get("metadata");
            if (_metadataNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartitions: unable to locate field 'metadata', which is mandatory in version " + _version);
            } else {
                if (_metadataNode.isNull()) {
                    _object.metadata = null;
                } else {
                    if (!_metadataNode.isTextual()) {
                        throw new RuntimeException("OffsetFetchResponsePartitions expected a string type, but got " + _node.getNodeType());
                    }
                    _object.metadata = _metadataNode.asText();
                }
            }
            JsonNode _errorCodeNode = _node.get("errorCode");
            if (_errorCodeNode == null) {
                throw new RuntimeException("OffsetFetchResponsePartitions: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "OffsetFetchResponsePartitions");
            }
            return _object;
        }
        public static JsonNode write(OffsetFetchResponsePartitions _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("partitionIndex", new IntNode(_object.partitionIndex));
            _node.set("committedOffset", new LongNode(_object.committedOffset));
            _node.set("committedLeaderEpoch", new IntNode(_object.committedLeaderEpoch));
            if (_object.metadata == null) {
                _node.set("metadata", NullNode.instance);
            } else {
                _node.set("metadata", new TextNode(_object.metadata));
            }
            _node.set("errorCode", new ShortNode(_object.errorCode));
            return _node;
        }
        public static JsonNode write(OffsetFetchResponsePartitions _object, short _version) {
            return write(_object, _version, true);
        }
    }
    
    public static class OffsetFetchResponseTopicJsonConverter {
        public static OffsetFetchResponseTopic read(JsonNode _node, short _version) {
            OffsetFetchResponseTopic _object = new OffsetFetchResponseTopic();
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponseTopic");
            }
            JsonNode _nameNode = _node.get("name");
            if (_nameNode == null) {
                throw new RuntimeException("OffsetFetchResponseTopic: unable to locate field 'name', which is mandatory in version " + _version);
            } else {
                if (!_nameNode.isTextual()) {
                    throw new RuntimeException("OffsetFetchResponseTopic expected a string type, but got " + _node.getNodeType());
                }
                _object.name = _nameNode.asText();
            }
            JsonNode _partitionsNode = _node.get("partitions");
            if (_partitionsNode == null) {
                throw new RuntimeException("OffsetFetchResponseTopic: unable to locate field 'partitions', which is mandatory in version " + _version);
            } else {
                if (!_partitionsNode.isArray()) {
                    throw new RuntimeException("OffsetFetchResponseTopic expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<OffsetFetchResponsePartition> _collection = new ArrayList<OffsetFetchResponsePartition>(_partitionsNode.size());
                _object.partitions = _collection;
                for (JsonNode _element : _partitionsNode) {
                    _collection.add(OffsetFetchResponsePartitionJsonConverter.read(_element, _version));
                }
            }
            return _object;
        }
        public static JsonNode write(OffsetFetchResponseTopic _object, short _version, boolean _serializeRecords) {
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponseTopic");
            }
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("name", new TextNode(_object.name));
            ArrayNode _partitionsArray = new ArrayNode(JsonNodeFactory.instance);
            for (OffsetFetchResponsePartition _element : _object.partitions) {
                _partitionsArray.add(OffsetFetchResponsePartitionJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("partitions", _partitionsArray);
            return _node;
        }
        public static JsonNode write(OffsetFetchResponseTopic _object, short _version) {
            return write(_object, _version, true);
        }
    }
    
    public static class OffsetFetchResponseTopicsJsonConverter {
        public static OffsetFetchResponseTopics read(JsonNode _node, short _version) {
            OffsetFetchResponseTopics _object = new OffsetFetchResponseTopics();
            JsonNode _nameNode = _node.get("name");
            if (_nameNode == null) {
                throw new RuntimeException("OffsetFetchResponseTopics: unable to locate field 'name', which is mandatory in version " + _version);
            } else {
                if (!_nameNode.isTextual()) {
                    throw new RuntimeException("OffsetFetchResponseTopics expected a string type, but got " + _node.getNodeType());
                }
                _object.name = _nameNode.asText();
            }
            JsonNode _partitionsNode = _node.get("partitions");
            if (_partitionsNode == null) {
                throw new RuntimeException("OffsetFetchResponseTopics: unable to locate field 'partitions', which is mandatory in version " + _version);
            } else {
                if (!_partitionsNode.isArray()) {
                    throw new RuntimeException("OffsetFetchResponseTopics expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<OffsetFetchResponsePartitions> _collection = new ArrayList<OffsetFetchResponsePartitions>(_partitionsNode.size());
                _object.partitions = _collection;
                for (JsonNode _element : _partitionsNode) {
                    _collection.add(OffsetFetchResponsePartitionsJsonConverter.read(_element, _version));
                }
            }
            return _object;
        }
        public static JsonNode write(OffsetFetchResponseTopics _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("name", new TextNode(_object.name));
            ArrayNode _partitionsArray = new ArrayNode(JsonNodeFactory.instance);
            for (OffsetFetchResponsePartitions _element : _object.partitions) {
                _partitionsArray.add(OffsetFetchResponsePartitionsJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("partitions", _partitionsArray);
            return _node;
        }
        public static JsonNode write(OffsetFetchResponseTopics _object, short _version) {
            return write(_object, _version, true);
        }
    }
}
