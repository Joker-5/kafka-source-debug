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


public class OffsetFetchResponseData implements ApiMessage {
    int throttleTimeMs;
    List<OffsetFetchResponseTopic> topics;
    short errorCode;
    List<OffsetFetchResponseGroup> groups;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_0), "The responses per topic.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_0), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_0), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new ArrayOf(OffsetFetchResponseTopic.SCHEMA_5), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error.")
        );
    
    public static final Schema SCHEMA_6 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("topics", new CompactArrayOf(OffsetFetchResponseTopic.SCHEMA_6), "The responses per topic."),
            new Field("error_code", Type.INT16, "The top-level error code, or 0 if there was no error."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema SCHEMA_7 = SCHEMA_6;
    
    public static final Schema SCHEMA_8 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("groups", new CompactArrayOf(OffsetFetchResponseGroup.SCHEMA_8), "The responses per group id."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5,
        SCHEMA_6,
        SCHEMA_7,
        SCHEMA_8
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 8;
    
    public OffsetFetchResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public OffsetFetchResponseData() {
        this.throttleTimeMs = 0;
        this.topics = new ArrayList<OffsetFetchResponseTopic>(0);
        this.errorCode = (short) 0;
        this.groups = new ArrayList<OffsetFetchResponseGroup>(0);
    }
    
    @Override
    public short apiKey() {
        return 9;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 8;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        if (_version >= 3) {
            this.throttleTimeMs = _readable.readInt();
        } else {
            this.throttleTimeMs = 0;
        }
        if (_version <= 7) {
            if (_version >= 6) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<OffsetFetchResponseTopic> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchResponseTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<OffsetFetchResponseTopic> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchResponseTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        } else {
            this.topics = new ArrayList<OffsetFetchResponseTopic>(0);
        }
        if ((_version >= 2) && (_version <= 7)) {
            this.errorCode = _readable.readShort();
        } else {
            this.errorCode = (short) 0;
        }
        if (_version >= 8) {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field groups was serialized as null");
            } else {
                ArrayList<OffsetFetchResponseGroup> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new OffsetFetchResponseGroup(_readable, _version));
                }
                this.groups = newCollection;
            }
        } else {
            this.groups = new ArrayList<OffsetFetchResponseGroup>(0);
        }
        this._unknownTaggedFields = null;
        if (_version >= 6) {
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
        if (_version >= 3) {
            _writable.writeInt(throttleTimeMs);
        }
        if (_version <= 7) {
            if (_version >= 6) {
                _writable.writeUnsignedVarint(topics.size() + 1);
                for (OffsetFetchResponseTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(topics.size());
                for (OffsetFetchResponseTopic topicsElement : topics) {
                    topicsElement.write(_writable, _cache, _version);
                }
            }
        } else {
            if (!this.topics.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default topics at version " + _version);
            }
        }
        if ((_version >= 2) && (_version <= 7)) {
            _writable.writeShort(errorCode);
        }
        if (_version >= 8) {
            _writable.writeUnsignedVarint(groups.size() + 1);
            for (OffsetFetchResponseGroup groupsElement : groups) {
                groupsElement.write(_writable, _cache, _version);
            }
        } else {
            if (!this.groups.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default groups at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 6) {
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
        if (_version >= 3) {
            _size.addBytes(4);
        }
        if (_version <= 7) {
            {
                if (_version >= 6) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(topics.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                for (OffsetFetchResponseTopic topicsElement : topics) {
                    topicsElement.addSize(_size, _cache, _version);
                }
            }
        }
        if ((_version >= 2) && (_version <= 7)) {
            _size.addBytes(2);
        }
        if (_version >= 8) {
            {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(groups.size() + 1));
                for (OffsetFetchResponseGroup groupsElement : groups) {
                    groupsElement.addSize(_size, _cache, _version);
                }
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
        if (_version >= 6) {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OffsetFetchResponseData)) return false;
        OffsetFetchResponseData other = (OffsetFetchResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (errorCode != other.errorCode) return false;
        if (this.groups == null) {
            if (other.groups != null) return false;
        } else {
            if (!this.groups.equals(other.groups)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (groups == null ? 0 : groups.hashCode());
        return hashCode;
    }
    
    @Override
    public OffsetFetchResponseData duplicate() {
        OffsetFetchResponseData _duplicate = new OffsetFetchResponseData();
        _duplicate.throttleTimeMs = throttleTimeMs;
        ArrayList<OffsetFetchResponseTopic> newTopics = new ArrayList<OffsetFetchResponseTopic>(topics.size());
        for (OffsetFetchResponseTopic _element : topics) {
            newTopics.add(_element.duplicate());
        }
        _duplicate.topics = newTopics;
        _duplicate.errorCode = errorCode;
        ArrayList<OffsetFetchResponseGroup> newGroups = new ArrayList<OffsetFetchResponseGroup>(groups.size());
        for (OffsetFetchResponseGroup _element : groups) {
            newGroups.add(_element.duplicate());
        }
        _duplicate.groups = newGroups;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "OffsetFetchResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", topics=" + MessageUtil.deepToString(topics.iterator())
            + ", errorCode=" + errorCode
            + ", groups=" + MessageUtil.deepToString(groups.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<OffsetFetchResponseTopic> topics() {
        return this.topics;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<OffsetFetchResponseGroup> groups() {
        return this.groups;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public OffsetFetchResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public OffsetFetchResponseData setTopics(List<OffsetFetchResponseTopic> v) {
        this.topics = v;
        return this;
    }
    
    public OffsetFetchResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public OffsetFetchResponseData setGroups(List<OffsetFetchResponseGroup> v) {
        this.groups = v;
        return this;
    }
    
    public static class OffsetFetchResponseTopic implements Message {
        String name;
        List<OffsetFetchResponsePartition> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetFetchResponsePartition.SCHEMA_0), "The responses per partition")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("partitions", new ArrayOf(OffsetFetchResponsePartition.SCHEMA_5), "The responses per partition")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("partitions", new CompactArrayOf(OffsetFetchResponsePartition.SCHEMA_6), "The responses per partition"),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 7;
        
        public OffsetFetchResponseTopic(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponseTopic() {
            this.name = "";
            this.partitions = new ArrayList<OffsetFetchResponsePartition>(0);
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 8;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
            }
            {
                if (_version >= 6) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<OffsetFetchResponsePartition> newCollection = new ArrayList<>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new OffsetFetchResponsePartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field partitions was serialized as null");
                    } else {
                        ArrayList<OffsetFetchResponsePartition> newCollection = new ArrayList<>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new OffsetFetchResponsePartition(_readable, _version));
                        }
                        this.partitions = newCollection;
                    }
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponseTopic");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 6) {
                _writable.writeUnsignedVarint(partitions.size() + 1);
                for (OffsetFetchResponsePartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(partitions.size());
                for (OffsetFetchResponsePartition partitionsElement : partitions) {
                    partitionsElement.write(_writable, _cache, _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 6) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                if (_version >= 6) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                for (OffsetFetchResponsePartition partitionsElement : partitions) {
                    partitionsElement.addSize(_size, _cache, _version);
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
            if (_version >= 6) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OffsetFetchResponseTopic)) return false;
            OffsetFetchResponseTopic other = (OffsetFetchResponseTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.partitions == null) {
                if (other.partitions != null) return false;
            } else {
                if (!this.partitions.equals(other.partitions)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (partitions == null ? 0 : partitions.hashCode());
            return hashCode;
        }
        
        @Override
        public OffsetFetchResponseTopic duplicate() {
            OffsetFetchResponseTopic _duplicate = new OffsetFetchResponseTopic();
            _duplicate.name = name;
            ArrayList<OffsetFetchResponsePartition> newPartitions = new ArrayList<OffsetFetchResponsePartition>(partitions.size());
            for (OffsetFetchResponsePartition _element : partitions) {
                newPartitions.add(_element.duplicate());
            }
            _duplicate.partitions = newPartitions;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "OffsetFetchResponseTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<OffsetFetchResponsePartition> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponseTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetFetchResponseTopic setPartitions(List<OffsetFetchResponsePartition> v) {
            this.partitions = v;
            return this;
        }
    }
    
    public static class OffsetFetchResponsePartition implements Message {
        int partitionIndex;
        long committedOffset;
        int committedLeaderEpoch;
        String metadata;
        short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("metadata", Type.NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch."),
                new Field("metadata", Type.NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error.")
            );
        
        public static final Schema SCHEMA_6 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch."),
                new Field("metadata", Type.COMPACT_NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_7 = SCHEMA_6;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6,
            SCHEMA_7
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 7;
        
        public OffsetFetchResponsePartition(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponsePartition() {
            this.partitionIndex = 0;
            this.committedOffset = 0L;
            this.committedLeaderEpoch = -1;
            this.metadata = "";
            this.errorCode = (short) 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 7;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            this.partitionIndex = _readable.readInt();
            this.committedOffset = _readable.readLong();
            if (_version >= 5) {
                this.committedLeaderEpoch = _readable.readInt();
            } else {
                this.committedLeaderEpoch = -1;
            }
            {
                int length;
                if (_version >= 6) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.metadata = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field metadata had invalid length " + length);
                } else {
                    this.metadata = _readable.readString(length);
                }
            }
            this.errorCode = _readable.readShort();
            this._unknownTaggedFields = null;
            if (_version >= 6) {
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
            _writable.writeInt(partitionIndex);
            _writable.writeLong(committedOffset);
            if (_version >= 5) {
                _writable.writeInt(committedLeaderEpoch);
            }
            if (metadata == null) {
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(metadata);
                if (_version >= 6) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeShort(errorCode);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 6) {
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
            _size.addBytes(4);
            _size.addBytes(8);
            if (_version >= 5) {
                _size.addBytes(4);
            }
            if (metadata == null) {
                if (_version >= 6) {
                    _size.addBytes(1);
                } else {
                    _size.addBytes(2);
                }
            } else {
                byte[] _stringBytes = metadata.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'metadata' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(metadata, _stringBytes);
                if (_version >= 6) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            _size.addBytes(2);
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                    _size.addBytes(_field.size());
                }
            }
            if (_version >= 6) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OffsetFetchResponsePartition)) return false;
            OffsetFetchResponsePartition other = (OffsetFetchResponsePartition) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (committedOffset != other.committedOffset) return false;
            if (committedLeaderEpoch != other.committedLeaderEpoch) return false;
            if (this.metadata == null) {
                if (other.metadata != null) return false;
            } else {
                if (!this.metadata.equals(other.metadata)) return false;
            }
            if (errorCode != other.errorCode) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + ((int) (committedOffset >> 32) ^ (int) committedOffset);
            hashCode = 31 * hashCode + committedLeaderEpoch;
            hashCode = 31 * hashCode + (metadata == null ? 0 : metadata.hashCode());
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public OffsetFetchResponsePartition duplicate() {
            OffsetFetchResponsePartition _duplicate = new OffsetFetchResponsePartition();
            _duplicate.partitionIndex = partitionIndex;
            _duplicate.committedOffset = committedOffset;
            _duplicate.committedLeaderEpoch = committedLeaderEpoch;
            if (metadata == null) {
                _duplicate.metadata = null;
            } else {
                _duplicate.metadata = metadata;
            }
            _duplicate.errorCode = errorCode;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "OffsetFetchResponsePartition("
                + "partitionIndex=" + partitionIndex
                + ", committedOffset=" + committedOffset
                + ", committedLeaderEpoch=" + committedLeaderEpoch
                + ", metadata=" + ((metadata == null) ? "null" : "'" + metadata.toString() + "'")
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public long committedOffset() {
            return this.committedOffset;
        }
        
        public int committedLeaderEpoch() {
            return this.committedLeaderEpoch;
        }
        
        public String metadata() {
            return this.metadata;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponsePartition setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setCommittedOffset(long v) {
            this.committedOffset = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setCommittedLeaderEpoch(int v) {
            this.committedLeaderEpoch = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setMetadata(String v) {
            this.metadata = v;
            return this;
        }
        
        public OffsetFetchResponsePartition setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
    
    public static class OffsetFetchResponseGroup implements Message {
        String groupId;
        List<OffsetFetchResponseTopics> topics;
        short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("group_id", Type.COMPACT_STRING, "The group ID."),
                new Field("topics", new CompactArrayOf(OffsetFetchResponseTopics.SCHEMA_8), "The responses per topic."),
                new Field("error_code", Type.INT16, "The group-level error code, or 0 if there was no error."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_8
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 8;
        public static final short HIGHEST_SUPPORTED_VERSION = 8;
        
        public OffsetFetchResponseGroup(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponseGroup() {
            this.groupId = "";
            this.topics = new ArrayList<OffsetFetchResponseTopics>(0);
            this.errorCode = (short) 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 8;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponseGroup");
            }
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    throw new RuntimeException("non-nullable field groupId was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field groupId had invalid length " + length);
                } else {
                    this.groupId = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    ArrayList<OffsetFetchResponseTopics> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchResponseTopics(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
            this.errorCode = _readable.readShort();
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
            if (_version < 8) {
                throw new UnsupportedVersionException("Can't write version " + _version + " of OffsetFetchResponseGroup");
            }
            int _numTaggedFields = 0;
            {
                byte[] _stringBytes = _cache.getSerializedValue(groupId);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (OffsetFetchResponseTopics topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
            _writable.writeShort(errorCode);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetFetchResponseGroup");
            }
            {
                byte[] _stringBytes = groupId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'groupId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(groupId, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(topics.size() + 1));
                for (OffsetFetchResponseTopics topicsElement : topics) {
                    topicsElement.addSize(_size, _cache, _version);
                }
            }
            _size.addBytes(2);
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
            if (!(obj instanceof OffsetFetchResponseGroup)) return false;
            OffsetFetchResponseGroup other = (OffsetFetchResponseGroup) obj;
            if (this.groupId == null) {
                if (other.groupId != null) return false;
            } else {
                if (!this.groupId.equals(other.groupId)) return false;
            }
            if (this.topics == null) {
                if (other.topics != null) return false;
            } else {
                if (!this.topics.equals(other.topics)) return false;
            }
            if (errorCode != other.errorCode) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (groupId == null ? 0 : groupId.hashCode());
            hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public OffsetFetchResponseGroup duplicate() {
            OffsetFetchResponseGroup _duplicate = new OffsetFetchResponseGroup();
            _duplicate.groupId = groupId;
            ArrayList<OffsetFetchResponseTopics> newTopics = new ArrayList<OffsetFetchResponseTopics>(topics.size());
            for (OffsetFetchResponseTopics _element : topics) {
                newTopics.add(_element.duplicate());
            }
            _duplicate.topics = newTopics;
            _duplicate.errorCode = errorCode;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "OffsetFetchResponseGroup("
                + "groupId=" + ((groupId == null) ? "null" : "'" + groupId.toString() + "'")
                + ", topics=" + MessageUtil.deepToString(topics.iterator())
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public String groupId() {
            return this.groupId;
        }
        
        public List<OffsetFetchResponseTopics> topics() {
            return this.topics;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponseGroup setGroupId(String v) {
            this.groupId = v;
            return this;
        }
        
        public OffsetFetchResponseGroup setTopics(List<OffsetFetchResponseTopics> v) {
            this.topics = v;
            return this;
        }
        
        public OffsetFetchResponseGroup setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
    
    public static class OffsetFetchResponseTopics implements Message {
        String name;
        List<OffsetFetchResponsePartitions> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("partitions", new CompactArrayOf(OffsetFetchResponsePartitions.SCHEMA_8), "The responses per partition"),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_8
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 8;
        public static final short HIGHEST_SUPPORTED_VERSION = 8;
        
        public OffsetFetchResponseTopics(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponseTopics() {
            this.name = "";
            this.partitions = new ArrayList<OffsetFetchResponsePartitions>(0);
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 8;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 8;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponseTopics");
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
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitions was serialized as null");
                } else {
                    ArrayList<OffsetFetchResponsePartitions> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new OffsetFetchResponsePartitions(_readable, _version));
                    }
                    this.partitions = newCollection;
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
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeUnsignedVarint(partitions.size() + 1);
            for (OffsetFetchResponsePartitions partitionsElement : partitions) {
                partitionsElement.write(_writable, _cache, _version);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetFetchResponseTopics");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1));
                for (OffsetFetchResponsePartitions partitionsElement : partitions) {
                    partitionsElement.addSize(_size, _cache, _version);
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
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OffsetFetchResponseTopics)) return false;
            OffsetFetchResponseTopics other = (OffsetFetchResponseTopics) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.partitions == null) {
                if (other.partitions != null) return false;
            } else {
                if (!this.partitions.equals(other.partitions)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + (partitions == null ? 0 : partitions.hashCode());
            return hashCode;
        }
        
        @Override
        public OffsetFetchResponseTopics duplicate() {
            OffsetFetchResponseTopics _duplicate = new OffsetFetchResponseTopics();
            _duplicate.name = name;
            ArrayList<OffsetFetchResponsePartitions> newPartitions = new ArrayList<OffsetFetchResponsePartitions>(partitions.size());
            for (OffsetFetchResponsePartitions _element : partitions) {
                newPartitions.add(_element.duplicate());
            }
            _duplicate.partitions = newPartitions;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "OffsetFetchResponseTopics("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public List<OffsetFetchResponsePartitions> partitions() {
            return this.partitions;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponseTopics setName(String v) {
            this.name = v;
            return this;
        }
        
        public OffsetFetchResponseTopics setPartitions(List<OffsetFetchResponsePartitions> v) {
            this.partitions = v;
            return this;
        }
    }
    
    public static class OffsetFetchResponsePartitions implements Message {
        int partitionIndex;
        long committedOffset;
        int committedLeaderEpoch;
        String metadata;
        short errorCode;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_8 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("committed_offset", Type.INT64, "The committed message offset."),
                new Field("committed_leader_epoch", Type.INT32, "The leader epoch."),
                new Field("metadata", Type.COMPACT_NULLABLE_STRING, "The partition metadata."),
                new Field("error_code", Type.INT16, "The partition-level error code, or 0 if there was no error."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_8
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 8;
        public static final short HIGHEST_SUPPORTED_VERSION = 8;
        
        public OffsetFetchResponsePartitions(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public OffsetFetchResponsePartitions() {
            this.partitionIndex = 0;
            this.committedOffset = 0L;
            this.committedLeaderEpoch = -1;
            this.metadata = "";
            this.errorCode = (short) 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 8;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 8;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of OffsetFetchResponsePartitions");
            }
            this.partitionIndex = _readable.readInt();
            this.committedOffset = _readable.readLong();
            this.committedLeaderEpoch = _readable.readInt();
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    this.metadata = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field metadata had invalid length " + length);
                } else {
                    this.metadata = _readable.readString(length);
                }
            }
            this.errorCode = _readable.readShort();
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
            _writable.writeInt(partitionIndex);
            _writable.writeLong(committedOffset);
            _writable.writeInt(committedLeaderEpoch);
            if (metadata == null) {
                _writable.writeUnsignedVarint(0);
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(metadata);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeShort(errorCode);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 8) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of OffsetFetchResponsePartitions");
            }
            _size.addBytes(4);
            _size.addBytes(8);
            _size.addBytes(4);
            if (metadata == null) {
                _size.addBytes(1);
            } else {
                byte[] _stringBytes = metadata.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'metadata' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(metadata, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            _size.addBytes(2);
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
            if (!(obj instanceof OffsetFetchResponsePartitions)) return false;
            OffsetFetchResponsePartitions other = (OffsetFetchResponsePartitions) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (committedOffset != other.committedOffset) return false;
            if (committedLeaderEpoch != other.committedLeaderEpoch) return false;
            if (this.metadata == null) {
                if (other.metadata != null) return false;
            } else {
                if (!this.metadata.equals(other.metadata)) return false;
            }
            if (errorCode != other.errorCode) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            hashCode = 31 * hashCode + ((int) (committedOffset >> 32) ^ (int) committedOffset);
            hashCode = 31 * hashCode + committedLeaderEpoch;
            hashCode = 31 * hashCode + (metadata == null ? 0 : metadata.hashCode());
            hashCode = 31 * hashCode + errorCode;
            return hashCode;
        }
        
        @Override
        public OffsetFetchResponsePartitions duplicate() {
            OffsetFetchResponsePartitions _duplicate = new OffsetFetchResponsePartitions();
            _duplicate.partitionIndex = partitionIndex;
            _duplicate.committedOffset = committedOffset;
            _duplicate.committedLeaderEpoch = committedLeaderEpoch;
            if (metadata == null) {
                _duplicate.metadata = null;
            } else {
                _duplicate.metadata = metadata;
            }
            _duplicate.errorCode = errorCode;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "OffsetFetchResponsePartitions("
                + "partitionIndex=" + partitionIndex
                + ", committedOffset=" + committedOffset
                + ", committedLeaderEpoch=" + committedLeaderEpoch
                + ", metadata=" + ((metadata == null) ? "null" : "'" + metadata.toString() + "'")
                + ", errorCode=" + errorCode
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public long committedOffset() {
            return this.committedOffset;
        }
        
        public int committedLeaderEpoch() {
            return this.committedLeaderEpoch;
        }
        
        public String metadata() {
            return this.metadata;
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public OffsetFetchResponsePartitions setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public OffsetFetchResponsePartitions setCommittedOffset(long v) {
            this.committedOffset = v;
            return this;
        }
        
        public OffsetFetchResponsePartitions setCommittedLeaderEpoch(int v) {
            this.committedLeaderEpoch = v;
            return this;
        }
        
        public OffsetFetchResponsePartitions setMetadata(String v) {
            this.metadata = v;
            return this;
        }
        
        public OffsetFetchResponsePartitions setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
    }
}
