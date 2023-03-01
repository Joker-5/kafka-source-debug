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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;

import static org.apache.kafka.common.message.DescribeTransactionsRequestData.*;

public class DescribeTransactionsRequestDataJsonConverter {
    public static DescribeTransactionsRequestData read(JsonNode _node, short _version) {
        DescribeTransactionsRequestData _object = new DescribeTransactionsRequestData();
        JsonNode _transactionalIdsNode = _node.get("transactionalIds");
        if (_transactionalIdsNode == null) {
            throw new RuntimeException("DescribeTransactionsRequestData: unable to locate field 'transactionalIds', which is mandatory in version " + _version);
        } else {
            if (!_transactionalIdsNode.isArray()) {
                throw new RuntimeException("DescribeTransactionsRequestData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<String> _collection = new ArrayList<String>(_transactionalIdsNode.size());
            _object.transactionalIds = _collection;
            for (JsonNode _element : _transactionalIdsNode) {
                if (!_element.isTextual()) {
                    throw new RuntimeException("DescribeTransactionsRequestData element expected a string type, but got " + _node.getNodeType());
                }
                _collection.add(_element.asText());
            }
        }
        return _object;
    }
    public static JsonNode write(DescribeTransactionsRequestData _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode _transactionalIdsArray = new ArrayNode(JsonNodeFactory.instance);
        for (String _element : _object.transactionalIds) {
            _transactionalIdsArray.add(new TextNode(_element));
        }
        _node.set("transactionalIds", _transactionalIdsArray);
        return _node;
    }
    public static JsonNode write(DescribeTransactionsRequestData _object, short _version) {
        return write(_object, _version, true);
    }
}
