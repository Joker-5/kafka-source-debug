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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.DelegationTokenRecord.*;

public class DelegationTokenRecordJsonConverter {
    public static DelegationTokenRecord read(JsonNode _node, short _version) {
        DelegationTokenRecord _object = new DelegationTokenRecord();
        JsonNode _ownerNode = _node.get("owner");
        if (_ownerNode == null) {
            throw new RuntimeException("DelegationTokenRecord: unable to locate field 'owner', which is mandatory in version " + _version);
        } else {
            if (!_ownerNode.isTextual()) {
                throw new RuntimeException("DelegationTokenRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.owner = _ownerNode.asText();
        }
        JsonNode _renewersNode = _node.get("renewers");
        if (_renewersNode == null) {
            throw new RuntimeException("DelegationTokenRecord: unable to locate field 'renewers', which is mandatory in version " + _version);
        } else {
            if (!_renewersNode.isArray()) {
                throw new RuntimeException("DelegationTokenRecord expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<String> _collection = new ArrayList<String>(_renewersNode.size());
            _object.renewers = _collection;
            for (JsonNode _element : _renewersNode) {
                if (!_element.isTextual()) {
                    throw new RuntimeException("DelegationTokenRecord element expected a string type, but got " + _node.getNodeType());
                }
                _collection.add(_element.asText());
            }
        }
        JsonNode _issueTimestampNode = _node.get("issueTimestamp");
        if (_issueTimestampNode == null) {
            throw new RuntimeException("DelegationTokenRecord: unable to locate field 'issueTimestamp', which is mandatory in version " + _version);
        } else {
            _object.issueTimestamp = MessageUtil.jsonNodeToLong(_issueTimestampNode, "DelegationTokenRecord");
        }
        JsonNode _maxTimestampNode = _node.get("maxTimestamp");
        if (_maxTimestampNode == null) {
            throw new RuntimeException("DelegationTokenRecord: unable to locate field 'maxTimestamp', which is mandatory in version " + _version);
        } else {
            _object.maxTimestamp = MessageUtil.jsonNodeToLong(_maxTimestampNode, "DelegationTokenRecord");
        }
        JsonNode _expirationTimestampNode = _node.get("expirationTimestamp");
        if (_expirationTimestampNode == null) {
            throw new RuntimeException("DelegationTokenRecord: unable to locate field 'expirationTimestamp', which is mandatory in version " + _version);
        } else {
            _object.expirationTimestamp = MessageUtil.jsonNodeToLong(_expirationTimestampNode, "DelegationTokenRecord");
        }
        JsonNode _tokenIdNode = _node.get("tokenId");
        if (_tokenIdNode == null) {
            throw new RuntimeException("DelegationTokenRecord: unable to locate field 'tokenId', which is mandatory in version " + _version);
        } else {
            if (!_tokenIdNode.isTextual()) {
                throw new RuntimeException("DelegationTokenRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.tokenId = _tokenIdNode.asText();
        }
        return _object;
    }
    public static JsonNode write(DelegationTokenRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("owner", new TextNode(_object.owner));
        ArrayNode _renewersArray = new ArrayNode(JsonNodeFactory.instance);
        for (String _element : _object.renewers) {
            _renewersArray.add(new TextNode(_element));
        }
        _node.set("renewers", _renewersArray);
        _node.set("issueTimestamp", new LongNode(_object.issueTimestamp));
        _node.set("maxTimestamp", new LongNode(_object.maxTimestamp));
        _node.set("expirationTimestamp", new LongNode(_object.expirationTimestamp));
        _node.set("tokenId", new TextNode(_object.tokenId));
        return _node;
    }
    public static JsonNode write(DelegationTokenRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
