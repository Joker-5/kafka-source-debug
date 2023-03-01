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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.MessageSizeAccumulator;
import org.apache.kafka.common.protocol.MessageUtil;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class AccessControlRecord implements ApiMessage {
    byte resourceType;
    String resourceName;
    byte patternType;
    String principal;
    String host;
    byte operation;
    byte permissionType;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("resource_type", Type.INT8, "The resource type"),
            new Field("resource_name", Type.COMPACT_NULLABLE_STRING, "The resource name, or null if this is for the default resource."),
            new Field("pattern_type", Type.INT8, "The pattern type (literal, prefixed, etc.)"),
            new Field("principal", Type.COMPACT_STRING, "The principal name."),
            new Field("host", Type.COMPACT_STRING, "The host."),
            new Field("operation", Type.INT8, "The operation type."),
            new Field("permission_type", Type.INT8, "The permission type (allow, deny)."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public AccessControlRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public AccessControlRecord() {
        this.resourceType = (byte) 0;
        this.resourceName = "";
        this.patternType = (byte) 0;
        this.principal = "";
        this.host = "";
        this.operation = (byte) 0;
        this.permissionType = (byte) 0;
    }
    
    @Override
    public short apiKey() {
        return 6;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 0;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.resourceType = _readable.readByte();
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.resourceName = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field resourceName had invalid length " + length);
            } else {
                this.resourceName = _readable.readString(length);
            }
        }
        this.patternType = _readable.readByte();
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field principal was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field principal had invalid length " + length);
            } else {
                this.principal = _readable.readString(length);
            }
        }
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field host was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field host had invalid length " + length);
            } else {
                this.host = _readable.readString(length);
            }
        }
        this.operation = _readable.readByte();
        this.permissionType = _readable.readByte();
        this._unknownTaggedFields = null;
        int _numTaggedFields = _readable.readUnsignedVarint();
        for (int _i = 0; _i < _numTaggedFields; _i++) {
            int _tag = _readable.readUnsignedVarint();
            int _size = _readable.readUnsignedVarint();
            switch (_tag) {
                default:
                    this._unknownTaggedFields = _readable.readUnknownTaggedField(this._unknownTaggedFields, _tag, _size);
                    break;
            }
        }
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeByte(resourceType);
        if (resourceName == null) {
            _writable.writeUnsignedVarint(0);
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(resourceName);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeByte(patternType);
        {
            byte[] _stringBytes = _cache.getSerializedValue(principal);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        {
            byte[] _stringBytes = _cache.getSerializedValue(host);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeByte(operation);
        _writable.writeByte(permissionType);
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _size.addBytes(1);
        if (resourceName == null) {
            _size.addBytes(1);
        } else {
            byte[] _stringBytes = resourceName.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'resourceName' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(resourceName, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        _size.addBytes(1);
        {
            byte[] _stringBytes = principal.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'principal' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(principal, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        {
            byte[] _stringBytes = host.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'host' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(host, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        _size.addBytes(1);
        _size.addBytes(1);
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                _size.addBytes(_field.size());
            }
        }
        _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccessControlRecord)) return false;
        AccessControlRecord other = (AccessControlRecord) obj;
        if (resourceType != other.resourceType) return false;
        if (this.resourceName == null) {
            if (other.resourceName != null) return false;
        } else {
            if (!this.resourceName.equals(other.resourceName)) return false;
        }
        if (patternType != other.patternType) return false;
        if (this.principal == null) {
            if (other.principal != null) return false;
        } else {
            if (!this.principal.equals(other.principal)) return false;
        }
        if (this.host == null) {
            if (other.host != null) return false;
        } else {
            if (!this.host.equals(other.host)) return false;
        }
        if (operation != other.operation) return false;
        if (permissionType != other.permissionType) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + resourceType;
        hashCode = 31 * hashCode + (resourceName == null ? 0 : resourceName.hashCode());
        hashCode = 31 * hashCode + patternType;
        hashCode = 31 * hashCode + (principal == null ? 0 : principal.hashCode());
        hashCode = 31 * hashCode + (host == null ? 0 : host.hashCode());
        hashCode = 31 * hashCode + operation;
        hashCode = 31 * hashCode + permissionType;
        return hashCode;
    }
    
    @Override
    public AccessControlRecord duplicate() {
        AccessControlRecord _duplicate = new AccessControlRecord();
        _duplicate.resourceType = resourceType;
        if (resourceName == null) {
            _duplicate.resourceName = null;
        } else {
            _duplicate.resourceName = resourceName;
        }
        _duplicate.patternType = patternType;
        _duplicate.principal = principal;
        _duplicate.host = host;
        _duplicate.operation = operation;
        _duplicate.permissionType = permissionType;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "AccessControlRecord("
            + "resourceType=" + resourceType
            + ", resourceName=" + ((resourceName == null) ? "null" : "'" + resourceName.toString() + "'")
            + ", patternType=" + patternType
            + ", principal=" + ((principal == null) ? "null" : "'" + principal.toString() + "'")
            + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
            + ", operation=" + operation
            + ", permissionType=" + permissionType
            + ")";
    }
    
    public byte resourceType() {
        return this.resourceType;
    }
    
    public String resourceName() {
        return this.resourceName;
    }
    
    public byte patternType() {
        return this.patternType;
    }
    
    public String principal() {
        return this.principal;
    }
    
    public String host() {
        return this.host;
    }
    
    public byte operation() {
        return this.operation;
    }
    
    public byte permissionType() {
        return this.permissionType;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public AccessControlRecord setResourceType(byte v) {
        this.resourceType = v;
        return this;
    }
    
    public AccessControlRecord setResourceName(String v) {
        this.resourceName = v;
        return this;
    }
    
    public AccessControlRecord setPatternType(byte v) {
        this.patternType = v;
        return this;
    }
    
    public AccessControlRecord setPrincipal(String v) {
        this.principal = v;
        return this;
    }
    
    public AccessControlRecord setHost(String v) {
        this.host = v;
        return this;
    }
    
    public AccessControlRecord setOperation(byte v) {
        this.operation = v;
        return this;
    }
    
    public AccessControlRecord setPermissionType(byte v) {
        this.permissionType = v;
        return this;
    }
}
