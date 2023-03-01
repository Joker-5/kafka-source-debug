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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.metadata.FeatureLevelRecord.*;

public class FeatureLevelRecordJsonConverter {
    public static FeatureLevelRecord read(JsonNode _node, short _version) {
        FeatureLevelRecord _object = new FeatureLevelRecord();
        JsonNode _nameNode = _node.get("name");
        if (_nameNode == null) {
            throw new RuntimeException("FeatureLevelRecord: unable to locate field 'name', which is mandatory in version " + _version);
        } else {
            if (!_nameNode.isTextual()) {
                throw new RuntimeException("FeatureLevelRecord expected a string type, but got " + _node.getNodeType());
            }
            _object.name = _nameNode.asText();
        }
        JsonNode _minFeatureLevelNode = _node.get("minFeatureLevel");
        if (_minFeatureLevelNode == null) {
            throw new RuntimeException("FeatureLevelRecord: unable to locate field 'minFeatureLevel', which is mandatory in version " + _version);
        } else {
            _object.minFeatureLevel = MessageUtil.jsonNodeToShort(_minFeatureLevelNode, "FeatureLevelRecord");
        }
        JsonNode _maxFeatureLevelNode = _node.get("maxFeatureLevel");
        if (_maxFeatureLevelNode == null) {
            throw new RuntimeException("FeatureLevelRecord: unable to locate field 'maxFeatureLevel', which is mandatory in version " + _version);
        } else {
            _object.maxFeatureLevel = MessageUtil.jsonNodeToShort(_maxFeatureLevelNode, "FeatureLevelRecord");
        }
        return _object;
    }
    public static JsonNode write(FeatureLevelRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("name", new TextNode(_object.name));
        _node.set("minFeatureLevel", new ShortNode(_object.minFeatureLevel));
        _node.set("maxFeatureLevel", new ShortNode(_object.maxFeatureLevel));
        return _node;
    }
    public static JsonNode write(FeatureLevelRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
