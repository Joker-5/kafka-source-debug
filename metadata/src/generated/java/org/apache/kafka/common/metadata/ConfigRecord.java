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


public class ConfigRecord implements ApiMessage {
    byte resourceType;
    String resourceName;
    String name;
    String value;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("resource_type", Type.INT8, "The type of resource this configuration applies to."),
            new Field("resource_name", Type.COMPACT_STRING, "The name of the resource this configuration applies to."),
            new Field("name", Type.COMPACT_STRING, "The name of the configuration key."),
            new Field("value", Type.COMPACT_NULLABLE_STRING, "The value of the configuration, or null if the it should be deleted."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public ConfigRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ConfigRecord() {
        this.resourceType = (byte) 0;
        this.resourceName = "";
        this.name = "";
        this.value = "";
    }
    
    @Override
    public short apiKey() {
        return 4;
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
                throw new RuntimeException("non-nullable field resourceName was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field resourceName had invalid length " + length);
            } else {
                this.resourceName = _readable.readString(length);
            }
        }
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field name was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field name had invalid length " + length);
            } else {
                this.name = _readable.readString(length);
            }
        }
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.value = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field value had invalid length " + length);
            } else {
                this.value = _readable.readString(length);
            }
        }
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(resourceName);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        {
            byte[] _stringBytes = _cache.getSerializedValue(name);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        if (value == null) {
            _writable.writeUnsignedVarint(0);
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(value);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _size.addBytes(1);
        {
            byte[] _stringBytes = resourceName.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'resourceName' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(resourceName, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        {
            byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'name' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(name, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        if (value == null) {
            _size.addBytes(1);
        } else {
            byte[] _stringBytes = value.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'value' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(value, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
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
        if (!(obj instanceof ConfigRecord)) return false;
        ConfigRecord other = (ConfigRecord) obj;
        if (resourceType != other.resourceType) return false;
        if (this.resourceName == null) {
            if (other.resourceName != null) return false;
        } else {
            if (!this.resourceName.equals(other.resourceName)) return false;
        }
        if (this.name == null) {
            if (other.name != null) return false;
        } else {
            if (!this.name.equals(other.name)) return false;
        }
        if (this.value == null) {
            if (other.value != null) return false;
        } else {
            if (!this.value.equals(other.value)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + resourceType;
        hashCode = 31 * hashCode + (resourceName == null ? 0 : resourceName.hashCode());
        hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
        hashCode = 31 * hashCode + (value == null ? 0 : value.hashCode());
        return hashCode;
    }
    
    @Override
    public ConfigRecord duplicate() {
        ConfigRecord _duplicate = new ConfigRecord();
        _duplicate.resourceType = resourceType;
        _duplicate.resourceName = resourceName;
        _duplicate.name = name;
        if (value == null) {
            _duplicate.value = null;
        } else {
            _duplicate.value = value;
        }
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "ConfigRecord("
            + "resourceType=" + resourceType
            + ", resourceName=" + ((resourceName == null) ? "null" : "'" + resourceName.toString() + "'")
            + ", name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
            + ", value=" + ((value == null) ? "null" : "'" + value.toString() + "'")
            + ")";
    }
    
    public byte resourceType() {
        return this.resourceType;
    }
    
    public String resourceName() {
        return this.resourceName;
    }
    
    public String name() {
        return this.name;
    }
    
    public String value() {
        return this.value;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ConfigRecord setResourceType(byte v) {
        this.resourceType = v;
        return this;
    }
    
    public ConfigRecord setResourceName(String v) {
        this.resourceName = v;
        return this;
    }
    
    public ConfigRecord setName(String v) {
        this.name = v;
        return this;
    }
    
    public ConfigRecord setValue(String v) {
        this.value = v;
        return this;
    }
}
