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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import org.apache.kafka.common.protocol.MessageUtil;

import static org.apache.kafka.common.message.SnapshotHeaderRecord.*;

public class SnapshotHeaderRecordJsonConverter {
    public static SnapshotHeaderRecord read(JsonNode _node, short _version) {
        SnapshotHeaderRecord _object = new SnapshotHeaderRecord();
        JsonNode _versionNode = _node.get("version");
        if (_versionNode == null) {
            throw new RuntimeException("SnapshotHeaderRecord: unable to locate field 'version', which is mandatory in version " + _version);
        } else {
            _object.version = MessageUtil.jsonNodeToShort(_versionNode, "SnapshotHeaderRecord");
        }
        JsonNode _lastContainedLogTimestampNode = _node.get("lastContainedLogTimestamp");
        if (_lastContainedLogTimestampNode == null) {
            throw new RuntimeException("SnapshotHeaderRecord: unable to locate field 'lastContainedLogTimestamp', which is mandatory in version " + _version);
        } else {
            _object.lastContainedLogTimestamp = MessageUtil.jsonNodeToLong(_lastContainedLogTimestampNode, "SnapshotHeaderRecord");
        }
        return _object;
    }
    public static JsonNode write(SnapshotHeaderRecord _object, short _version, boolean _serializeRecords) {
        ObjectNode _node = new ObjectNode(JsonNodeFactory.instance);
        _node.set("version", new ShortNode(_object.version));
        _node.set("lastContainedLogTimestamp", new LongNode(_object.lastContainedLogTimestamp));
        return _node;
    }
    public static JsonNode write(SnapshotHeaderRecord _object, short _version) {
        return write(_object, _version, true);
    }
}
