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

import static org.apache.kafka.common.message.ListTransactionsResponseData.*;

public class ListTransactionsResponseDataJsonConverter {
    public static ListTransactionsResponseData read(JsonNode _node, short _version) {
        ListTransactionsResponseData _object = new ListTransactionsResponseData();
        JsonNode _throttleTimeMsNode = _node.get("throttleTimeMs");
        if (_throttleTimeMsNode == null) {
            throw new RuntimeException("ListTransactionsResponseData: unable to locate field 'throttleTimeMs', which is mandatory in version " + _version);
        } else {
            _object.throttleTimeMs = MessageUtil.jsonNodeToInt(_throttleTimeMsNode, "ListTransactionsResponseData");
        }
        JsonNode _errorCodeNode = _node.get("errorCode");
        if (_errorCodeNode == null) {
            throw new RuntimeException("ListTransactionsResponseData: unable to locate field 'errorCode', which is mandatory in version " + _version);
        } else {
            _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "ListTransactionsResponseData");
        }
        JsonNode _unknownStateFiltersNode = _node.get("unknownStateFilters");
        if (_unknownStateFiltersNode == null) {
            throw new RuntimeException("ListTransactionsResponseData: unable to locate field 'unknownStateFilters', which is mandatory in version " + _version);
        } else {
            if (!_unknownStateFiltersNode.isArray()) {
                throw new RuntimeException("ListTransactionsResponseData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<String> _collection = new ArrayList<String>(_unknownStateFiltersNode.size());
            _object.unknownStateFilters = _collection;
            for (JsonNode _element : _unknownStateFiltersNode) {
                if (!_element.isTextual()) {
                    throw new RuntimeException("ListTransactionsResponseData element expected a string type, but got " + _node.getNodeType());
                }
                _collection.add(_element.asText());
            }
        }
        JsonNode _transactionStatesNode = _node.get("transactionStates");
        if (_transactionStatesNode == null) {
            throw new RuntimeException("ListTransactionsResponseData: unable to locate field 'transactionStates', which is mandatory in version " + _version);
        } else {
            if (!_transactionStatesNode.isArray()) {
                throw new RuntimeException("ListTransactionsResponseData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<TransactionState> _collection = new ArrayList<TransactionState>(_transactionStatesNode.size());
            _object.transactionStates = _collection;
            for (JsonNode _element : _transactionStatesNode) {
                _collection.add(TransactionStateJsonConverter.read(_element, _version));
            }
        }
        return _object;
    }
    public static JsonNode write(ListTransactionsResponseData _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("throttleTimeMs", new IntNode(_object.throttleTimeMs));
        _node.set("errorCode", new ShortNode(_object.errorCode));
        ArrayNode _unknownStateFiltersArray = new ArrayNode(JsonNodeFactory.instance);
        for (String _element : _object.unknownStateFilters) {
            _unknownStateFiltersArray.add(new TextNode(_element));
        }
        _node.set("unknownStateFilters", _unknownStateFiltersArray);
        ArrayNode _transactionStatesArray = new ArrayNode(JsonNodeFactory.instance);
        for (TransactionState _element : _object.transactionStates) {
            _transactionStatesArray.add(TransactionStateJsonConverter.write(_element, _version, _serializeRecords));
        }
        _node.set("transactionStates", _transactionStatesArray);
        return _node;
    }
    public static JsonNode write(ListTransactionsResponseData _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class TransactionStateJsonConverter {
        public static TransactionState read(JsonNode _node, short _version) {
            TransactionState _object = new TransactionState();
            JsonNode _transactionalIdNode = _node.get("transactionalId");
            if (_transactionalIdNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'transactionalId', which is mandatory in version " + _version);
            } else {
                if (!_transactionalIdNode.isTextual()) {
                    throw new RuntimeException("TransactionState expected a string type, but got " + _node.getNodeType());
                }
                _object.transactionalId = _transactionalIdNode.asText();
            }
            JsonNode _producerIdNode = _node.get("producerId");
            if (_producerIdNode == null) {
                throw new RuntimeException("TransactionState: unable to locate field 'producerId', which is mandatory in version " + _version);
            } else {
                _object.producerId = MessageUtil.jsonNodeToLong(_producerIdNode, "TransactionState");
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
            return _object;
        }
        public static JsonNode write(TransactionState _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("transactionalId", new TextNode(_object.transactionalId));
            _node.set("producerId", new LongNode(_object.producerId));
            _node.set("transactionState", new TextNode(_object.transactionState));
            return _node;
        }
        public static JsonNode write(TransactionState _object, short _version) {
            return write(_object, _version, true);
        }
    }
}
