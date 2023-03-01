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

package org.apache.kafka.streams.internals.generated;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.kafka.common.protocol.types.ArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;
import org.apache.kafka.common.utils.Bytes;


public class SubscriptionInfoData implements ApiMessage {
    int version;
    int latestSupportedVersion;
    Uuid processId;
    List<TaskId> prevTasks;
    List<TaskId> standbyTasks;
    byte[] userEndPoint;
    List<TaskOffsetSum> taskOffsetSums;
    byte uniqueField;
    int errorCode;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("prev_tasks", new ArrayOf(TaskId.SCHEMA_1), ""),
            new Field("standby_tasks", new ArrayOf(TaskId.SCHEMA_1), "")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("prev_tasks", new ArrayOf(TaskId.SCHEMA_1), ""),
            new Field("standby_tasks", new ArrayOf(TaskId.SCHEMA_1), ""),
            new Field("user_end_point", Type.BYTES, "")
        );
    
    public static final Schema SCHEMA_3 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("latest_supported_version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("prev_tasks", new ArrayOf(TaskId.SCHEMA_1), ""),
            new Field("standby_tasks", new ArrayOf(TaskId.SCHEMA_1), ""),
            new Field("user_end_point", Type.BYTES, "")
        );
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 = SCHEMA_4;
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
    public static final Schema SCHEMA_7 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("latest_supported_version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("user_end_point", Type.BYTES, ""),
            new Field("task_offset_sums", new ArrayOf(TaskOffsetSum.SCHEMA_7), "")
        );
    
    public static final Schema SCHEMA_8 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("latest_supported_version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("user_end_point", Type.BYTES, ""),
            new Field("task_offset_sums", new ArrayOf(TaskOffsetSum.SCHEMA_7), ""),
            new Field("unique_field", Type.INT8, "")
        );
    
    public static final Schema SCHEMA_9 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("latest_supported_version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("user_end_point", Type.BYTES, ""),
            new Field("task_offset_sums", new ArrayOf(TaskOffsetSum.SCHEMA_7), ""),
            new Field("unique_field", Type.INT8, ""),
            new Field("error_code", Type.INT32, "")
        );
    
    public static final Schema SCHEMA_10 =
        new Schema(
            new Field("version", Type.INT32, ""),
            new Field("latest_supported_version", Type.INT32, ""),
            new Field("process_id", Type.UUID, ""),
            new Field("user_end_point", Type.BYTES, ""),
            new Field("task_offset_sums", new ArrayOf(TaskOffsetSum.SCHEMA_10), ""),
            new Field("unique_field", Type.INT8, ""),
            new Field("error_code", Type.INT32, "")
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        null,
        SCHEMA_1,
        SCHEMA_2,
        SCHEMA_3,
        SCHEMA_4,
        SCHEMA_5,
        SCHEMA_6,
        SCHEMA_7,
        SCHEMA_8,
        SCHEMA_9,
        SCHEMA_10
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 1;
    public static final short HIGHEST_SUPPORTED_VERSION = 10;
    
    public SubscriptionInfoData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public SubscriptionInfoData() {
        this.version = 0;
        this.latestSupportedVersion = -1;
        this.processId = Uuid.ZERO_UUID;
        this.prevTasks = new ArrayList<TaskId>(0);
        this.standbyTasks = new ArrayList<TaskId>(0);
        this.userEndPoint = Bytes.EMPTY;
        this.taskOffsetSums = new ArrayList<TaskOffsetSum>(0);
        this.uniqueField = (byte) 0;
        this.errorCode = 0;
    }
    
    @Override
    public short apiKey() {
        return -1;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 1;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 10;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.version = _readable.readInt();
        if (_version >= 3) {
            this.latestSupportedVersion = _readable.readInt();
        } else {
            this.latestSupportedVersion = -1;
        }
        this.processId = _readable.readUuid();
        if (_version <= 6) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field prevTasks was serialized as null");
            } else {
                ArrayList<TaskId> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new TaskId(_readable, _version));
                }
                this.prevTasks = newCollection;
            }
        } else {
            this.prevTasks = new ArrayList<TaskId>(0);
        }
        if (_version <= 6) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field standbyTasks was serialized as null");
            } else {
                ArrayList<TaskId> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new TaskId(_readable, _version));
                }
                this.standbyTasks = newCollection;
            }
        } else {
            this.standbyTasks = new ArrayList<TaskId>(0);
        }
        if (_version >= 2) {
            int length;
            length = _readable.readInt();
            if (length < 0) {
                throw new RuntimeException("non-nullable field userEndPoint was serialized as null");
            } else {
                byte[] newBytes = new byte[length];
                _readable.readArray(newBytes);
                this.userEndPoint = newBytes;
            }
        } else {
            this.userEndPoint = Bytes.EMPTY;
        }
        if (_version >= 7) {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field taskOffsetSums was serialized as null");
            } else {
                ArrayList<TaskOffsetSum> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new TaskOffsetSum(_readable, _version));
                }
                this.taskOffsetSums = newCollection;
            }
        } else {
            this.taskOffsetSums = new ArrayList<TaskOffsetSum>(0);
        }
        if (_version >= 8) {
            this.uniqueField = _readable.readByte();
        } else {
            this.uniqueField = (byte) 0;
        }
        if (_version >= 9) {
            this.errorCode = _readable.readInt();
        } else {
            this.errorCode = 0;
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeInt(version);
        if (_version >= 3) {
            _writable.writeInt(latestSupportedVersion);
        } else {
            if (this.latestSupportedVersion != -1) {
                throw new UnsupportedVersionException("Attempted to write a non-default latestSupportedVersion at version " + _version);
            }
        }
        _writable.writeUuid(processId);
        if (_version <= 6) {
            _writable.writeInt(prevTasks.size());
            for (TaskId prevTasksElement : prevTasks) {
                prevTasksElement.write(_writable, _cache, _version);
            }
        } else {
            if (!this.prevTasks.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default prevTasks at version " + _version);
            }
        }
        if (_version <= 6) {
            _writable.writeInt(standbyTasks.size());
            for (TaskId standbyTasksElement : standbyTasks) {
                standbyTasksElement.write(_writable, _cache, _version);
            }
        } else {
            if (!this.standbyTasks.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default standbyTasks at version " + _version);
            }
        }
        if (_version >= 2) {
            _writable.writeInt(userEndPoint.length);
            _writable.writeByteArray(userEndPoint);
        } else {
            if (this.userEndPoint.length != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default userEndPoint at version " + _version);
            }
        }
        if (_version >= 7) {
            _writable.writeInt(taskOffsetSums.size());
            for (TaskOffsetSum taskOffsetSumsElement : taskOffsetSums) {
                taskOffsetSumsElement.write(_writable, _cache, _version);
            }
        } else {
            if (!this.taskOffsetSums.isEmpty()) {
                throw new UnsupportedVersionException("Attempted to write a non-default taskOffsetSums at version " + _version);
            }
        }
        if (_version >= 8) {
            _writable.writeByte(uniqueField);
        } else {
            if (this.uniqueField != (byte) 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default uniqueField at version " + _version);
            }
        }
        if (_version >= 9) {
            _writable.writeInt(errorCode);
        } else {
            if (this.errorCode != 0) {
                throw new UnsupportedVersionException("Attempted to write a non-default errorCode at version " + _version);
            }
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _size.addBytes(4);
        if (_version >= 3) {
            _size.addBytes(4);
        }
        _size.addBytes(16);
        if (_version <= 6) {
            {
                _size.addBytes(4);
                for (TaskId prevTasksElement : prevTasks) {
                    prevTasksElement.addSize(_size, _cache, _version);
                }
            }
        }
        if (_version <= 6) {
            {
                _size.addBytes(4);
                for (TaskId standbyTasksElement : standbyTasks) {
                    standbyTasksElement.addSize(_size, _cache, _version);
                }
            }
        }
        if (_version >= 2) {
            {
                _size.addBytes(userEndPoint.length);
                _size.addBytes(4);
            }
        }
        if (_version >= 7) {
            {
                _size.addBytes(4);
                for (TaskOffsetSum taskOffsetSumsElement : taskOffsetSums) {
                    taskOffsetSumsElement.addSize(_size, _cache, _version);
                }
            }
        }
        if (_version >= 8) {
            _size.addBytes(1);
        }
        if (_version >= 9) {
            _size.addBytes(4);
        }
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                _size.addBytes(_field.size());
            }
        }
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubscriptionInfoData)) return false;
        SubscriptionInfoData other = (SubscriptionInfoData) obj;
        if (version != other.version) return false;
        if (latestSupportedVersion != other.latestSupportedVersion) return false;
        if (!this.processId.equals(other.processId)) return false;
        if (this.prevTasks == null) {
            if (other.prevTasks != null) return false;
        } else {
            if (!this.prevTasks.equals(other.prevTasks)) return false;
        }
        if (this.standbyTasks == null) {
            if (other.standbyTasks != null) return false;
        } else {
            if (!this.standbyTasks.equals(other.standbyTasks)) return false;
        }
        if (!Arrays.equals(this.userEndPoint, other.userEndPoint)) return false;
        if (this.taskOffsetSums == null) {
            if (other.taskOffsetSums != null) return false;
        } else {
            if (!this.taskOffsetSums.equals(other.taskOffsetSums)) return false;
        }
        if (uniqueField != other.uniqueField) return false;
        if (errorCode != other.errorCode) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + version;
        hashCode = 31 * hashCode + latestSupportedVersion;
        hashCode = 31 * hashCode + processId.hashCode();
        hashCode = 31 * hashCode + (prevTasks == null ? 0 : prevTasks.hashCode());
        hashCode = 31 * hashCode + (standbyTasks == null ? 0 : standbyTasks.hashCode());
        hashCode = 31 * hashCode + Arrays.hashCode(userEndPoint);
        hashCode = 31 * hashCode + (taskOffsetSums == null ? 0 : taskOffsetSums.hashCode());
        hashCode = 31 * hashCode + uniqueField;
        hashCode = 31 * hashCode + errorCode;
        return hashCode;
    }
    
    @Override
    public SubscriptionInfoData duplicate() {
        SubscriptionInfoData _duplicate = new SubscriptionInfoData();
        _duplicate.version = version;
        _duplicate.latestSupportedVersion = latestSupportedVersion;
        _duplicate.processId = processId;
        ArrayList<TaskId> newPrevTasks = new ArrayList<TaskId>(prevTasks.size());
        for (TaskId _element : prevTasks) {
            newPrevTasks.add(_element.duplicate());
        }
        _duplicate.prevTasks = newPrevTasks;
        ArrayList<TaskId> newStandbyTasks = new ArrayList<TaskId>(standbyTasks.size());
        for (TaskId _element : standbyTasks) {
            newStandbyTasks.add(_element.duplicate());
        }
        _duplicate.standbyTasks = newStandbyTasks;
        _duplicate.userEndPoint = MessageUtil.duplicate(userEndPoint);
        ArrayList<TaskOffsetSum> newTaskOffsetSums = new ArrayList<TaskOffsetSum>(taskOffsetSums.size());
        for (TaskOffsetSum _element : taskOffsetSums) {
            newTaskOffsetSums.add(_element.duplicate());
        }
        _duplicate.taskOffsetSums = newTaskOffsetSums;
        _duplicate.uniqueField = uniqueField;
        _duplicate.errorCode = errorCode;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "SubscriptionInfoData("
            + "version=" + version
            + ", latestSupportedVersion=" + latestSupportedVersion
            + ", processId=" + processId.toString()
            + ", prevTasks=" + MessageUtil.deepToString(prevTasks.iterator())
            + ", standbyTasks=" + MessageUtil.deepToString(standbyTasks.iterator())
            + ", userEndPoint=" + Arrays.toString(userEndPoint)
            + ", taskOffsetSums=" + MessageUtil.deepToString(taskOffsetSums.iterator())
            + ", uniqueField=" + uniqueField
            + ", errorCode=" + errorCode
            + ")";
    }
    
    public int version() {
        return this.version;
    }
    
    public int latestSupportedVersion() {
        return this.latestSupportedVersion;
    }
    
    public Uuid processId() {
        return this.processId;
    }
    
    public List<TaskId> prevTasks() {
        return this.prevTasks;
    }
    
    public List<TaskId> standbyTasks() {
        return this.standbyTasks;
    }
    
    public byte[] userEndPoint() {
        return this.userEndPoint;
    }
    
    public List<TaskOffsetSum> taskOffsetSums() {
        return this.taskOffsetSums;
    }
    
    public byte uniqueField() {
        return this.uniqueField;
    }
    
    public int errorCode() {
        return this.errorCode;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public SubscriptionInfoData setVersion(int v) {
        this.version = v;
        return this;
    }
    
    public SubscriptionInfoData setLatestSupportedVersion(int v) {
        this.latestSupportedVersion = v;
        return this;
    }
    
    public SubscriptionInfoData setProcessId(Uuid v) {
        this.processId = v;
        return this;
    }
    
    public SubscriptionInfoData setPrevTasks(List<TaskId> v) {
        this.prevTasks = v;
        return this;
    }
    
    public SubscriptionInfoData setStandbyTasks(List<TaskId> v) {
        this.standbyTasks = v;
        return this;
    }
    
    public SubscriptionInfoData setUserEndPoint(byte[] v) {
        this.userEndPoint = v;
        return this;
    }
    
    public SubscriptionInfoData setTaskOffsetSums(List<TaskOffsetSum> v) {
        this.taskOffsetSums = v;
        return this;
    }
    
    public SubscriptionInfoData setUniqueField(byte v) {
        this.uniqueField = v;
        return this;
    }
    
    public SubscriptionInfoData setErrorCode(int v) {
        this.errorCode = v;
        return this;
    }
    
    public static class PartitionToOffsetSum implements Message {
        int partition;
        long offsetSum;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_7 =
            new Schema(
                new Field("partition", Type.INT32, ""),
                new Field("offset_sum", Type.INT64, "")
            );
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 = SCHEMA_8;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_7,
            SCHEMA_8,
            SCHEMA_9
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 7;
        public static final short HIGHEST_SUPPORTED_VERSION = 9;
        
        public PartitionToOffsetSum(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public PartitionToOffsetSum() {
            this.partition = 0;
            this.offsetSum = 0L;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 7;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 9;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            this.partition = _readable.readInt();
            this.offsetSum = _readable.readLong();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            _writable.writeInt(partition);
            _writable.writeLong(offsetSum);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
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
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PartitionToOffsetSum)) return false;
            PartitionToOffsetSum other = (PartitionToOffsetSum) obj;
            if (partition != other.partition) return false;
            if (offsetSum != other.offsetSum) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partition;
            hashCode = 31 * hashCode + ((int) (offsetSum >> 32) ^ (int) offsetSum);
            return hashCode;
        }
        
        @Override
        public PartitionToOffsetSum duplicate() {
            PartitionToOffsetSum _duplicate = new PartitionToOffsetSum();
            _duplicate.partition = partition;
            _duplicate.offsetSum = offsetSum;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "PartitionToOffsetSum("
                + "partition=" + partition
                + ", offsetSum=" + offsetSum
                + ")";
        }
        
        public int partition() {
            return this.partition;
        }
        
        public long offsetSum() {
            return this.offsetSum;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public PartitionToOffsetSum setPartition(int v) {
            this.partition = v;
            return this;
        }
        
        public PartitionToOffsetSum setOffsetSum(long v) {
            this.offsetSum = v;
            return this;
        }
    }
    
    public static class TaskId implements Message {
        int topicGroupId;
        int partition;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("topic_group_id", Type.INT32, ""),
                new Field("partition", Type.INT32, "")
            );
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 = SCHEMA_4;
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            SCHEMA_1,
            SCHEMA_2,
            SCHEMA_3,
            SCHEMA_4,
            SCHEMA_5,
            SCHEMA_6
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 1;
        public static final short HIGHEST_SUPPORTED_VERSION = 6;
        
        public TaskId(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TaskId() {
            this.topicGroupId = 0;
            this.partition = 0;
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 1;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 6;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            this.topicGroupId = _readable.readInt();
            this.partition = _readable.readInt();
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            _writable.writeInt(topicGroupId);
            _writable.writeInt(partition);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            _size.addBytes(4);
            _size.addBytes(4);
            if (_unknownTaggedFields != null) {
                _numTaggedFields += _unknownTaggedFields.size();
                for (RawTaggedField _field : _unknownTaggedFields) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                    _size.addBytes(_field.size());
                }
            }
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TaskId)) return false;
            TaskId other = (TaskId) obj;
            if (topicGroupId != other.topicGroupId) return false;
            if (partition != other.partition) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + topicGroupId;
            hashCode = 31 * hashCode + partition;
            return hashCode;
        }
        
        @Override
        public TaskId duplicate() {
            TaskId _duplicate = new TaskId();
            _duplicate.topicGroupId = topicGroupId;
            _duplicate.partition = partition;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "TaskId("
                + "topicGroupId=" + topicGroupId
                + ", partition=" + partition
                + ")";
        }
        
        public int topicGroupId() {
            return this.topicGroupId;
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
        
        public TaskId setTopicGroupId(int v) {
            this.topicGroupId = v;
            return this;
        }
        
        public TaskId setPartition(int v) {
            this.partition = v;
            return this;
        }
    }
    
    public static class TaskOffsetSum implements Message {
        int topicGroupId;
        int partition;
        long offsetSum;
        String namedTopology;
        List<PartitionToOffsetSum> partitionToOffsetSum;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_7 =
            new Schema(
                new Field("topic_group_id", Type.INT32, ""),
                new Field("partition_to_offset_sum", new ArrayOf(PartitionToOffsetSum.SCHEMA_7), "")
            );
        
        public static final Schema SCHEMA_8 = SCHEMA_7;
        
        public static final Schema SCHEMA_9 = SCHEMA_8;
        
        public static final Schema SCHEMA_10 =
            new Schema(
                new Field("topic_group_id", Type.INT32, ""),
                new Field("partition", Type.INT32, ""),
                new Field("offset_sum", Type.INT64, ""),
                new Field("named_topology", Type.NULLABLE_STRING, "")
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            SCHEMA_7,
            SCHEMA_8,
            SCHEMA_9,
            SCHEMA_10
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 7;
        public static final short HIGHEST_SUPPORTED_VERSION = 10;
        
        public TaskOffsetSum(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TaskOffsetSum() {
            this.topicGroupId = 0;
            this.partition = 0;
            this.offsetSum = 0L;
            this.namedTopology = "";
            this.partitionToOffsetSum = new ArrayList<PartitionToOffsetSum>(0);
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 7;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 32767;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            this.topicGroupId = _readable.readInt();
            if (_version >= 10) {
                this.partition = _readable.readInt();
            } else {
                this.partition = 0;
            }
            if (_version >= 10) {
                this.offsetSum = _readable.readLong();
            } else {
                this.offsetSum = 0L;
            }
            if (_version >= 10) {
                int length;
                length = _readable.readShort();
                if (length < 0) {
                    this.namedTopology = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field namedTopology had invalid length " + length);
                } else {
                    this.namedTopology = _readable.readString(length);
                }
            } else {
                this.namedTopology = "";
            }
            if (_version <= 9) {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitionToOffsetSum was serialized as null");
                } else {
                    ArrayList<PartitionToOffsetSum> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new PartitionToOffsetSum(_readable, _version));
                    }
                    this.partitionToOffsetSum = newCollection;
                }
            } else {
                this.partitionToOffsetSum = new ArrayList<PartitionToOffsetSum>(0);
            }
            this._unknownTaggedFields = null;
        }
        
        @Override
        public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            _writable.writeInt(topicGroupId);
            if (_version >= 10) {
                _writable.writeInt(partition);
            } else {
                if (this.partition != 0) {
                    throw new UnsupportedVersionException("Attempted to write a non-default partition at version " + _version);
                }
            }
            if (_version >= 10) {
                _writable.writeLong(offsetSum);
            } else {
                if (this.offsetSum != 0L) {
                    throw new UnsupportedVersionException("Attempted to write a non-default offsetSum at version " + _version);
                }
            }
            if (_version >= 10) {
                if (namedTopology == null) {
                    _writable.writeShort((short) -1);
                } else {
                    byte[] _stringBytes = _cache.getSerializedValue(namedTopology);
                    _writable.writeShort((short) _stringBytes.length);
                    _writable.writeByteArray(_stringBytes);
                }
            } else {
                if (this.namedTopology == null || !this.namedTopology.equals("")) {
                    throw new UnsupportedVersionException("Attempted to write a non-default namedTopology at version " + _version);
                }
            }
            if (_version <= 9) {
                _writable.writeInt(partitionToOffsetSum.size());
                for (PartitionToOffsetSum partitionToOffsetSumElement : partitionToOffsetSum) {
                    partitionToOffsetSumElement.write(_writable, _cache, _version);
                }
            } else {
                if (!this.partitionToOffsetSum.isEmpty()) {
                    throw new UnsupportedVersionException("Attempted to write a non-default partitionToOffsetSum at version " + _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            _size.addBytes(4);
            if (_version >= 10) {
                _size.addBytes(4);
            }
            if (_version >= 10) {
                _size.addBytes(8);
            }
            if (_version >= 10) {
                if (namedTopology == null) {
                    _size.addBytes(2);
                } else {
                    byte[] _stringBytes = namedTopology.getBytes(StandardCharsets.UTF_8);
                    if (_stringBytes.length > 0x7fff) {
                        throw new RuntimeException("'namedTopology' field is too long to be serialized");
                    }
                    _cache.cacheSerializedValue(namedTopology, _stringBytes);
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            if (_version <= 9) {
                {
                    _size.addBytes(4);
                    for (PartitionToOffsetSum partitionToOffsetSumElement : partitionToOffsetSum) {
                        partitionToOffsetSumElement.addSize(_size, _cache, _version);
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
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TaskOffsetSum)) return false;
            TaskOffsetSum other = (TaskOffsetSum) obj;
            if (topicGroupId != other.topicGroupId) return false;
            if (partition != other.partition) return false;
            if (offsetSum != other.offsetSum) return false;
            if (this.namedTopology == null) {
                if (other.namedTopology != null) return false;
            } else {
                if (!this.namedTopology.equals(other.namedTopology)) return false;
            }
            if (this.partitionToOffsetSum == null) {
                if (other.partitionToOffsetSum != null) return false;
            } else {
                if (!this.partitionToOffsetSum.equals(other.partitionToOffsetSum)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + topicGroupId;
            hashCode = 31 * hashCode + partition;
            hashCode = 31 * hashCode + ((int) (offsetSum >> 32) ^ (int) offsetSum);
            hashCode = 31 * hashCode + (namedTopology == null ? 0 : namedTopology.hashCode());
            hashCode = 31 * hashCode + (partitionToOffsetSum == null ? 0 : partitionToOffsetSum.hashCode());
            return hashCode;
        }
        
        @Override
        public TaskOffsetSum duplicate() {
            TaskOffsetSum _duplicate = new TaskOffsetSum();
            _duplicate.topicGroupId = topicGroupId;
            _duplicate.partition = partition;
            _duplicate.offsetSum = offsetSum;
            if (namedTopology == null) {
                _duplicate.namedTopology = null;
            } else {
                _duplicate.namedTopology = namedTopology;
            }
            ArrayList<PartitionToOffsetSum> newPartitionToOffsetSum = new ArrayList<PartitionToOffsetSum>(partitionToOffsetSum.size());
            for (PartitionToOffsetSum _element : partitionToOffsetSum) {
                newPartitionToOffsetSum.add(_element.duplicate());
            }
            _duplicate.partitionToOffsetSum = newPartitionToOffsetSum;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "TaskOffsetSum("
                + "topicGroupId=" + topicGroupId
                + ", partition=" + partition
                + ", offsetSum=" + offsetSum
                + ", namedTopology=" + ((namedTopology == null) ? "null" : "'" + namedTopology.toString() + "'")
                + ", partitionToOffsetSum=" + MessageUtil.deepToString(partitionToOffsetSum.iterator())
                + ")";
        }
        
        public int topicGroupId() {
            return this.topicGroupId;
        }
        
        public int partition() {
            return this.partition;
        }
        
        public long offsetSum() {
            return this.offsetSum;
        }
        
        public String namedTopology() {
            return this.namedTopology;
        }
        
        public List<PartitionToOffsetSum> partitionToOffsetSum() {
            return this.partitionToOffsetSum;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TaskOffsetSum setTopicGroupId(int v) {
            this.topicGroupId = v;
            return this;
        }
        
        public TaskOffsetSum setPartition(int v) {
            this.partition = v;
            return this;
        }
        
        public TaskOffsetSum setOffsetSum(long v) {
            this.offsetSum = v;
            return this;
        }
        
        public TaskOffsetSum setNamedTopology(String v) {
            this.namedTopology = v;
            return this;
        }
        
        public TaskOffsetSum setPartitionToOffsetSum(List<PartitionToOffsetSum> v) {
            this.partitionToOffsetSum = v;
            return this;
        }
    }
}
