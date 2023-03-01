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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.MessageSizeAccumulator;
import org.apache.kafka.common.protocol.MessageUtil;
import org.apache.kafka.common.protocol.ObjectSerializationCache;
import org.apache.kafka.common.protocol.Readable;
import org.apache.kafka.common.protocol.Writable;
import org.apache.kafka.common.protocol.types.ArrayOf;
import org.apache.kafka.common.protocol.types.CompactArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class DescribeGroupsRequestData implements ApiMessage {
    List<String> groups;
    boolean includeAuthorizedOperations;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("groups", new ArrayOf(Type.STRING), "The names of the groups to describe")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("groups", new ArrayOf(Type.STRING), "The names of the groups to describe"),
            new Field("include_authorized_operations", Type.BOOLEAN, "Whether to include authorized operations.")
        );
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("groups", new CompactArrayOf(Type.COMPACT_STRING), "The names of the groups to describe"),
            new Field("include_authorized_operations", Type.BOOLEAN, "Whether to include authorized operations."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 5;
    
    public DescribeGroupsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeGroupsRequestData() {
        this.groups = new ArrayList<String>(0);
        this.includeAuthorizedOperations = false;
    }
    
    @Override
    public short apiKey() {
        return 15;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 5;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            if (_version >= 5) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field groups was serialized as null");
                } else {
                    ArrayList<String> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readUnsignedVarint() - 1;
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field groups element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field groups element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.groups = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field groups was serialized as null");
                } else {
                    ArrayList<String> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        int length;
                        length = _readable.readShort();
                        if (length < 0) {
                            throw new RuntimeException("non-nullable field groups element was serialized as null");
                        } else if (length > 0x7fff) {
                            throw new RuntimeException("string field groups element had invalid length " + length);
                        } else {
                            newCollection.add(_readable.readString(length));
                        }
                    }
                    this.groups = newCollection;
                }
            }
        }
        if (_version >= 3) {
            this.includeAuthorizedOperations = _readable.readByte() != 0;
        } else {
            this.includeAuthorizedOperations = false;
        }
        this._unknownTaggedFields = null;
        if (_version >= 5) {
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
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        if (_version >= 5) {
            _writable.writeUnsignedVarint(groups.size() + 1);
            for (String groupsElement : groups) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(groupsElement);
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                    _writable.writeByteArray(_stringBytes);
                }
            }
        } else {
            _writable.writeInt(groups.size());
            for (String groupsElement : groups) {
                {
                    byte[] _stringBytes = _cache.getSerializedValue(groupsElement);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
                }
            }
        }
        if (_version >= 3) {
            _writable.writeByte(includeAuthorizedOperations ? (byte) 1 : (byte) 0);
        } else {
            if (this.includeAuthorizedOperations) {
                throw new UnsupportedVersionException("Attempted to write a non-default includeAuthorizedOperations at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 5) {
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        {
            if (_version >= 5) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(groups.size() + 1));
            } else {
                _size.addBytes(4);
            }
            for (String groupsElement : groups) {
                byte[] _stringBytes = groupsElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupsElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupsElement, _stringBytes);
                if (_version >= 5) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
        }
        if (_version >= 3) {
            _size.addBytes(1);
        }
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                _size.addBytes(_field.size());
            }
        }
        if (_version >= 5) {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DescribeGroupsRequestData)) return false;
        DescribeGroupsRequestData other = (DescribeGroupsRequestData) obj;
        if (this.groups == null) {
            if (other.groups != null) return false;
        } else {
            if (!this.groups.equals(other.groups)) return false;
        }
        if (includeAuthorizedOperations != other.includeAuthorizedOperations) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (groups == null ? 0 : groups.hashCode());
        hashCode = 31 * hashCode + (includeAuthorizedOperations ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public DescribeGroupsRequestData duplicate() {
        DescribeGroupsRequestData _duplicate = new DescribeGroupsRequestData();
        ArrayList<String> newGroups = new ArrayList<String>(groups.size());
        for (String _element : groups) {
            newGroups.add(_element);
        }
        _duplicate.groups = newGroups;
        _duplicate.includeAuthorizedOperations = includeAuthorizedOperations;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "DescribeGroupsRequestData("
            + "groups=" + MessageUtil.deepToString(groups.iterator())
            + ", includeAuthorizedOperations=" + (includeAuthorizedOperations ? "true" : "false")
            + ")";
    }
    
    public List<String> groups() {
        return this.groups;
    }
    
    public boolean includeAuthorizedOperations() {
        return this.includeAuthorizedOperations;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeGroupsRequestData setGroups(List<String> v) {
        this.groups = v;
        return this;
    }
    
    public DescribeGroupsRequestData setIncludeAuthorizedOperations(boolean v) {
        this.includeAuthorizedOperations = v;
        return this;
    }
}
