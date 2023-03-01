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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.AccessControlRecord.*;

public class AccessControlRecordJsonConverter {
    public static AccessControlRecord read(JsonNode _node, short _version) {
        AccessControlRecord _object = new AccessControlRecord();
        JsonNode _resourceTypeNode = _node.get("resourceType");
        if (_resourceTypeNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'resourceType', which is mandatory in version " + _version);
        } else {
            _object.resourceType = MessageUtil.jsonNodeToByte(_resourceTypeNode, "AccessControlRecord");
        }
        JsonNode _resourceNameNode = _node.get("resourceName");
        if (_resourceNameNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'resourceName', which is mandatory in version " + _version);
        } else {
            if (_resourceNameNode.isNull()) {
                _object.resourceName = null;
            } else {
                if (!_resourceNameNode.isTextual()) {
                    throw new RuntimeException("AccessControlRecord expected a string type, but got " + _node.getNodeType());
                }
                _object.resourceName = _resourceNameNode.asText();
            }
        }
        JsonNode _patternTypeNode = _node.get("patternType");
        if (_patternTypeNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'patternType', which is mandatory in version " + _version);
        } else {
            _object.patternType = MessageUtil.jsonNodeToByte(_patternTypeNode, "AccessControlRecord");
        }
        JsonNode _principalNode = _node.get("principal");
        if (_principalNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'principal', which is mandatory in version " + _version);
        } else {
            if (!_principalNode.isTextual()) {
                throw new RuntimeException("AccessControlRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.principal = _principalNode.asText();
        }
        JsonNode _hostNode = _node.get("host");
        if (_hostNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'host', which is mandatory in version " + _version);
        } else {
            if (!_hostNode.isTextual()) {
                throw new RuntimeException("AccessControlRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.host = _hostNode.asText();
        }
        JsonNode _operationNode = _node.get("operation");
        if (_operationNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'operation', which is mandatory in version " + _version);
        } else {
            _object.operation = MessageUtil.jsonNodeToByte(_operationNode, "AccessControlRecord");
        }
        JsonNode _permissionTypeNode = _node.get("permissionType");
        if (_permissionTypeNode == null) {
            throw new RuntimeException("AccessControlRecord: unable to locate field 'permissionType', which is mandatory in version " + _version);
        } else {
            _object.permissionType = MessageUtil.jsonNodeToByte(_permissionTypeNode, "AccessControlRecord");
        }
        return _object;
    }
    public static JsonNode write(AccessControlRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("resourceType", new ShortNode(_object.resourceType));
        if (_object.resourceName == null) {
            _node.set("resourceName", NullNode.instance);
        } else {
            _node.set("resourceName", new TextNode(_object.resourceName));
        }
        _node.set("patternType", new ShortNode(_object.patternType));
        _node.set("principal", new TextNode(_object.principal));
        _node.set("host", new TextNode(_object.host));
        _node.set("operation", new ShortNode(_object.operation));
        _node.set("permissionType", new ShortNode(_object.permissionType));
        return _node;
    }
    public static JsonNode write(AccessControlRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
