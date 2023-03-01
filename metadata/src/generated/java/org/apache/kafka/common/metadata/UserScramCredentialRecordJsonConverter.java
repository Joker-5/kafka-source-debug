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
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.UserScramCredentialRecord.*;

public class UserScramCredentialRecordJsonConverter {
    public static UserScramCredentialRecord read(JsonNode _node, short _version) {
        UserScramCredentialRecord _object = new UserScramCredentialRecord();
        JsonNode _nameNode = _node.get("name");
        if (_nameNode == null) {
            throw new RuntimeException("UserScramCredentialRecord: unable to locate field 'name', which is mandatory in version " + _version);
        } else {
            if (!_nameNode.isTextual()) {
                throw new RuntimeException("UserScramCredentialRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.name = _nameNode.asText();
        }
        JsonNode _credentialInfosNode = _node.get("credentialInfos");
        if (_credentialInfosNode == null) {
            throw new RuntimeException("UserScramCredentialRecord: unable to locate field 'credentialInfos', which is mandatory in version " + _version);
        } else {
            if (!_credentialInfosNode.isArray()) {
                throw new RuntimeException("UserScramCredentialRecord expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<CredentialInfo> _collection = new ArrayList<CredentialInfo>(_credentialInfosNode.size());
            _object.credentialInfos = _collection;
            for (JsonNode _element : _credentialInfosNode) {
                _collection.add(CredentialInfoJsonConverter.read(_element, _version));
            }
        }
        return _object;
    }
    public static JsonNode write(UserScramCredentialRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("name", new TextNode(_object.name));
        ArrayNode _credentialInfosArray = new ArrayNode(JsonNodeFactory.instance);
        for (CredentialInfo _element : _object.credentialInfos) {
            _credentialInfosArray.add(CredentialInfoJsonConverter.write(_element, _version, _serializeRecords));
        }
        _node.set("credentialInfos", _credentialInfosArray);
        return _node;
    }
    public static JsonNode write(UserScramCredentialRecord _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class CredentialInfoJsonConverter {
        public static CredentialInfo read(JsonNode _node, short _version) {
            CredentialInfo _object = new CredentialInfo();
            JsonNode _mechanismNode = _node.get("mechanism");
            if (_mechanismNode == null) {
                throw new RuntimeException("CredentialInfo: unable to locate field 'mechanism', which is mandatory in version " + _version);
            } else {
                _object.mechanism = MessageUtil.jsonNodeToByte(_mechanismNode, "CredentialInfo");
            }
            JsonNode _saltNode = _node.get("salt");
            if (_saltNode == null) {
                throw new RuntimeException("CredentialInfo: unable to locate field 'salt', which is mandatory in version " + _version);
            } else {
                _object.salt = MessageUtil.jsonNodeToBinary(_saltNode, "CredentialInfo");
            }
            JsonNode _saltedPasswordNode = _node.get("saltedPassword");
            if (_saltedPasswordNode == null) {
                throw new RuntimeException("CredentialInfo: unable to locate field 'saltedPassword', which is mandatory in version " + _version);
            } else {
                _object.saltedPassword = MessageUtil.jsonNodeToBinary(_saltedPasswordNode, "CredentialInfo");
            }
            JsonNode _iterationsNode = _node.get("iterations");
            if (_iterationsNode == null) {
                throw new RuntimeException("CredentialInfo: unable to locate field 'iterations', which is mandatory in version " + _version);
            } else {
                _object.iterations = MessageUtil.jsonNodeToInt(_iterationsNode, "CredentialInfo");
            }
            return _object;
        }
        public static JsonNode write(CredentialInfo _object, short _version, boolean _serializeRecords) {
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("mechanism", new ShortNode(_object.mechanism));
            _node.set("salt", new BinaryNode(Arrays.copyOf(_object.salt, _object.salt.length)));
            _node.set("saltedPassword", new BinaryNode(Arrays.copyOf(_object.saltedPassword, _object.saltedPassword.length)));
            _node.set("iterations", new IntNode(_object.iterations));
            return _node;
        }
        public static JsonNode write(CredentialInfo _object, short _version) {
            return write(_object, _version, true);
        }
    }
}
