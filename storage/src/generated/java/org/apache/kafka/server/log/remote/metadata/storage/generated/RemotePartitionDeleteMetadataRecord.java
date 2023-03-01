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


public class RemotePartitionDeleteMetadataRecord implements ApiMessage {
    TopicIdPartitionEntry topicIdPartition;
    int brokerId;
    long eventTimestampMs;
    byte remotePartitionDeleteState;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topic_id_partition", TopicIdPartitionEntry.SCHEMA_0, "Represents unique topic partition."),
            new Field("broker_id", Type.INT32, "Broker (controller or leader) id from which this event is created. DELETE_PARTITION_MARKED is sent by the controller. DELETE_PARTITION_STARTED and DELETE_PARTITION_FINISHED are sent by remote log metadata topic partition leader."),
            new Field("event_timestamp_ms", Type.INT64, "Epoch time in milli seconds at which this event is generated."),
            new Field("remote_partition_delete_state", Type.INT8, "Deletion state identifier of the remote partition, which is RemotePartitionDeleteState.id()."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public RemotePartitionDeleteMetadataRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public RemotePartitionDeleteMetadataRecord() {
        this.topicIdPartition = new TopicIdPartitionEntry();
        this.brokerId = 0;
        this.eventTimestampMs = 0L;
        this.remotePartitionDeleteState = (byte) 0;
    }
    
    @Override
    public short apiKey() {
        return 2;
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
            this.topicIdPartition = new TopicIdPartitionEntry(_readable, _version);
        }
        this.brokerId = _readable.readInt();
        this.eventTimestampMs = _readable.readLong();
        this.remotePartitionDeleteState = _readable.readByte();
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
        _writable.writeInt(brokerId);
        _writable.writeLong(eventTimestampMs);
        _writable.writeByte(remotePartitionDeleteState);
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
            this.topicIdPartition.addSize(_size, _cache, _version);
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
        if (!(obj instanceof RemotePartitionDeleteMetadataRecord)) return false;
        RemotePartitionDeleteMetadataRecord other = (RemotePartitionDeleteMetadataRecord) obj;
        if (this.topicIdPartition == null) {
            if (other.topicIdPartition != null) return false;
        } else {
            if (!this.topicIdPartition.equals(other.topicIdPartition)) return false;
        }
        if (brokerId != other.brokerId) return false;
        if (eventTimestampMs != other.eventTimestampMs) return false;
        if (remotePartitionDeleteState != other.remotePartitionDeleteState) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (topicIdPartition == null ? 0 : topicIdPartition.hashCode());
        hashCode = 31 * hashCode + brokerId;
        hashCode = 31 * hashCode + ((int) (eventTimestampMs >> 32) ^ (int) eventTimestampMs);
        hashCode = 31 * hashCode + remotePartitionDeleteState;
        return hashCode;
    }
    
    @Override
    public RemotePartitionDeleteMetadataRecord duplicate() {
        RemotePartitionDeleteMetadataRecord _duplicate = new RemotePartitionDeleteMetadataRecord();
        _duplicate.topicIdPartition = topicIdPartition.duplicate();
        _duplicate.brokerId = brokerId;
        _duplicate.eventTimestampMs = eventTimestampMs;
        _duplicate.remotePartitionDeleteState = remotePartitionDeleteState;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "RemotePartitionDeleteMetadataRecord("
            + "topicIdPartition=" + topicIdPartition.toString()
            + ", brokerId=" + brokerId
            + ", eventTimestampMs=" + eventTimestampMs
            + ", remotePartitionDeleteState=" + remotePartitionDeleteState
            + ")";
    }
    
    public TopicIdPartitionEntry topicIdPartition() {
        return this.topicIdPartition;
    }
    
    public int brokerId() {
        return this.brokerId;
    }
    
    public long eventTimestampMs() {
        return this.eventTimestampMs;
    }
    
    public byte remotePartitionDeleteState() {
        return this.remotePartitionDeleteState;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public RemotePartitionDeleteMetadataRecord setTopicIdPartition(TopicIdPartitionEntry v) {
        this.topicIdPartition = v;
        return this;
    }
    
    public RemotePartitionDeleteMetadataRecord setBrokerId(int v) {
        this.brokerId = v;
        return this;
    }
    
    public RemotePartitionDeleteMetadataRecord setEventTimestampMs(long v) {
        this.eventTimestampMs = v;
        return this;
    }
    
    public RemotePartitionDeleteMetadataRecord setRemotePartitionDeleteState(byte v) {
        this.remotePartitionDeleteState = v;
        return this;
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
