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
import org.apache.kafka.common.protocol.types.CompactArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class RemoteLogSegmentMetadataRecord implements ApiMessage {
    RemoteLogSegmentIdEntry remoteLogSegmentId;
    long startOffset;
    long endOffset;
    int brokerId;
    long maxTimestampMs;
    long eventTimestampMs;
    List<SegmentLeaderEpochEntry> segmentLeaderEpochs;
    int segmentSizeInBytes;
    byte remoteLogSegmentState;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("remote_log_segment_id", RemoteLogSegmentIdEntry.SCHEMA_0, "Unique representation of the remote log segment."),
            new Field("start_offset", Type.INT64, "Start offset  of the segment."),
            new Field("end_offset", Type.INT64, "End offset  of the segment."),
            new Field("broker_id", Type.INT32, "Broker id from which this event is generated."),
            new Field("max_timestamp_ms", Type.INT64, "Maximum timestamp in milli seconds with in this segment."),
            new Field("event_timestamp_ms", Type.INT64, "Epoch time in milli seconds at which this event is generated."),
            new Field("segment_leader_epochs", new CompactArrayOf(SegmentLeaderEpochEntry.SCHEMA_0), "Leader epoch to start-offset mappings for the records with in this segment."),
            new Field("segment_size_in_bytes", Type.INT32, "Segment size in bytes."),
            new Field("remote_log_segment_state", Type.INT8, "State identifier of the remote log segment, which is RemoteLogSegmentState.id()."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public RemoteLogSegmentMetadataRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public RemoteLogSegmentMetadataRecord() {
        this.remoteLogSegmentId = new RemoteLogSegmentIdEntry();
        this.startOffset = 0L;
        this.endOffset = 0L;
        this.brokerId = 0;
        this.maxTimestampMs = 0L;
        this.eventTimestampMs = 0L;
        this.segmentLeaderEpochs = new ArrayList<SegmentLeaderEpochEntry>(0);
        this.segmentSizeInBytes = 0;
        this.remoteLogSegmentState = (byte) 0;
    }
    
    @Override
    public short apiKey() {
        return 0;
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
        this.startOffset = _readable.readLong();
        this.endOffset = _readable.readLong();
        this.brokerId = _readable.readInt();
        this.maxTimestampMs = _readable.readLong();
        this.eventTimestampMs = _readable.readLong();
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field segmentLeaderEpochs was serialized as null");
            } else {
                ArrayList<SegmentLeaderEpochEntry> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new SegmentLeaderEpochEntry(_readable, _version));
                }
                this.segmentLeaderEpochs = newCollection;
            }
        }
        this.segmentSizeInBytes = _readable.readInt();
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
        _writable.writeLong(startOffset);
        _writable.writeLong(endOffset);
        _writable.writeInt(brokerId);
        _writable.writeLong(maxTimestampMs);
        _writable.writeLong(eventTimestampMs);
        _writable.writeUnsignedVarint(segmentLeaderEpochs.size() + 1);
        for (SegmentLeaderEpochEntry segmentLeaderEpochsElement : segmentLeaderEpochs) {
            segmentLeaderEpochsElement.write(_writable, _cache, _version);
        }
        _writable.writeInt(segmentSizeInBytes);
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
        _size.addBytes(8);
        _size.addBytes(8);
        _size.addBytes(4);
        _size.addBytes(8);
        _size.addBytes(8);
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(segmentLeaderEpochs.size() + 1));
            for (SegmentLeaderEpochEntry segmentLeaderEpochsElement : segmentLeaderEpochs) {
                segmentLeaderEpochsElement.addSize(_size, _cache, _version);
            }
        }
        _size.addBytes(4);
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
        if (!(obj instanceof RemoteLogSegmentMetadataRecord)) return false;
        RemoteLogSegmentMetadataRecord other = (RemoteLogSegmentMetadataRecord) obj;
        if (this.remoteLogSegmentId == null) {
            if (other.remoteLogSegmentId != null) return false;
        } else {
            if (!this.remoteLogSegmentId.equals(other.remoteLogSegmentId)) return false;
        }
        if (startOffset != other.startOffset) return false;
        if (endOffset != other.endOffset) return false;
        if (brokerId != other.brokerId) return false;
        if (maxTimestampMs != other.maxTimestampMs) return false;
        if (eventTimestampMs != other.eventTimestampMs) return false;
        if (this.segmentLeaderEpochs == null) {
            if (other.segmentLeaderEpochs != null) return false;
        } else {
            if (!this.segmentLeaderEpochs.equals(other.segmentLeaderEpochs)) return false;
        }
        if (segmentSizeInBytes != other.segmentSizeInBytes) return false;
        if (remoteLogSegmentState != other.remoteLogSegmentState) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (remoteLogSegmentId == null ? 0 : remoteLogSegmentId.hashCode());
        hashCode = 31 * hashCode + ((int) (startOffset >> 32) ^ (int) startOffset);
        hashCode = 31 * hashCode + ((int) (endOffset >> 32) ^ (int) endOffset);
        hashCode = 31 * hashCode + brokerId;
        hashCode = 31 * hashCode + ((int) (maxTimestampMs >> 32) ^ (int) maxTimestampMs);
        hashCode = 31 * hashCode + ((int) (eventTimestampMs >> 32) ^ (int) eventTimestampMs);
        hashCode = 31 * hashCode + (segmentLeaderEpochs == null ? 0 : segmentLeaderEpochs.hashCode());
        hashCode = 31 * hashCode + segmentSizeInBytes;
        hashCode = 31 * hashCode + remoteLogSegmentState;
        return hashCode;
    }
    
    @Override
    public RemoteLogSegmentMetadataRecord duplicate() {
        RemoteLogSegmentMetadataRecord _duplicate = new RemoteLogSegmentMetadataRecord();
        _duplicate.remoteLogSegmentId = remoteLogSegmentId.duplicate();
        _duplicate.startOffset = startOffset;
        _duplicate.endOffset = endOffset;
        _duplicate.brokerId = brokerId;
        _duplicate.maxTimestampMs = maxTimestampMs;
        _duplicate.eventTimestampMs = eventTimestampMs;
        ArrayList<SegmentLeaderEpochEntry> newSegmentLeaderEpochs = new ArrayList<SegmentLeaderEpochEntry>(segmentLeaderEpochs.size());
        for (SegmentLeaderEpochEntry _element : segmentLeaderEpochs) {
            newSegmentLeaderEpochs.add(_element.duplicate());
        }
        _duplicate.segmentLeaderEpochs = newSegmentLeaderEpochs;
        _duplicate.segmentSizeInBytes = segmentSizeInBytes;
        _duplicate.remoteLogSegmentState = remoteLogSegmentState;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "RemoteLogSegmentMetadataRecord("
            + "remoteLogSegmentId=" + remoteLogSegmentId.toString()
            + ", startOffset=" + startOffset
            + ", endOffset=" + endOffset
            + ", brokerId=" + brokerId
            + ", maxTimestampMs=" + maxTimestampMs
            + ", eventTimestampMs=" + eventTimestampMs
            + ", segmentLeaderEpochs=" + MessageUtil.deepToString(segmentLeaderEpochs.iterator())
            + ", segmentSizeInBytes=" + segmentSizeInBytes
            + ", remoteLogSegmentState=" + remoteLogSegmentState
            + ")";
    }
    
    public RemoteLogSegmentIdEntry remoteLogSegmentId() {
        return this.remoteLogSegmentId;
    }
    
    public long startOffset() {
        return this.startOffset;
    }
    
    public long endOffset() {
        return this.endOffset;
    }
    
    public int brokerId() {
        return this.brokerId;
    }
    
    public long maxTimestampMs() {
        return this.maxTimestampMs;
    }
    
    public long eventTimestampMs() {
        return this.eventTimestampMs;
    }
    
    public List<SegmentLeaderEpochEntry> segmentLeaderEpochs() {
        return this.segmentLeaderEpochs;
    }
    
    public int segmentSizeInBytes() {
        return this.segmentSizeInBytes;
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
    
    public RemoteLogSegmentMetadataRecord setRemoteLogSegmentId(RemoteLogSegmentIdEntry v) {
        this.remoteLogSegmentId = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setStartOffset(long v) {
        this.startOffset = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setEndOffset(long v) {
        this.endOffset = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setBrokerId(int v) {
        this.brokerId = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setMaxTimestampMs(long v) {
        this.maxTimestampMs = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setEventTimestampMs(long v) {
        this.eventTimestampMs = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setSegmentLeaderEpochs(List<SegmentLeaderEpochEntry> v) {
        this.segmentLeaderEpochs = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setSegmentSizeInBytes(int v) {
        this.segmentSizeInBytes = v;
        return this;
    }
    
    public RemoteLogSegmentMetadataRecord setRemoteLogSegmentState(byte v) {
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
    
    public static class SegmentLeaderEpochEntry implements Message {
        int leaderEpoch;
        long offset;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("leader_epoch", Type.INT32, "Leader epoch"),
                new Field("offset", Type.INT64, "Start offset for the leader epoch."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public SegmentLeaderEpochEntry(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public SegmentLeaderEpochEntry() {
            this.leaderEpoch = 0;
            this.offset = 0L;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of SegmentLeaderEpochEntry");
            }
            this.leaderEpoch = _readable.readInt();
            this.offset = _readable.readLong();
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
            _writable.writeInt(leaderEpoch);
            _writable.writeLong(offset);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of SegmentLeaderEpochEntry");
            }
            _size.addBytes(4);
            _size.addBytes(8);
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
            if (!(obj instanceof SegmentLeaderEpochEntry)) return false;
            SegmentLeaderEpochEntry other = (SegmentLeaderEpochEntry) obj;
            if (leaderEpoch != other.leaderEpoch) return false;
            if (offset != other.offset) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + leaderEpoch;
            hashCode = 31 * hashCode + ((int) (offset >> 32) ^ (int) offset);
            return hashCode;
        }
        
        @Override
        public SegmentLeaderEpochEntry duplicate() {
            SegmentLeaderEpochEntry _duplicate = new SegmentLeaderEpochEntry();
            _duplicate.leaderEpoch = leaderEpoch;
            _duplicate.offset = offset;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "SegmentLeaderEpochEntry("
                + "leaderEpoch=" + leaderEpoch
                + ", offset=" + offset
                + ")";
        }
        
        public int leaderEpoch() {
            return this.leaderEpoch;
        }
        
        public long offset() {
            return this.offset;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public SegmentLeaderEpochEntry setLeaderEpoch(int v) {
            this.leaderEpoch = v;
            return this;
        }
        
        public SegmentLeaderEpochEntry setOffset(long v) {
            this.offset = v;
            return this;
        }
    }
}
