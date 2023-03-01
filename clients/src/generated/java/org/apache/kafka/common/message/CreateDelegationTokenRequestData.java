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

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class CreateDelegationTokenRequestData implements ApiMessage {
    List<CreatableRenewers> renewers;
    long maxLifetimeMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("renewers", new ArrayOf(CreatableRenewers.SCHEMA_0), "A list of those who are allowed to renew this token before it expires."),
            new Field("max_lifetime_ms", Type.INT64, "The maximum lifetime of the token in milliseconds, or -1 to use the server side default.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("renewers", new CompactArrayOf(CreatableRenewers.SCHEMA_2), "A list of those who are allowed to renew this token before it expires."),
            new Field("max_lifetime_ms", Type.INT64, "The maximum lifetime of the token in milliseconds, or -1 to use the server side default."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 2;
    
    public CreateDelegationTokenRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public CreateDelegationTokenRequestData() {
        this.renewers = new ArrayList<CreatableRenewers>(0);
        this.maxLifetimeMs = 0L;
    }
    
    @Override
    public short apiKey() {
        return 38;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 2;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field renewers was serialized as null");
                } else {
                    ArrayList<CreatableRenewers> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableRenewers(_readable, _version));
                    }
                    this.renewers = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field renewers was serialized as null");
                } else {
                    ArrayList<CreatableRenewers> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableRenewers(_readable, _version));
                    }
                    this.renewers = newCollection;
                }
            }
        }
        this.maxLifetimeMs = _readable.readLong();
        this._unknownTaggedFields = null;
        if (_version >= 2) {
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
        if (_version >= 2) {
            _writable.writeUnsignedVarint(renewers.size() + 1);
            for (CreatableRenewers renewersElement : renewers) {
                renewersElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(renewers.size());
            for (CreatableRenewers renewersElement : renewers) {
                renewersElement.write(_writable, _cache, _version);
            }
        }
        _writable.writeLong(maxLifetimeMs);
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 2) {
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
            if (_version >= 2) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(renewers.size() + 1));
            } else {
                _size.addBytes(4);
            }
            for (CreatableRenewers renewersElement : renewers) {
                renewersElement.addSize(_size, _cache, _version);
            }
        }
        _size.addBytes(8);
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                _size.addBytes(_field.size());
            }
        }
        if (_version >= 2) {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CreateDelegationTokenRequestData)) return false;
        CreateDelegationTokenRequestData other = (CreateDelegationTokenRequestData) obj;
        if (this.renewers == null) {
            if (other.renewers != null) return false;
        } else {
            if (!this.renewers.equals(other.renewers)) return false;
        }
        if (maxLifetimeMs != other.maxLifetimeMs) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (renewers == null ? 0 : renewers.hashCode());
        hashCode = 31 * hashCode + ((int) (maxLifetimeMs >> 32) ^ (int) maxLifetimeMs);
        return hashCode;
    }
    
    @Override
    public CreateDelegationTokenRequestData duplicate() {
        CreateDelegationTokenRequestData _duplicate = new CreateDelegationTokenRequestData();
        ArrayList<CreatableRenewers> newRenewers = new ArrayList<CreatableRenewers>(renewers.size());
        for (CreatableRenewers _element : renewers) {
            newRenewers.add(_element.duplicate());
        }
        _duplicate.renewers = newRenewers;
        _duplicate.maxLifetimeMs = maxLifetimeMs;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "CreateDelegationTokenRequestData("
            + "renewers=" + MessageUtil.deepToString(renewers.iterator())
            + ", maxLifetimeMs=" + maxLifetimeMs
            + ")";
    }
    
    public List<CreatableRenewers> renewers() {
        return this.renewers;
    }
    
    public long maxLifetimeMs() {
        return this.maxLifetimeMs;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public CreateDelegationTokenRequestData setRenewers(List<CreatableRenewers> v) {
        this.renewers = v;
        return this;
    }
    
    public CreateDelegationTokenRequestData setMaxLifetimeMs(long v) {
        this.maxLifetimeMs = v;
        return this;
    }
    
    public static class CreatableRenewers implements Message {
        String principalType;
        String principalName;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("principal_type", Type.STRING, "The type of the Kafka principal."),
                new Field("principal_name", Type.STRING, "The name of the Kafka principal.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("principal_type", Type.COMPACT_STRING, "The type of the Kafka principal."),
                new Field("principal_name", Type.COMPACT_STRING, "The name of the Kafka principal."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 2;
        
        public CreatableRenewers(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public CreatableRenewers() {
            this.principalType = "";
            this.principalName = "";
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableRenewers");
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalType was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalType had invalid length " + length);
                } else {
                    this.principalType = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalName had invalid length " + length);
                } else {
                    this.principalName = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 2) {
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
                byte[] _stringBytes = _cache.getSerializedValue(principalType);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(principalName);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of CreatableRenewers");
            }
            {
                byte[] _stringBytes = principalType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalType, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                byte[] _stringBytes = principalName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalName, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
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
            if (_version >= 2) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CreatableRenewers)) return false;
            CreatableRenewers other = (CreatableRenewers) obj;
            if (this.principalType == null) {
                if (other.principalType != null) return false;
            } else {
                if (!this.principalType.equals(other.principalType)) return false;
            }
            if (this.principalName == null) {
                if (other.principalName != null) return false;
            } else {
                if (!this.principalName.equals(other.principalName)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (principalType == null ? 0 : principalType.hashCode());
            hashCode = 31 * hashCode + (principalName == null ? 0 : principalName.hashCode());
            return hashCode;
        }
        
        @Override
        public CreatableRenewers duplicate() {
            CreatableRenewers _duplicate = new CreatableRenewers();
            _duplicate.principalType = principalType;
            _duplicate.principalName = principalName;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "CreatableRenewers("
                + "principalType=" + ((principalType == null) ? "null" : "'" + principalType.toString() + "'")
                + ", principalName=" + ((principalName == null) ? "null" : "'" + principalName.toString() + "'")
                + ")";
        }
        
        public String principalType() {
            return this.principalType;
        }
        
        public String principalName() {
            return this.principalName;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public CreatableRenewers setPrincipalType(String v) {
            this.principalType = v;
            return this;
        }
        
        public CreatableRenewers setPrincipalName(String v) {
            this.principalName = v;
            return this;
        }
    }
}
