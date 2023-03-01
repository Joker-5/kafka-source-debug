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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.message.DescribeTransactionsResponseData.*;

public class DescribeTransactionsResponseDataJsonConverter {
    public static DescribeTransactionsResponseData read(JsonNode _node, short _version) {
        DescribeTransactionsResponseData _object = new DescribeTransactionsResponseData();
        JsonNode _throttleTimeMsNode = _node.get("throttleTimeMs");
        if (_throttleTimeMsNode == null) {
            throw new RuntimeException("DescribeTransactionsResponseData: unable to locate field 'throttleTimeMs', which is mandatory in version " + _version);
        } else {
            _object.throttleTimeMs = MessageUtil.jsonNodeToInt(_throttleTimeMsNode, "DescribeTransactionsResponseData");
        }
        JsonNode _transactionStatesNode = _node.get("transactionStates");
        if (_transactionStatesNode == null) {
            throw new RuntimeException("DescribeTransactionsResponseData: unable to locate field 'transactionStates', which is mandatory in version " + _version);
        } else {
            if (!_transactionStatesNode.isArray()) {
                throw new RuntimeException("DescribeTransactionsResponseData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<TransactionState> _collection = new ArrayList<TransactionState>(_transactionStatesNode.size());
            _object.transactionStates = _collection;
            for (JsonNode _element : _transactionStatesNode) {
                _collection.add(TransactionStateJsonConverter.read(_element, _version));
            }
        }
        return _object;
    }
    public static JsonNode write(DescribeTransactionsResponseData _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("throttleTimeMs", new IntNode(_object.throttleTimeMs));
        ArrayNode _transactionStatesArray = new ArrayNode(JsonNodeFactory.instance);
        for (TransactionState _element : _object.transactionStates) {
            _transactionStatesArray.add(TransactionStateJsonConverter.write(_element, _version, _serializeRecords));
        }
        _node.set("transactionStates", _transactionStatesArray);
        return _node;
    }
    public static JsonNode write(DescribeTransactionsResponseData _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class TopicDataJsonConverter {
        public static TopicData read(JsonNode _node, short _version) {
            TopicData _object = new TopicData();
            JsonNode _topicNode = _node.get("topic");
            if (_topicNode == null) {
                throw new RuntimeException("TopicData: unable to locate field 'topic', which is mandatory in version " + _version);
            } else {
                if (!_topicNode.isTextual()) {
                    throw new RuntimeException("TopicData expected a string type, but got " + _node.getNodeType());
                }
                _object.topic = _topicNode.asText();
            }
            JsonNode _partitionsNode = _node.get("partitions");
            if (_partitionsNode == null) {
                throw new RuntimeException("TopicData: unable to locate field 'partitions', which is mandatory in version " + _version);
            } else {
                if (!_partitionsNode.isArray()) {
                    throw new RuntimeException("TopicData expected a JSON array, but got " + _node.getNodeType());
                }
                ArrayList<Integer> _collection = new ArrayList<Integer>(_partitionsNode.size());
                _object.partitions = _collection;
                for (JsonNode _element : _partitionsNode) {
                    _collection.add(MessageUtil.jsonNodeToInt(_element, "TopicData element"));
                }
            }
            return _object;
        }
        public static JsonNode write(TopicData _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("topic", new TextNode(_object.topic));
            ArrayNode _partitionsArray = new ArrayNode(JsonNodeFactory.instance);
            for (Integer _element : _object.partitions) {
                _partitionsArray.add(new IntNode(_element));
            }
            _node.set("partitions", _partitionsArray);
            return _node;
        }
        public static JsonNode write(TopicData _object, short _version) {
            return write(_object, _version, true);
        }
    }
    
    public static class TransactionStateJsonConverter {
        public static TransactionState read(JsonNode _node, short _version) {
            TransactionState _object = new TransactionState();
            JsonNode _errorCodeNode = _node.get("errorCode");
            if (_errorCodeNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "TransactionState");
            }
            JsonNode _transactionalIdNode = _node.get("transactionalId");
            if (_transactionalIdNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'transactionalId', which is mandatory in version " + _version);
            } else {
                if (!_transactionalIdNode.isTextual()) {
                    throw new RuntimeException("TransactionState expected a string type, but got " + _node.getNodeType());
                }
                _object.transactionalId = _transactionalIdNode.asText();
            }
            JsonNode _transactionStateNode = _node.get("transactionState");
            if (_transactionStateNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'transactionState', which is mandatory in version " + _version);
            } else {
                if (!_transactionStateNode.isTextual()) {
                    throw new RuntimeException("TransactionState expected a string type, but got " + _node.getNodeType());
                }
                _object.transactionState = _transactionStateNode.asText();
            }
            JsonNode _transactionTimeoutMsNode = _node.get("transactionTimeoutMs");
            if (_transactionTimeoutMsNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'transactionTimeoutMs', which is mandatory in version " + _version);
            } else {
                _object.transactionTimeoutMs = MessageUtil.jsonNodeToInt(_transactionTimeoutMsNode, "TransactionState");
            }
            JsonNode _transactionStartTimeMsNode = _node.get("transactionStartTimeMs");
            if (_transactionStartTimeMsNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'transactionStartTimeMs', which is mandatory in version " + _version);
            } else {
                _object.transactionStartTimeMs = MessageUtil.jsonNodeToLong(_transactionStartTimeMsNode, "TransactionState");
            }
            JsonNode _producerIdNode = _node.get("producerId");
            if (_producerIdNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'producerId', which is mandatory in version " + _version);
            } else {
                _object.producerId = MessageUtil.jsonNodeToLong(_producerIdNode, "TransactionState");
            }
            JsonNode _producerEpochNode = _node.get("producerEpoch");
            if (_producerEpochNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'producerEpoch', which is mandatory in version " + _version);
            } else {
                _object.producerEpoch = MessageUtil.jsonNodeToShort(_producerEpochNode, "TransactionState");
            }
            JsonNode _topicsNode = _node.get("topics");
            if (_topicsNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'topics', which is mandatory in version " + _version);
            } else {
                if (!_topicsNode.isArray()) {
                    throw new RuntimeException("TransactionState expected a JSON array, but got " + _node.getNodeType());
                }
                TopicDataCollection _collection = new TopicDataCollection(_topicsNode.size());
                _object.topics = _collection;
                for (JsonNode _element : _topicsNode) {
                    _collection.add(TopicDataJsonConverter.read(_element, _version));
                }
            }
            return _object;
        }
        public static JsonNode write(TransactionState _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("errorCode", new ShortNode(_object.errorCode));
            _node.set("transactionalId", new TextNode(_object.transactionalId));
            _node.set("transactionState", new TextNode(_object.transactionState));
            _node.set("transactionTimeoutMs", new IntNode(_object.transactionTimeoutMs));
            _node.set("transactionStartTimeMs", new LongNode(_object.transactionStartTimeMs));
            _node.set("producerId", new LongNode(_object.producerId));
            _node.set("producerEpoch", new ShortNode(_object.producerEpoch));
            ArrayNode _topicsArray = new ArrayNode(JsonNodeFactory.instance);
            for (TopicData _element : _object.topics) {
                _topicsArray.add(TopicDataJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("topics", _topicsArray);
            return _node;
        }
        public static JsonNode write(TransactionState _object, short _version) {
            return write(_object, _version, true);
        }
    }
}
