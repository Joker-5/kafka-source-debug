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

import static org.apache.kafka.common.metadata.ConfigRecord.*;

public class ConfigRecordJsonConverter {
    public static ConfigRecord read(JsonNode _node, short _version) {
        ConfigRecord _object = new ConfigRecord();
        JsonNode _resourceTypeNode = _node.get("resourceType");
        if (_resourceTypeNode == null) {
            throw new RuntimeException("ConfigRecord: unable to locate field 'resourceType', which is mandatory in version " + _version);
        } else {
            _object.resourceType = MessageUtil.jsonNodeToByte(_resourceTypeNode, "ConfigRecord");
        }
        JsonNode _resourceNameNode = _node.get("resourceName");
        if (_resourceNameNode == null) {
            throw new RuntimeException("ConfigRecord: unable to locate field 'resourceName', which is mandatory in version " + _version);
        } else {
            if (!_resourceNameNode.isTextual()) {
                throw new RuntimeException("ConfigRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.resourceName = _resourceNameNode.asText();
        }
        JsonNode _nameNode = _node.get("name");
        if (_nameNode == null) {
            throw new RuntimeException("ConfigRecord: unable to locate field 'name', which is mandatory in version " + _version);
        } else {
            if (!_nameNode.isTextual()) {
                throw new RuntimeException("ConfigRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.name = _nameNode.asText();
        }
        JsonNode _valueNode = _node.get("value");
        if (_valueNode == null) {
            throw new RuntimeException("ConfigRecord: unable to locate field 'value', which is mandatory in version " + _version);
        } else {
            if (_valueNode.isNull()) {
                _object.value = null;
            } else {
                if (!_valueNode.isTextual()) {
                    throw new RuntimeException("ConfigRecord expected a string type, but got " + _node.getNodeType());
                }
                _object.value = _valueNode.asText();
            }
        }
        return _object;
    }
    public static JsonNode write(ConfigRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("resourceType", new ShortNode(_object.resourceType));
        _node.set("resourceName", new TextNode(_object.resourceName));
        _node.set("name", new TextNode(_object.name));
        if (_object.value == null) {
            _node.set("value", NullNode.instance);
        } else {
            _node.set("value", new TextNode(_object.value));
        }
        return _node;
    }
    public static JsonNode write(ConfigRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
