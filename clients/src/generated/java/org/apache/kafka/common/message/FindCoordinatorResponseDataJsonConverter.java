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
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.message.FindCoordinatorResponseData.*;

public class FindCoordinatorResponseDataJsonConverter {
    public static FindCoordinatorResponseData read(JsonNode _node, short _version) {
        FindCoordinatorResponseData _object = new FindCoordinatorResponseData();
        JsonNode _throttleTimeMsNode = _node.get("throttleTimeMs");
        if (_throttleTimeMsNode == null) {
            if (_version >= 1) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'throttleTimeMs', which is mandatory in version " + _version);
            } else {
                _object.throttleTimeMs = 0;
            }
        } else {
            _object.throttleTimeMs = MessageUtil.jsonNodeToInt(_throttleTimeMsNode, "FindCoordinatorResponseData");
        }
        JsonNode _errorCodeNode = _node.get("errorCode");
        if (_errorCodeNode == null) {
            if (_version <= 3) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = (short) 0;
            }
        } else {
            _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "FindCoordinatorResponseData");
        }
        JsonNode _errorMessageNode = _node.get("errorMessage");
        if (_errorMessageNode == null) {
            if ((_version >= 1) && (_version <= 3)) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'errorMessage', which is mandatory in version " + _version);
            } else {
                _object.errorMessage = "";
            }
        } else {
            if (_errorMessageNode.isNull()) {
                _object.errorMessage = null;
            } else {
                if (!_errorMessageNode.isTextual()) {
                    throw new RuntimeException("FindCoordinatorResponseData expected a string type, but got " + _node.getNodeType());
                }
                _object.errorMessage = _errorMessageNode.asText();
            }
        }
        JsonNode _nodeIdNode = _node.get("nodeId");
        if (_nodeIdNode == null) {
            if (_version <= 3) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'nodeId', which is mandatory in version " + _version);
            } else {
                _object.nodeId = 0;
            }
        } else {
            _object.nodeId = MessageUtil.jsonNodeToInt(_nodeIdNode, "FindCoordinatorResponseData");
        }
        JsonNode _hostNode = _node.get("host");
        if (_hostNode == null) {
            if (_version <= 3) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'host', which is mandatory in version " + _version);
            } else {
                _object.host = "";
            }
        } else {
            if (!_hostNode.isTextual()) {
                throw new RuntimeException("FindCoordinatorResponseData expected a string type, but got " + _node.getNodeType());
            }
            _object.host = _hostNode.asText();
        }
        JsonNode _portNode = _node.get("port");
        if (_portNode == null) {
            if (_version <= 3) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'port', which is mandatory in version " + _version);
            } else {
                _object.port = 0;
            }
        } else {
            _object.port = MessageUtil.jsonNodeToInt(_portNode, "FindCoordinatorResponseData");
        }
        JsonNode _coordinatorsNode = _node.get("coordinators");
        if (_coordinatorsNode == null) {
            if (_version >= 4) {
                throw new RuntimeException("FindCoordinatorResponseData: unable to locate field 'coordinators', which is mandatory in version " + _version);
            } else {
                _object.coordinators = new ArrayList<Coordinator>(0);
            }
        } else {
            if (!_coordinatorsNode.isArray()) {
                throw new RuntimeException("FindCoordinatorResponseData expected a JSON array, but got " + _node.getNodeType());
            }
            ArrayList<Coordinator> _collection = new ArrayList<Coordinator>(_coordinatorsNode.size());
            _object.coordinators = _collection;
            for (JsonNode _element : _coordinatorsNode) {
                _collection.add(CoordinatorJsonConverter.read(_element, _version));
            }
        }
        return _object;
    }
    public static JsonNode write(FindCoordinatorResponseData _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        if (_version >= 1) {
            _node.set("throttleTimeMs", new IntNode(_object.throttleTimeMs));
        }
        if (_version <= 3) {
            _node.set("errorCode", new ShortNode(_object.errorCode));
        } else {
            if (_object.errorCode != (short) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default errorCode at version " + _version);
            }
        }
        if ((_version >= 1) && (_version <= 3)) {
            if (_object.errorMessage == null) {
                _node.set("errorMessage", NullNode.instance);
            } else {
                _node.set("errorMessage", new TextNode(_object.errorMessage));
            }
        }
        if (_version <= 3) {
            _node.set("nodeId", new IntNode(_object.nodeId));
        } else {
            if (_object.nodeId != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default nodeId at version " + _version);
            }
        }
        if (_version <= 3) {
            _node.set("host", new TextNode(_object.host));
        } else {
            if (!_object.host.equals("")) {
                throw new UnsupportedVersionException("Attempted to write a non-default host at version " + _version);
            }
        }
        if (_version <= 3) {
            _node.set("port", new IntNode(_object.port));
        } else {
            if (_object.port != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default port at version " + _version);
            }
        }
        if (_version >= 4) {
            ArrayNode _coordinatorsArray = new ArrayNode(JsonNodeFactory.instance);
            for (Coordinator _element : _object.coordinators) {
                _coordinatorsArray.add(CoordinatorJsonConverter.write(_element, _version, _serializeRecords));
            }
            _node.set("coordinators", _coordinatorsArray);
        } else {
            if (!_object.coordinators.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default coordinators at version " + _version);
            }
        }
        return _node;
    }
    public static JsonNode write(FindCoordinatorResponseData _object, short _version) {
        return write(_object, _version, true);
    }
    
    public static class CoordinatorJsonConverter {
        public static Coordinator read(JsonNode _node, short _version) {
            Coordinator _object = new Coordinator();
            if (_version < 4) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of Coordinator");
            }
            JsonNode _keyNode = _node.get("key");
            if (_keyNode == null) {
                throw new RuntimeException("Coordinator: unable to locate field 'key', which is mandatory in version " + _version);
            } else {
                if (!_keyNode.isTextual()) {
                    throw new RuntimeException("Coordinator expected a string type, but got " + _node.getNodeType());
                }
                _object.key = _keyNode.asText();
            }
            JsonNode _nodeIdNode = _node.get("nodeId");
            if (_nodeIdNode == null) {
                throw new RuntimeException("Coordinator: unable to locate field 'nodeId', which is mandatory in version " + _version);
            } else {
                _object.nodeId = MessageUtil.jsonNodeToInt(_nodeIdNode, "Coordinator");
            }
            JsonNode _hostNode = _node.get("host");
            if (_hostNode == null) {
                throw new RuntimeException("Coordinator: unable to locate field 'host', which is mandatory in version " + _version);
            } else {
                if (!_hostNode.isTextual()) {
                    throw new RuntimeException("Coordinator expected a string type, but got " + _node.getNodeType());
                }
                _object.host = _hostNode.asText();
            }
            JsonNode _portNode = _node.get("port");
            if (_portNode == null) {
                throw new RuntimeException("Coordinator: unable to locate field 'port', which is mandatory in version " + _version);
            } else {
                _object.port = MessageUtil.jsonNodeToInt(_portNode, "Coordinator");
            }
            JsonNode _errorCodeNode = _node.get("errorCode");
            if (_errorCodeNode == null) {
                throw new RuntimeException("Coordinator: unable to locate field 'errorCode', which is mandatory in version " + _version);
            } else {
                _object.errorCode = MessageUtil.jsonNodeToShort(_errorCodeNode, "Coordinator");
            }
            JsonNode _errorMessageNode = _node.get("errorMessage");
            if (_errorMessageNode == null) {
                throw new RuntimeException("Coordinator: unable to locate field 'errorMessage', which is mandatory in version " + _version);
            } else {
                if (_errorMessageNode.isNull()) {
                    _object.errorMessage = null;
                } else {
                    if (!_errorMessageNode.isTextual()) {
                        throw new RuntimeException("Coordinator expected a string type, but got " + _node.getNodeType());
                    }
                    _object.errorMessage = _errorMessageNode.asText();
                }
            }
            return _object;
        }
        public static JsonNode write(Coordinator _object, short _version, boolean _serializeRecords) {
            if (_version < 4) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of Coordinator");
            }
            ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
            _node.set("key", new TextNode(_object.key));
            _node.set("nodeId", new IntNode(_object.nodeId));
            _node.set("host", new TextNode(_object.host));
            _node.set("port", new IntNode(_object.port));
            _node.set("errorCode", new ShortNode(_object.errorCode));
            if (_object.errorMessage == null) {
                _node.set("errorMessage", NullNode.instance);
            } else {
                _node.set("errorMessage", new TextNode(_object.errorMessage));
            }
            return _node;
        }
        public static JsonNode write(Coordinator _object, short _version) {
            return write(_object, _version, true);
        }
    }
}
