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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.PartitionChangeRecord.*;

public class PartitionChangeRecordJsonConverter {
    public static PartitionChangeRecord read(JsonNode _node, short _version) {
        PartitionChangeRecord _object = new PartitionChangeRecord();
        JsonNode _partitionIdNode = _node.get("partitionId");
        if (_partitionIdNode == null) {
            throw new RuntimeException("PartitionChangeRecord: unable to locate field 'partitionId', which is mandatory in version " + _version);
        } else {
            _object.partitionId = MessageUtil.jsonNodeToInt(_partitionIdNode, "PartitionChangeRecord");
        }
        JsonNode _topicIdNode = _node.get("topicId");
        if (_topicIdNode == null) {
            throw new RuntimeException("PartitionChangeRecord: unable to locate field 'topicId', which is mandatory in version " + _version);
        } else {
            if (!_topicIdNode.isTextual()) {
                throw new RuntimeException("PartitionChangeRecord expected a JSON string type, but got " + _node.getNodeType());
            }
            _object.topicId = Uuid.fromString(_topicIdNode.asText());
        }
        JsonNode _isrNode = _node.get("isr");
        if (_isrNode == null) {
            _object.isr = null;
        } else {
            if (_isrNode.isNull()) {
                _object.isr = null;
            } else {
                if (!_isrNode.isArray()) {
                    throw new RuntimeException("PartitionChangeRecord expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<Integer> _collection = new ArrayList<Integer>(_isrNode.size());
                _object.isr = _collection;
                for (JsonNode _element : _isrNode) {
                    _collection.add(MessageUtil.jsonNodeToInt(_element, "PartitionChangeRecord element"));
                }
            }
        }
        JsonNode _leaderNode = _node.get("leader");
        if (_leaderNode == null) {
            _object.leader = -2;
        } else {
            _object.leader = MessageUtil.jsonNodeToInt(_leaderNode, "PartitionChangeRecord");
        }
        JsonNode _replicasNode = _node.get("replicas");
        if (_replicasNode == null) {
            _object.replicas = null;
        } else {
            if (_replicasNode.isNull()) {
                _object.replicas = null;
            } else {
                if (!_replicasNode.isArray()) {
                    throw new RuntimeException("PartitionChangeRecord expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<Integer> _collection = new ArrayList<Integer>(_replicasNode.size());
                _object.replicas = _collection;
                for (JsonNode _element : _replicasNode) {
                    _collection.add(MessageUtil.jsonNodeToInt(_element, "PartitionChangeRecord element"));
                }
            }
        }
        JsonNode _removingReplicasNode = _node.get("removingReplicas");
        if (_removingReplicasNode == null) {
            _object.removingReplicas = null;
        } else {
            if (_removingReplicasNode.isNull()) {
                _object.removingReplicas = null;
            } else {
                if (!_removingReplicasNode.isArray()) {
                    throw new RuntimeException("PartitionChangeRecord expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<Integer> _collection = new ArrayList<Integer>(_removingReplicasNode.size());
                _object.removingReplicas = _collection;
                for (JsonNode _element : _removingReplicasNode) {
                    _collection.add(MessageUtil.jsonNodeToInt(_element, "PartitionChangeRecord element"));
                }
            }
        }
        JsonNode _addingReplicasNode = _node.get("addingReplicas");
        if (_addingReplicasNode == null) {
            _object.addingReplicas = null;
        } else {
            if (_addingReplicasNode.isNull()) {
                _object.addingReplicas = null;
            } else {
                if (!_addingReplicasNode.isArray()) {
                    throw new RuntimeException("PartitionChangeRecord expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<Integer> _collection = new ArrayList<Integer>(_addingReplicasNode.size());
                _object.addingReplicas = _collection;
                for (JsonNode _element : _addingReplicasNode) {
                    _collection.add(MessageUtil.jsonNodeToInt(_element, "PartitionChangeRecord element"));
                }
            }
        }
        return _object;
    }
    public static JsonNode write(PartitionChangeRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("partitionId", new IntNode(_object.partitionId));
        _node.set("topicId", new TextNode(_object.topicId.toString()));
        if (_object.isr != null) {
            ArrayNode _isrArray = new ArrayNode(JsonNodeFactory.instance);
            for (Integer _element : _object.isr) {
                _isrArray.add(new IntNode(_element));
            }
            _node.set("isr", _isrArray);
        }
        if (_object.leader != -2) {
            _node.set("leader", new IntNode(_object.leader));
        }
        if (_object.replicas != null) {
            ArrayNode _replicasArray = new ArrayNode(JsonNodeFactory.instance);
            for (Integer _element : _object.replicas) {
                _replicasArray.add(new IntNode(_element));
            }
            _node.set("replicas", _replicasArray);
        }
        if (_object.removingReplicas != null) {
            ArrayNode _removingReplicasArray = new ArrayNode(JsonNodeFactory.instance);
            for (Integer _element : _object.removingReplicas) {
                _removingReplicasArray.add(new IntNode(_element));
            }
            _node.set("removingReplicas", _removingReplicasArray);
        }
        if (_object.addingReplicas != null) {
            ArrayNode _addingReplicasArray = new ArrayNode(JsonNodeFactory.instance);
            for (Integer _element : _object.addingReplicas) {
                _addingReplicasArray.add(new IntNode(_element));
            }
            _node.set("addingReplicas", _addingReplicasArray);
        }
        return _node;
    }
    public static JsonNode write(PartitionChangeRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
