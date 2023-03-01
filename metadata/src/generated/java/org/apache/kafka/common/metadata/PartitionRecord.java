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

import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.protocol.ApiMessage;
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


public class PartitionRecord implements ApiMessage {
    int partitionId;
    Uuid topicId;
    List<Integer> replicas;
    List<Integer> isr;
    List<Integer> removingReplicas;
    List<Integer> addingReplicas;
    int leader;
    int leaderEpoch;
    int partitionEpoch;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("partition_id", Type.INT32, "The partition id."),
            new Field("topic_id", Type.UUID, "The unique ID of this topic."),
            new Field("replicas", new CompactArrayOf(Type.INT32), "The replicas of this partition, sorted by preferred order."),
            new Field("isr", new CompactArrayOf(Type.INT32), "The in-sync replicas of this partition"),
            new Field("removing_replicas", new CompactArrayOf(Type.INT32), "The replicas that we are in the process of removing."),
            new Field("adding_replicas", new CompactArrayOf(Type.INT32), "The replicas that we are in the process of adding."),
            new Field("leader", Type.INT32, "The lead replica, or -1 if there is no leader."),
            new Field("leader_epoch", Type.INT32, "The epoch of the partition leader."),
            new Field("partition_epoch", Type.INT32, "An epoch that gets incremented each time we change anything in the partition."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public PartitionRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public PartitionRecord() {
        this.partitionId = -1;
        this.topicId = Uuid.ZERO_UUID;
        this.replicas = new ArrayList<Integer>(0);
        this.isr = new ArrayList<Integer>(0);
        this.removingReplicas = new ArrayList<Integer>(0);
        this.addingReplicas = new ArrayList<Integer>(0);
        this.leader = -1;
        this.leaderEpoch = -1;
        this.partitionEpoch = -1;
    }
    
    @Override
    public short apiKey() {
        return 3;
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
        this.partitionId = _readable.readInt();
        this.topicId = _readable.readUuid();
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field replicas was serialized as null");
            } else {
                ArrayList<Integer> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(_readable.readInt());
                }
                this.replicas = newCollection;
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field isr was serialized as null");
            } else {
                ArrayList<Integer> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(_readable.readInt());
                }
                this.isr = newCollection;
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field removingReplicas was serialized as null");
            } else {
                ArrayList<Integer> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(_readable.readInt());
                }
                this.removingReplicas = newCollection;
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field addingReplicas was serialized as null");
            } else {
                ArrayList<Integer> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(_readable.readInt());
                }
                this.addingReplicas = newCollection;
            }
        }
        this.leader = _readable.readInt();
        this.leaderEpoch = _readable.readInt();
        this.partitionEpoch = _readable.readInt();
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
        _writable.writeInt(partitionId);
        _writable.writeUuid(topicId);
        _writable.writeUnsignedVarint(replicas.size() + 1);
        for (Integer replicasElement : replicas) {
            _writable.writeInt(replicasElement);
        }
        _writable.writeUnsignedVarint(isr.size() + 1);
        for (Integer isrElement : isr) {
            _writable.writeInt(isrElement);
        }
        _writable.writeUnsignedVarint(removingReplicas.size() + 1);
        for (Integer removingReplicasElement : removingReplicas) {
            _writable.writeInt(removingReplicasElement);
        }
        _writable.writeUnsignedVarint(addingReplicas.size() + 1);
        for (Integer addingReplicasElement : addingReplicas) {
            _writable.writeInt(addingReplicasElement);
        }
        _writable.writeInt(leader);
        _writable.writeInt(leaderEpoch);
        _writable.writeInt(partitionEpoch);
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _size.addBytes(4);
        _size.addBytes(16);
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(replicas.size() + 1));
            _size.addBytes(replicas.size() * 4);
        }
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(isr.size() + 1));
            _size.addBytes(isr.size() * 4);
        }
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(removingReplicas.size() + 1));
            _size.addBytes(removingReplicas.size() * 4);
        }
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(addingReplicas.size() + 1));
            _size.addBytes(addingReplicas.size() * 4);
        }
        _size.addBytes(4);
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
        _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PartitionRecord)) return false;
        PartitionRecord other = (PartitionRecord) obj;
        if (partitionId != other.partitionId) return false;
        if (!this.topicId.equals(other.topicId)) return false;
        if (this.replicas == null) {
            if (other.replicas != null) return false;
        } else {
            if (!this.replicas.equals(other.replicas)) return false;
        }
        if (this.isr == null) {
            if (other.isr != null) return false;
        } else {
            if (!this.isr.equals(other.isr)) return false;
        }
        if (this.removingReplicas == null) {
            if (other.removingReplicas != null) return false;
        } else {
            if (!this.removingReplicas.equals(other.removingReplicas)) return false;
        }
        if (this.addingReplicas == null) {
            if (other.addingReplicas != null) return false;
        } else {
            if (!this.addingReplicas.equals(other.addingReplicas)) return false;
        }
        if (leader != other.leader) return false;
        if (leaderEpoch != other.leaderEpoch) return false;
        if (partitionEpoch != other.partitionEpoch) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + partitionId;
        hashCode = 31 * hashCode + topicId.hashCode();
        hashCode = 31 * hashCode + (replicas == null ? 0 : replicas.hashCode());
        hashCode = 31 * hashCode + (isr == null ? 0 : isr.hashCode());
        hashCode = 31 * hashCode + (removingReplicas == null ? 0 : removingReplicas.hashCode());
        hashCode = 31 * hashCode + (addingReplicas == null ? 0 : addingReplicas.hashCode());
        hashCode = 31 * hashCode + leader;
        hashCode = 31 * hashCode + leaderEpoch;
        hashCode = 31 * hashCode + partitionEpoch;
        return hashCode;
    }
    
    @Override
    public PartitionRecord duplicate() {
        PartitionRecord _duplicate = new PartitionRecord();
        _duplicate.partitionId = partitionId;
        _duplicate.topicId = topicId;
        ArrayList<Integer> newReplicas = new ArrayList<Integer>(replicas.size());
        for (Integer _element : replicas) {
            newReplicas.add(_element);
        }
        _duplicate.replicas = newReplicas;
        ArrayList<Integer> newIsr = new ArrayList<Integer>(isr.size());
        for (Integer _element : isr) {
            newIsr.add(_element);
        }
        _duplicate.isr = newIsr;
        ArrayList<Integer> newRemovingReplicas = new ArrayList<Integer>(removingReplicas.size());
        for (Integer _element : removingReplicas) {
            newRemovingReplicas.add(_element);
        }
        _duplicate.removingReplicas = newRemovingReplicas;
        ArrayList<Integer> newAddingReplicas = new ArrayList<Integer>(addingReplicas.size());
        for (Integer _element : addingReplicas) {
            newAddingReplicas.add(_element);
        }
        _duplicate.addingReplicas = newAddingReplicas;
        _duplicate.leader = leader;
        _duplicate.leaderEpoch = leaderEpoch;
        _duplicate.partitionEpoch = partitionEpoch;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "PartitionRecord("
            + "partitionId=" + partitionId
            + ", topicId=" + topicId.toString()
            + ", replicas=" + MessageUtil.deepToString(replicas.iterator())
            + ", isr=" + MessageUtil.deepToString(isr.iterator())
            + ", removingReplicas=" + MessageUtil.deepToString(removingReplicas.iterator())
            + ", addingReplicas=" + MessageUtil.deepToString(addingReplicas.iterator())
            + ", leader=" + leader
            + ", leaderEpoch=" + leaderEpoch
            + ", partitionEpoch=" + partitionEpoch
            + ")";
    }
    
    public int partitionId() {
        return this.partitionId;
    }
    
    public Uuid topicId() {
        return this.topicId;
    }
    
    public List<Integer> replicas() {
        return this.replicas;
    }
    
    public List<Integer> isr() {
        return this.isr;
    }
    
    public List<Integer> removingReplicas() {
        return this.removingReplicas;
    }
    
    public List<Integer> addingReplicas() {
        return this.addingReplicas;
    }
    
    public int leader() {
        return this.leader;
    }
    
    public int leaderEpoch() {
        return this.leaderEpoch;
    }
    
    public int partitionEpoch() {
        return this.partitionEpoch;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public PartitionRecord setPartitionId(int v) {
        this.partitionId = v;
        return this;
    }
    
    public PartitionRecord setTopicId(Uuid v) {
        this.topicId = v;
        return this;
    }
    
    public PartitionRecord setReplicas(List<Integer> v) {
        this.replicas = v;
        return this;
    }
    
    public PartitionRecord setIsr(List<Integer> v) {
        this.isr = v;
        return this;
    }
    
    public PartitionRecord setRemovingReplicas(List<Integer> v) {
        this.removingReplicas = v;
        return this;
    }
    
    public PartitionRecord setAddingReplicas(List<Integer> v) {
        this.addingReplicas = v;
        return this;
    }
    
    public PartitionRecord setLeader(int v) {
        this.leader = v;
        return this;
    }
    
    public PartitionRecord setLeaderEpoch(int v) {
        this.leaderEpoch = v;
        return this;
    }
    
    public PartitionRecord setPartitionEpoch(int v) {
        this.partitionEpoch = v;
        return this;
    }
}
