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

package  org.apache.kafka.server.log.remote.metadata.storage.generated;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;
import org.apache.kafka.common.protocol.Message;
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


public class RemoteLogSegmentMetadataUpdateRecord implements ApiMessage {
    RemoteLogSegmentIdEntry remoteLogSegmentId;
    int brokerId;
    long eventTimestampMs;
    byte remoteLogSegmentState;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("remote_log_segment_id", RemoteLogSegmentIdEntry.SCHEMA_0, "Unique representation of the remote log segment."),
            new Field("broker_id", Type.INT32, "Broker id from which this event is generated."),
            new Field("event_timestamp_ms", Type.INT64, "Epoch time in milli seconds at which this event is generated."),
            new Field("remote_log_segment_state", Type.INT8, "State identifier of the remote log segment, which is RemoteLogSegmentState.id()."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public RemoteLogSegmentMetadataUpdateRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public RemoteLogSegmentMetadataUpdateRecord() {
        this.remoteLogSegmentId = new RemoteLogSegmentIdEntry();
        this.brokerId = 0;
        this.eventTimestampMs = 0L;
        this.remoteLogSegmentState = (byte) 0;
    }
    
    @Override
    public short apiKey() {
        return 1;
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
        {
            this.remoteLogSegmentId = new RemoteLogSegmentIdEntry(_readable, _version);
        }
        this.brokerId = _readable.readInt();
        this.eventTimestampMs = _readable.readLong();
        this.remoteLogSegmentState = _readable.readByte();
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
        remoteLogSegmentId.write(_writable, _cache, _version);
        _writable.writeInt(brokerId);
        _writable.writeLong(eventTimestampMs);
        _writable.writeByte(remoteLogSegmentState);
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        {
            int _sizeBeforeStruct = _size.totalSize();
            this.remoteLogSegmentId.addSize(_size, _cache, _version);
            int _structSize = _size.totalSize() - _sizeBeforeStruct;
        }
        _size.addBytes(4);
        _size.addBytes(8);
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
        if (!(obj instanceof RemoteLogSegmentMetadataUpdateRecord)) return false;
        RemoteLogSegmentMetadataUpdateRecord other = (RemoteLogSegmentMetadataUpdateRecord) obj;
        if (this.remoteLogSegmentId == null) {
            if (other.remoteLogSegmentId != null) return false;
        } else {
            if (!this.remoteLogSegmentId.equals(other.remoteLogSegmentId)) return false;
        }
        if (brokerId != other.brokerId) return false;
        if (eventTimestampMs != other.eventTimestampMs) return false;
        if (remoteLogSegmentState != other.remoteLogSegmentState) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (remoteLogSegmentId == null ? 0 : remoteLogSegmentId.hashCode());
        hashCode = 31 * hashCode + brokerId;
        hashCode = 31 * hashCode + ((int) (eventTimestampMs >> 32) ^ (int) eventTimestampMs);
        hashCode = 31 * hashCode + remoteLogSegmentState;
        return hashCode;
    }
    
    @Override
    public RemoteLogSegmentMetadataUpdateRecord duplicate() {
        RemoteLogSegmentMetadataUpdateRecord _duplicate = new RemoteLogSegmentMetadataUpdateRecord();
        _duplicate.remoteLogSegmentId = remoteLogSegmentId.duplicate();
        _duplicate.brokerId = brokerId;
        _duplicate.eventTimestampMs = eventTimestampMs;
        _duplicate.remoteLogSegmentState = remoteLogSegmentState;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "RemoteLogSegmentMetadataUpdateRecord("
            + "remoteLogSegmentId=" + remoteLogSegmentId.toString()
            + ", brokerId=" + brokerId
            + ", eventTimestampMs=" + eventTimestampMs
            + ", remoteLogSegmentState=" + remoteLogSegmentState
            + ")";
    }
    
    public RemoteLogSegmentIdEntry remoteLogSegmentId() {
        return this.remoteLogSegmentId;
    }
    
    public int brokerId() {
        return this.brokerId;
    }
    
    public long eventTimestampMs() {
        return this.eventTimestampMs;
    }
    
    public byte remoteLogSegmentState() {
        return this.remoteLogSegmentState;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public RemoteLogSegmentMetadataUpdateRecord setRemoteLogSegmentId(RemoteLogSegmentIdEntry v) {
        this.remoteLogSegmentId = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataUpdateRecord setBrokerId(int v) {
        this.brokerId = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataUpdateRecord setEventTimestampMs(long v) {
        this.eventTimestampMs = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataUpdateRecord setRemoteLogSegmentState(byte v) {
        this.remoteLogSegmentState = v;
        return this;
    }
    
    public static class RemoteLogSegmentIdEntry implements Message {
        TopicIdPartitionEntry topicIdPartition;
        Uuid id;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic_id_partition", TopicIdPartitionEntry.SCHEMA_0, "Represents unique topic partition."),
                new Field("id", Type.UUID, "Unique identifier of the remote log segment."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public RemoteLogSegmentIdEntry(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public RemoteLogSegmentIdEntry() {
            this.topicIdPartition = new TopicIdPartitionEntry();
            this.id = Uuid.ZERO_UUID;
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
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of RemoteLogSegmentIdEntry");
            }
            {
                this.topicIdPartition = new TopicIdPartitionEntry(_readable, _version);
            }
            this.id = _readable.readUuid();
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
            topicIdPartition.write(_writable, _cache, _version);
            _writable.writeUuid(id);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of RemoteLogSegmentIdEntry");
            }
            {
                int _sizeBeforeStruct = _size.totalSize();
                this.topicIdPartition.addSize(_size, _cache, _version);
                int _structSize = _size.totalSize() - _sizeBeforeStruct;
            }
            _size.addBytes(16);
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
            if (!(obj instanceof RemoteLogSegmentIdEntry)) return false;
            RemoteLogSegmentIdEntry other = (RemoteLogSegmentIdEntry) obj;
            if (this.topicIdPartition == null) {
                if (other.topicIdPartition != null) return false;
            } else {
                if (!this.topicIdPartition.equals(other.topicIdPartition)) return false;
            }
            if (!this.id.equals(other.id)) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (topicIdPartition == null ? 0 : topicIdPartition.hashCode());
            hashCode = 31 * hashCode + id.hashCode();
            return hashCode;
        }
        
        @Override
        public RemoteLogSegmentIdEntry duplicate() {
            RemoteLogSegmentIdEntry _duplicate = new RemoteLogSegmentIdEntry();
            _duplicate.topicIdPartition = topicIdPartition.duplicate();
            _duplicate.id = id;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "RemoteLogSegmentIdEntry("
                + "topicIdPartition=" + topicIdPartition.toString()
                + ", id=" + id.toString()
                + ")";
        }
        
        public TopicIdPartitionEntry topicIdPartition() {
            return this.topicIdPartition;
        }
        
        public Uuid id() {
            return this.id;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public RemoteLogSegmentIdEntry setTopicIdPartition(TopicIdPartitionEntry v) {
            this.topicIdPartition = v;
            return this;
        }
        
        public RemoteLogSegmentIdEntry setId(Uuid v) {
            this.id = v;
            return this;
        }
    }
    
    public static class TopicIdPartitionEntry implements Message {
        String name;
        Uuid id;
        int partition;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "Topic name."),
                new Field("id", Type.UUID, "Unique identifier of the topic."),
                new Field("partition", Type.INT32, "Partition number."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public TopicIdPartitionEntry(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TopicIdPartitionEntry() {
            this.name = "";
            this.id = Uuid.ZERO_UUID;
            this.partition = 0;
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
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of TopicIdPartitionEntry");
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
            this.id = _readable.readUuid();
            this.partition = _readable.readInt();
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
            _writable.writeUuid(id);
            _writable.writeInt(partition);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of TopicIdPartitionEntry");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            _size.addBytes(16);
            _size.addBytes(4);
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
            if (!(obj instanceof TopicIdPartitionEntry)) return false;
            TopicIdPartitionEntry other = (TopicIdPartitionEntry) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (!this.id.equals(other.id)) return false;
            if (partition != other.partition) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            hashCode = 31 * hashCode + id.hashCode();
            hashCode = 31 * hashCode + partition;
            return hashCode;
        }
        
        @Override
        public TopicIdPartitionEntry duplicate() {
            TopicIdPartitionEntry _duplicate = new TopicIdPartitionEntry();
            _duplicate.name = name;
            _duplicate.id = id;
            _duplicate.partition = partition;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "TopicIdPartitionEntry("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", id=" + id.toString()
                + ", partition=" + partition
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public Uuid id() {
            return this.id;
        }
        
        public int partition() {
            return this.partition;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TopicIdPartitionEntry setName(String v) {
            this.name = v;
            return this;
        }
        
        public TopicIdPartitionEntry setId(Uuid v) {
            this.id = v;
            return this;
        }
        
        public TopicIdPartitionEntry setPartition(int v) {
            this.partition = v;
            return this;
        }
    }
}
