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
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.Message;
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
import org.apache.kafka.common.utils.Bytes;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class SyncGroupRequestData implements ApiMessage {
    String groupId;
    int generationId;
    String memberId;
    String groupInstanceId;
    String protocolType;
    String protocolName;
    List<SyncGroupRequestAssignment> assignments;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("group_id", Type.STRING, "The unique group identifier."),
            new Field("generation_id", Type.INT32, "The generation of the group."),
            new Field("member_id", Type.STRING, "The member ID assigned by the group."),
            new Field("assignments", new ArrayOf(SyncGroupRequestAssignment.SCHEMA_0), "Each assignment.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("group_id", Type.STRING, "The unique group identifier."),
            new Field("generation_id", Type.INT32, "The generation of the group."),
            new Field("member_id", Type.STRING, "The member ID assigned by the group."),
            new Field("group_instance_id", Type.NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
            new Field("assignments", new ArrayOf(SyncGroupRequestAssignment.SCHEMA_0), "Each assignment.")
        );
    
    public static final Schema SCHEMA_4 =
        new Schema(
            new Field("group_id", Type.COMPACT_STRING, "The unique group identifier."),
            new Field("generation_id", Type.INT32, "The generation of the group."),
            new Field("member_id", Type.COMPACT_STRING, "The member ID assigned by the group."),
            new Field("group_instance_id", Type.COMPACT_NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
            new Field("assignments", new CompactArrayOf(SyncGroupRequestAssignment.SCHEMA_4), "Each assignment."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("group_id", Type.COMPACT_STRING, "The unique group identifier."),
            new Field("generation_id", Type.INT32, "The generation of the group."),
            new Field("member_id", Type.COMPACT_STRING, "The member ID assigned by the group."),
            new Field("group_instance_id", Type.COMPACT_NULLABLE_STRING, "The unique identifier of the consumer instance provided by end user."),
            new Field("protocol_type", Type.COMPACT_NULLABLE_STRING, "The group protocol type."),
            new Field("protocol_name", Type.COMPACT_NULLABLE_STRING, "The group protocol name."),
            new Field("assignments", new CompactArrayOf(SyncGroupRequestAssignment.SCHEMA_4), "Each assignment."),
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
    
    public SyncGroupRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public SyncGroupRequestData() {
        this.groupId = "";
        this.generationId = 0;
        this.memberId = "";
        this.groupInstanceId = null;
        this.protocolType = null;
        this.protocolName = null;
        this.assignments = new ArrayList<SyncGroupRequestAssignment>(0);
    }
    
    @Override
    public short apiKey() {
        return 14;
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
            int length;
            if (_version >= 4) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field groupId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field groupId had invalid length " + length);
            } else {
                this.groupId = _readable.readString(length);
            }
        }
        this.generationId = _readable.readInt();
        {
            int length;
            if (_version >= 4) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                throw new RuntimeException("non-nullable field memberId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field memberId had invalid length " + length);
            } else {
                this.memberId = _readable.readString(length);
            }
        }
        if (_version >= 3) {
            int length;
            if (_version >= 4) {
                length = _readable.readUnsignedVarint() - 1;
            } else {
                length = _readable.readShort();
            }
            if (length < 0) {
                this.groupInstanceId = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field groupInstanceId had invalid length " + length);
            } else {
                this.groupInstanceId = _readable.readString(length);
            }
        } else {
            this.groupInstanceId = null;
        }
        if (_version >= 5) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.protocolType = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field protocolType had invalid length " + length);
            } else {
                this.protocolType = _readable.readString(length);
            }
        } else {
            this.protocolType = null;
        }
        if (_version >= 5) {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.protocolName = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field protocolName had invalid length " + length);
            } else {
                this.protocolName = _readable.readString(length);
            }
        } else {
            this.protocolName = null;
        }
        {
            if (_version >= 4) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field assignments was serialized as null");
                } else {
                    ArrayList<SyncGroupRequestAssignment> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new SyncGroupRequestAssignment(_readable, _version));
                    }
                    this.assignments = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field assignments was serialized as null");
                } else {
                    ArrayList<SyncGroupRequestAssignment> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new SyncGroupRequestAssignment(_readable, _version));
                    }
                    this.assignments = newCollection;
                }
            }
        }
        this._unknownTaggedFields = null;
        if (_version >= 4) {
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
        {
            byte[] _stringBytes = _cache.getSerializedValue(groupId);
            if (_version >= 4) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeInt(generationId);
        {
            byte[] _stringBytes = _cache.getSerializedValue(memberId);
            if (_version >= 4) {
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
            } else {
                _writable.writeShort((short) _stringBytes.length);
            }
            _writable.writeByteArray(_stringBytes);
        }
        if (_version >= 3) {
            if (groupInstanceId == null) {
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(groupInstanceId);
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
        } else {
            if (this.groupInstanceId != null) {
                throw new UnsupportedVersionException("Attempted to write a non-default groupInstanceId at version " + _version);
            }
        }
        if (_version >= 5) {
            if (protocolType == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(protocolType);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        }
        if (_version >= 5) {
            if (protocolName == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(protocolName);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        }
        if (_version >= 4) {
            _writable.writeUnsignedVarint(assignments.size() + 1);
            for (SyncGroupRequestAssignment assignmentsElement : assignments) {
                assignmentsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(assignments.size());
            for (SyncGroupRequestAssignment assignmentsElement : assignments) {
                assignmentsElement.write(_writable, _cache, _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 4) {
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
            byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'groupId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(groupId, _stringBytes);
            if (_version >= 4) {
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            } else {
                _size.addBytes(_stringBytes.length + 2);
            }
        }
        _size.addBytes(4);
        {
            byte[] _stringBytes = memberId.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'memberId' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(memberId, _stringBytes);
            if (_version >= 4) {
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            } else {
                _size.addBytes(_stringBytes.length + 2);
            }
        }
        if (_version >= 3) {
            if (groupInstanceId == null) {
                if (_version >= 4) {
                    _size.addBytes(1);
                } else {
                    _size.addBytes(2);
                }
            } else {
                byte[] _stringBytes = groupInstanceId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupInstanceId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupInstanceId, _stringBytes);
                if (_version >= 4) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
        }
        if (_version >= 5) {
            if (protocolType == null) {
                _size.addBytes(1);
            } else {
                byte[] _stringBytes = protocolType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'protocolType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(protocolType, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
        }
        if (_version >= 5) {
            if (protocolName == null) {
                _size.addBytes(1);
            } else {
                byte[] _stringBytes = protocolName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'protocolName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(protocolName, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
        }
        {
            if (_version >= 4) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(assignments.size() + 1));
            } else {
                _size.addBytes(4);
            }
            for (SyncGroupRequestAssignment assignmentsElement : assignments) {
                assignmentsElement.addSize(_size, _cache, _version);
            }
        }
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                _size.addBytes(_field.size());
            }
        }
        if (_version >= 4) {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SyncGroupRequestData)) return false;
        SyncGroupRequestData other = (SyncGroupRequestData) obj;
        if (this.groupId == null) {
            if (other.groupId != null) return false;
        } else {
            if (!this.groupId.equals(other.groupId)) return false;
        }
        if (generationId != other.generationId) return false;
        if (this.memberId == null) {
            if (other.memberId != null) return false;
        } else {
            if (!this.memberId.equals(other.memberId)) return false;
        }
        if (this.groupInstanceId == null) {
            if (other.groupInstanceId != null) return false;
        } else {
            if (!this.groupInstanceId.equals(other.groupInstanceId)) return false;
        }
        if (this.protocolType == null) {
            if (other.protocolType != null) return false;
        } else {
            if (!this.protocolType.equals(other.protocolType)) return false;
        }
        if (this.protocolName == null) {
            if (other.protocolName != null) return false;
        } else {
            if (!this.protocolName.equals(other.protocolName)) return false;
        }
        if (this.assignments == null) {
            if (other.assignments != null) return false;
        } else {
            if (!this.assignments.equals(other.assignments)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
        hashCode = 31 * hashCode + generationId;
        hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
        hashCode = 31 * hashCode + (groupInstanceId == null ? 0 : groupInstanceId.hashCode());
        hashCode = 31 * hashCode + (protocolType == null ? 0 : protocolType.hashCode());
        hashCode = 31 * hashCode + (protocolName == null ? 0 : protocolName.hashCode());
        hashCode = 31 * hashCode + (assignments == null ? 0 : assignments.hashCode());
        return hashCode;
    }
    
    @Override
    public SyncGroupRequestData duplicate() {
        SyncGroupRequestData _duplicate = new SyncGroupRequestData();
        _duplicate.groupId = groupId;
        _duplicate.generationId = generationId;
        _duplicate.memberId = memberId;
        if (groupInstanceId == null) {
            _duplicate.groupInstanceId = null;
        } else {
            _duplicate.groupInstanceId = groupInstanceId;
        }
        if (protocolType == null) {
            _duplicate.protocolType = null;
        } else {
            _duplicate.protocolType = protocolType;
        }
        if (protocolName == null) {
            _duplicate.protocolName = null;
        } else {
            _duplicate.protocolName = protocolName;
        }
        ArrayList<SyncGroupRequestAssignment> newAssignments = new ArrayList<SyncGroupRequestAssignment>(assignments.size());
        for (SyncGroupRequestAssignment _element : assignments) {
            newAssignments.add(_element.duplicate());
        }
        _duplicate.assignments = newAssignments;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "SyncGroupRequestData("
            + "groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
            + ", generationId=" + generationId
            + ", memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
            + ", groupInstanceId=" + ((groupInstanceId == null) ? "null" : "'" + groupInstanceId.toString() + "'")
            + ", protocolType=" + ((protocolType == null) ? "null" : "'" + protocolType.toString() + "'")
            + ", protocolName=" + ((protocolName == null) ? "null" : "'" + protocolName.toString() + "'")
            + ", assignments=" + MessageUtil.deepToString(assignments.iterator())
            + ")";
    }
    
    public String groupId() {
        return this.groupId;
    }
    
    public int generationId() {
        return this.generationId;
    }
    
    public String memberId() {
        return this.memberId;
    }
    
    public String groupInstanceId() {
        return this.groupInstanceId;
    }
    
    public String protocolType() {
        return this.protocolType;
    }
    
    public String protocolName() {
        return this.protocolName;
    }
    
    public List<SyncGroupRequestAssignment> assignments() {
        return this.assignments;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public SyncGroupRequestData setGroupId(String v) {
        this.groupId = v;
        return this;
    }
    
    public SyncGroupRequestData setGenerationId(int v) {
        this.generationId = v;
        return this;
    }
    
    public SyncGroupRequestData setMemberId(String v) {
        this.memberId = v;
        return this;
    }
    
    public SyncGroupRequestData setGroupInstanceId(String v) {
        this.groupInstanceId = v;
        return this;
    }
    
    public SyncGroupRequestData setProtocolType(String v) {
        this.protocolType = v;
        return this;
    }
    
    public SyncGroupRequestData setProtocolName(String v) {
        this.protocolName = v;
        return this;
    }
    
    public SyncGroupRequestData setAssignments(List<SyncGroupRequestAssignment> v) {
        this.assignments = v;
        return this;
    }
    
    public static class SyncGroupRequestAssignment implements Message {
        String memberId;
        byte[] assignment;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("member_id", Type.STRING, "The ID of the member to assign."),
                new Field("assignment", Type.BYTES, "The member assignment.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 =
            new Schema(
                new Field("member_id", Type.COMPACT_STRING, "The ID of the member to assign."),
                new Field("assignment", Type.COMPACT_BYTES, "The member assignment."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
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
        
        public SyncGroupRequestAssignment(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public SyncGroupRequestAssignment() {
            this.memberId = "";
            this.assignment = Bytes.EMPTY;
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of SyncGroupRequestAssignment");
            }
            {
                int length;
                if (_version >= 4) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field memberId was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field memberId had invalid length " + length);
                } else {
                    this.memberId = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 4) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readInt();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field assignment was serialized as null");
                } else {
                    byte[] newBytes = new byte[length];
                    _readable.readArray(newBytes);
                    this.assignment = newBytes;
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 4) {
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
            {
                byte[] _stringBytes = _cache.getSerializedValue(memberId);
                if (_version >= 4) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 4) {
                _writable.writeUnsignedVarint(assignment.length + 1);
            } else {
                _writable.writeInt(assignment.length);
            }
            _writable.writeByteArray(assignment);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 4) {
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
            if (_version > 5) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of SyncGroupRequestAssignment");
            }
            {
                byte[] _stringBytes = memberId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'memberId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(memberId, _stringBytes);
                if (_version >= 4) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                _size.addBytes(assignment.length);
                if (_version >= 4) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(assignment.length + 1));
                } else {
                    _size.addBytes(4);
                }
            }
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                    _size.addBytes(_field.size());
                }
            }
            if (_version >= 4) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SyncGroupRequestAssignment)) return false;
            SyncGroupRequestAssignment other = (SyncGroupRequestAssignment) obj;
            if (this.memberId == null) {
                if (other.memberId != null) return false;
            } else {
                if (!this.memberId.equals(other.memberId)) return false;
            }
            if (!Arrays.equals(this.assignment, other.assignment)) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (memberId == null ? 0 : memberId.hashCode());
            hashCode = 31 * hashCode + Arrays.hashCode(assignment);
            return hashCode;
        }
        
        @Override
        public SyncGroupRequestAssignment duplicate() {
            SyncGroupRequestAssignment _duplicate = new SyncGroupRequestAssignment();
            _duplicate.memberId = memberId;
            _duplicate.assignment = MessageUtil.duplicate(assignment);
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "SyncGroupRequestAssignment("
                + "memberId=" + ((memberId == null) ? "null" : "'" + memberId.toString() + "'")
                + ", assignment=" + Arrays.toString(assignment)
                + ")";
        }
        
        public String memberId() {
            return this.memberId;
        }
        
        public byte[] assignment() {
            return this.assignment;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public SyncGroupRequestAssignment setMemberId(String v) {
            this.memberId = v;
            return this;
        }
        
        public SyncGroupRequestAssignment setAssignment(byte[] v) {
            this.assignment = v;
            return this;
        }
    }
}
