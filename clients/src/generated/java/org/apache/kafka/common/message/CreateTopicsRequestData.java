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
import java.util.Iterator;
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
import org.apache.kafka.common.utils.ImplicitLinkedHashCollection;
import org.apache.kafka.common.utils.ImplicitLinkedHashMultiCollection;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class CreateTopicsRequestData implements ApiMessage {
    CreatableTopicCollection topics;
    int timeoutMs;
    boolean validateOnly;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("topics", new ArrayOf(CreatableTopic.SCHEMA_0), "The topics to create."),
            new Field("timeout_ms", Type.INT32, "How long to wait in milliseconds before timing out the request.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("topics", new ArrayOf(CreatableTopic.SCHEMA_0), "The topics to create."),
            new Field("timeout_ms", Type.INT32, "How long to wait in milliseconds before timing out the request."),
            new Field("validate_only", Type.BOOLEAN, "If true, check that the topics can be created as specified, but don't create anything.")
        );
    
    public static final Schema SCHEMA_2 = SCHEMA_1;
    
    public static final Schema SCHEMA_3 = SCHEMA_2;
    
    public static final Schema SCHEMA_4 = SCHEMA_3;
    
    public static final Schema SCHEMA_5 =
        new Schema(
            new Field("topics", new CompactArrayOf(CreatableTopic.SCHEMA_5), "The topics to create."),
            new Field("timeout_ms", Type.INT32, "How long to wait in milliseconds before timing out the request."),
            new Field("validate_only", Type.BOOLEAN, "If true, check that the topics can be created as specified, but don't create anything."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema SCHEMA_6 = SCHEMA_5;
    
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
    
    public CreateTopicsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public CreateTopicsRequestData() {
        this.topics = new CreatableTopicCollection(0);
        this.timeoutMs = 60000;
        this.validateOnly = false;
    }
    
    @Override
    public short apiKey() {
        return 19;
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
        {
            if (_version >= 5) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    CreatableTopicCollection newCollection = new CreatableTopicCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    CreatableTopicCollection newCollection = new CreatableTopicCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new CreatableTopic(_readable, _version));
                    }
                    this.topics = newCollection;
                }
            }
        }
        this.timeoutMs = _readable.readInt();
        if (_version >= 1) {
            this.validateOnly = _readable.readByte() != 0;
        } else {
            this.validateOnly = false;
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
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (CreatableTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(topics.size());
            for (CreatableTopic topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
        }
        _writable.writeInt(timeoutMs);
        if (_version >= 1) {
            _writable.writeByte(validateOnly ? (byte) 1 : (byte) 0);
        } else {
            if (this.validateOnly) {
                throw new UnsupportedVersionException("Attempted to write a non-default validateOnly at version " + _version);
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
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(topics.size() + 1));
            } else {
                _size.addBytes(4);
            }
            for (CreatableTopic topicsElement : topics) {
                topicsElement.addSize(_size, _cache, _version);
            }
        }
        _size.addBytes(4);
        if (_version >= 1) {
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
        if (!(obj instanceof CreateTopicsRequestData)) return false;
        CreateTopicsRequestData other = (CreateTopicsRequestData) obj;
        if (this.topics == null) {
            if (other.topics != null) return false;
        } else {
            if (!this.topics.equals(other.topics)) return false;
        }
        if (timeoutMs != other.timeoutMs) return false;
        if (validateOnly != other.validateOnly) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
        hashCode = 31 * hashCode + timeoutMs;
        hashCode = 31 * hashCode + (validateOnly ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public CreateTopicsRequestData duplicate() {
        CreateTopicsRequestData _duplicate = new CreateTopicsRequestData();
        CreatableTopicCollection newTopics = new CreatableTopicCollection(topics.size());
        for (CreatableTopic _element : topics) {
            newTopics.add(_element.duplicate());
        }
        _duplicate.topics = newTopics;
        _duplicate.timeoutMs = timeoutMs;
        _duplicate.validateOnly = validateOnly;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "CreateTopicsRequestData("
            + "topics=" + MessageUtil.deepToString(topics.iterator())
            + ", timeoutMs=" + timeoutMs
            + ", validateOnly=" + (validateOnly ? "true" : "false")
            + ")";
    }
    
    public CreatableTopicCollection topics() {
        return this.topics;
    }
    
    public int timeoutMs() {
        return this.timeoutMs;
    }
    
    public boolean validateOnly() {
        return this.validateOnly;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public CreateTopicsRequestData setTopics(CreatableTopicCollection v) {
        this.topics = v;
        return this;
    }
    
    public CreateTopicsRequestData setTimeoutMs(int v) {
        this.timeoutMs = v;
        return this;
    }
    
    public CreateTopicsRequestData setValidateOnly(boolean v) {
        this.validateOnly = v;
        return this;
    }
    
    public static class CreatableTopic implements Message, ImplicitLinkedHashMultiCollection.Element {
        String name;
        int numPartitions;
        short replicationFactor;
        CreatableReplicaAssignmentCollection assignments;
        CreateableTopicConfigCollection configs;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The topic name."),
                new Field("num_partitions", Type.INT32, "The number of partitions to create in the topic, or -1 if we are either specifying a manual partition assignment or using the default partitions."),
                new Field("replication_factor", Type.INT16, "The number of replicas to create for each partition in the topic, or -1 if we are either specifying a manual partition assignment or using the default replication factor."),
                new Field("assignments", new ArrayOf(CreatableReplicaAssignment.SCHEMA_0), "The manual partition assignment, or the empty array if we are using automatic assignment."),
                new Field("configs", new ArrayOf(CreateableTopicConfig.SCHEMA_0), "The custom topic configurations to set.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The topic name."),
                new Field("num_partitions", Type.INT32, "The number of partitions to create in the topic, or -1 if we are either specifying a manual partition assignment or using the default partitions."),
                new Field("replication_factor", Type.INT16, "The number of replicas to create for each partition in the topic, or -1 if we are either specifying a manual partition assignment or using the default replication factor."),
                new Field("assignments", new CompactArrayOf(CreatableReplicaAssignment.SCHEMA_5), "The manual partition assignment, or the empty array if we are using automatic assignment."),
                new Field("configs", new CompactArrayOf(CreateableTopicConfig.SCHEMA_5), "The custom topic configurations to set."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
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
        
        public CreatableTopic(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public CreatableTopic() {
            this.name = "";
            this.numPartitions = 0;
            this.replicationFactor = (short) 0;
            this.assignments = new CreatableReplicaAssignmentCollection(0);
            this.configs = new CreateableTopicConfigCollection(0);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableTopic");
            }
            {
                int length;
                if (_version >= 5) {
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
            this.numPartitions = _readable.readInt();
            this.replicationFactor = _readable.readShort();
            {
                if (_version >= 5) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field assignments was serialized as null");
                    } else {
                        CreatableReplicaAssignmentCollection newCollection = new CreatableReplicaAssignmentCollection(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new CreatableReplicaAssignment(_readable, _version));
                        }
                        this.assignments = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field assignments was serialized as null");
                    } else {
                        CreatableReplicaAssignmentCollection newCollection = new CreatableReplicaAssignmentCollection(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new CreatableReplicaAssignment(_readable, _version));
                        }
                        this.assignments = newCollection;
                    }
                }
            }
            {
                if (_version >= 5) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field configs was serialized as null");
                    } else {
                        CreateableTopicConfigCollection newCollection = new CreateableTopicConfigCollection(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new CreateableTopicConfig(_readable, _version));
                        }
                        this.configs = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field configs was serialized as null");
                    } else {
                        CreateableTopicConfigCollection newCollection = new CreateableTopicConfigCollection(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new CreateableTopicConfig(_readable, _version));
                        }
                        this.configs = newCollection;
                    }
                }
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
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 5) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(numPartitions);
            _writable.writeShort(replicationFactor);
            if (_version >= 5) {
                _writable.writeUnsignedVarint(assignments.size() + 1);
                for (CreatableReplicaAssignment assignmentsElement : assignments) {
                    assignmentsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(assignments.size());
                for (CreatableReplicaAssignment assignmentsElement : assignments) {
                    assignmentsElement.write(_writable, _cache, _version);
                }
            }
            if (_version >= 5) {
                _writable.writeUnsignedVarint(configs.size() + 1);
                for (CreateableTopicConfig configsElement : configs) {
                    configsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(configs.size());
                for (CreateableTopicConfig configsElement : configs) {
                    configsElement.write(_writable, _cache, _version);
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of CreatableTopic");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 5) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            _size.addBytes(4);
            _size.addBytes(2);
            {
                if (_version >= 5) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(assignments.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                for (CreatableReplicaAssignment assignmentsElement : assignments) {
                    assignmentsElement.addSize(_size, _cache, _version);
                }
            }
            {
                if (_version >= 5) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(configs.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                for (CreateableTopicConfig configsElement : configs) {
                    configsElement.addSize(_size, _cache, _version);
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
            if (_version >= 5) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean elementKeysAreEqual(Object obj) {
            if (!(obj instanceof CreatableTopic)) return false;
            CreatableTopic other = (CreatableTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CreatableTopic)) return false;
            CreatableTopic other = (CreatableTopic) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (numPartitions != other.numPartitions) return false;
            if (replicationFactor != other.replicationFactor) return false;
            if (this.assignments == null) {
                if (other.assignments != null) return false;
            } else {
                if (!this.assignments.equals(other.assignments)) return false;
            }
            if (this.configs == null) {
                if (other.configs != null) return false;
            } else {
                if (!this.configs.equals(other.configs)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public CreatableTopic duplicate() {
            CreatableTopic _duplicate = new CreatableTopic();
            _duplicate.name = name;
            _duplicate.numPartitions = numPartitions;
            _duplicate.replicationFactor = replicationFactor;
            CreatableReplicaAssignmentCollection newAssignments = new CreatableReplicaAssignmentCollection(assignments.size());
            for (CreatableReplicaAssignment _element : assignments) {
                newAssignments.add(_element.duplicate());
            }
            _duplicate.assignments = newAssignments;
            CreateableTopicConfigCollection newConfigs = new CreateableTopicConfigCollection(configs.size());
            for (CreateableTopicConfig _element : configs) {
                newConfigs.add(_element.duplicate());
            }
            _duplicate.configs = newConfigs;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "CreatableTopic("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", numPartitions=" + numPartitions
                + ", replicationFactor=" + replicationFactor
                + ", assignments=" + MessageUtil.deepToString(assignments.iterator())
                + ", configs=" + MessageUtil.deepToString(configs.iterator())
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public int numPartitions() {
            return this.numPartitions;
        }
        
        public short replicationFactor() {
            return this.replicationFactor;
        }
        
        public CreatableReplicaAssignmentCollection assignments() {
            return this.assignments;
        }
        
        public CreateableTopicConfigCollection configs() {
            return this.configs;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public CreatableTopic setName(String v) {
            this.name = v;
            return this;
        }
        
        public CreatableTopic setNumPartitions(int v) {
            this.numPartitions = v;
            return this;
        }
        
        public CreatableTopic setReplicationFactor(short v) {
            this.replicationFactor = v;
            return this;
        }
        
        public CreatableTopic setAssignments(CreatableReplicaAssignmentCollection v) {
            this.assignments = v;
            return this;
        }
        
        public CreatableTopic setConfigs(CreateableTopicConfigCollection v) {
            this.configs = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    public static class CreatableReplicaAssignment implements Message, ImplicitLinkedHashMultiCollection.Element {
        int partitionIndex;
        List<Integer> brokerIds;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("broker_ids", new ArrayOf(Type.INT32), "The brokers to place the partition on.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("partition_index", Type.INT32, "The partition index."),
                new Field("broker_ids", new CompactArrayOf(Type.INT32), "The brokers to place the partition on."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
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
        
        public CreatableReplicaAssignment(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public CreatableReplicaAssignment() {
            this.partitionIndex = 0;
            this.brokerIds = new ArrayList<Integer>(0);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreatableReplicaAssignment");
            }
            this.partitionIndex = _readable.readInt();
            {
                int arrayLength;
                if (_version >= 5) {
                    arrayLength = _readable.readUnsignedVarint() - 1;
                } else {
                    arrayLength = _readable.readInt();
                }
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field brokerIds was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
                    }
                    this.brokerIds = newCollection;
                }
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
            _writable.writeInt(partitionIndex);
            if (_version >= 5) {
                _writable.writeUnsignedVarint(brokerIds.size() + 1);
            } else {
                _writable.writeInt(brokerIds.size());
            }
            for (Integer brokerIdsElement : brokerIds) {
                _writable.writeInt(brokerIdsElement);
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of CreatableReplicaAssignment");
            }
            _size.addBytes(4);
            {
                if (_version >= 5) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(brokerIds.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                _size.addBytes(brokerIds.size() * 4);
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
        public boolean elementKeysAreEqual(Object obj) {
            if (!(obj instanceof CreatableReplicaAssignment)) return false;
            CreatableReplicaAssignment other = (CreatableReplicaAssignment) obj;
            if (partitionIndex != other.partitionIndex) return false;
            return true;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CreatableReplicaAssignment)) return false;
            CreatableReplicaAssignment other = (CreatableReplicaAssignment) obj;
            if (partitionIndex != other.partitionIndex) return false;
            if (this.brokerIds == null) {
                if (other.brokerIds != null) return false;
            } else {
                if (!this.brokerIds.equals(other.brokerIds)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + partitionIndex;
            return hashCode;
        }
        
        @Override
        public CreatableReplicaAssignment duplicate() {
            CreatableReplicaAssignment _duplicate = new CreatableReplicaAssignment();
            _duplicate.partitionIndex = partitionIndex;
            ArrayList<Integer> newBrokerIds = new ArrayList<Integer>(brokerIds.size());
            for (Integer _element : brokerIds) {
                newBrokerIds.add(_element);
            }
            _duplicate.brokerIds = newBrokerIds;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "CreatableReplicaAssignment("
                + "partitionIndex=" + partitionIndex
                + ", brokerIds=" + MessageUtil.deepToString(brokerIds.iterator())
                + ")";
        }
        
        public int partitionIndex() {
            return this.partitionIndex;
        }
        
        public List<Integer> brokerIds() {
            return this.brokerIds;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public CreatableReplicaAssignment setPartitionIndex(int v) {
            this.partitionIndex = v;
            return this;
        }
        
        public CreatableReplicaAssignment setBrokerIds(List<Integer> v) {
            this.brokerIds = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    public static class CreatableReplicaAssignmentCollection extends ImplicitLinkedHashMultiCollection<CreatableReplicaAssignment> {
        public CreatableReplicaAssignmentCollection() {
            super();
        }
        
        public CreatableReplicaAssignmentCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public CreatableReplicaAssignmentCollection(Iterator<CreatableReplicaAssignment> iterator) {
            super(iterator);
        }
        
        public CreatableReplicaAssignment find(int partitionIndex) {
            CreatableReplicaAssignment _key = new CreatableReplicaAssignment();
            _key.setPartitionIndex(partitionIndex);
            return find(_key);
        }
        
        public List<CreatableReplicaAssignment> findAll(int partitionIndex) {
            CreatableReplicaAssignment _key = new CreatableReplicaAssignment();
            _key.setPartitionIndex(partitionIndex);
            return findAll(_key);
        }
        
        public CreatableReplicaAssignmentCollection duplicate() {
            CreatableReplicaAssignmentCollection _duplicate = new CreatableReplicaAssignmentCollection(size());
            for (CreatableReplicaAssignment _element : this) {
                _duplicate.add(_element.duplicate());
            }
            return _duplicate;
        }
    }
    
    public static class CreateableTopicConfig implements Message, ImplicitLinkedHashMultiCollection.Element {
        String name;
        String value;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.STRING, "The configuration name."),
                new Field("value", Type.NULLABLE_STRING, "The configuration value.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 = SCHEMA_1;
        
        public static final Schema SCHEMA_3 = SCHEMA_2;
        
        public static final Schema SCHEMA_4 = SCHEMA_3;
        
        public static final Schema SCHEMA_5 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The configuration name."),
                new Field("value", Type.COMPACT_NULLABLE_STRING, "The configuration value."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema SCHEMA_6 = SCHEMA_5;
        
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
        
        public CreateableTopicConfig(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public CreateableTopicConfig() {
            this.name = "";
            this.value = "";
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of CreateableTopicConfig");
            }
            {
                int length;
                if (_version >= 5) {
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
                int length;
                if (_version >= 5) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.value = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field value had invalid length " + length);
                } else {
                    this.value = _readable.readString(length);
                }
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
            {
                byte[] _stringBytes = _cache.getSerializedValue(name);
                if (_version >= 5) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (value == null) {
                if (_version >= 5) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(value);
                if (_version >= 5) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
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
            if (_version > 7) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of CreateableTopicConfig");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                if (_version >= 5) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            if (value == null) {
                if (_version >= 5) {
                    _size.addBytes(1);
                } else {
                    _size.addBytes(2);
                }
            } else {
                byte[] _stringBytes = value.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'value' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(value, _stringBytes);
                if (_version >= 5) {
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
            if (_version >= 5) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean elementKeysAreEqual(Object obj) {
            if (!(obj instanceof CreateableTopicConfig)) return false;
            CreateableTopicConfig other = (CreateableTopicConfig) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CreateableTopicConfig)) return false;
            CreateableTopicConfig other = (CreateableTopicConfig) obj;
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
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public CreateableTopicConfig duplicate() {
            CreateableTopicConfig _duplicate = new CreateableTopicConfig();
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
            return "CreateableTopicConfig("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", value=" + ((value == null) ? "null" : "'" + value.toString() + "'")
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public String value() {
            return this.value;
        }
        
        @Override
        public int next() {
            return this.next;
        }
        
        @Override
        public int prev() {
            return this.prev;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public CreateableTopicConfig setName(String v) {
            this.name = v;
            return this;
        }
        
        public CreateableTopicConfig setValue(String v) {
            this.value = v;
            return this;
        }
        
        @Override
        public void setNext(int v) {
            this.next = v;
        }
        
        @Override
        public void setPrev(int v) {
            this.prev = v;
        }
    }
    
    public static class CreateableTopicConfigCollection extends ImplicitLinkedHashMultiCollection<CreateableTopicConfig> {
        public CreateableTopicConfigCollection() {
            super();
        }
        
        public CreateableTopicConfigCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public CreateableTopicConfigCollection(Iterator<CreateableTopicConfig> iterator) {
            super(iterator);
        }
        
        public CreateableTopicConfig find(String name) {
            CreateableTopicConfig _key = new CreateableTopicConfig();
            _key.setName(name);
            return find(_key);
        }
        
        public List<CreateableTopicConfig> findAll(String name) {
            CreateableTopicConfig _key = new CreateableTopicConfig();
            _key.setName(name);
            return findAll(_key);
        }
        
        public CreateableTopicConfigCollection duplicate() {
            CreateableTopicConfigCollection _duplicate = new CreateableTopicConfigCollection(size());
            for (CreateableTopicConfig _element : this) {
                _duplicate.add(_element.duplicate());
            }
            return _duplicate;
        }
    }
    
    public static class CreatableTopicCollection extends ImplicitLinkedHashMultiCollection<CreatableTopic> {
        public CreatableTopicCollection() {
            super();
        }
        
        public CreatableTopicCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public CreatableTopicCollection(Iterator<CreatableTopic> iterator) {
            super(iterator);
        }
        
        public CreatableTopic find(String name) {
            CreatableTopic _key = new CreatableTopic();
            _key.setName(name);
            return find(_key);
        }
        
        public List<CreatableTopic> findAll(String name) {
            CreatableTopic _key = new CreatableTopic();
            _key.setName(name);
            return findAll(_key);
        }
        
        public CreatableTopicCollection duplicate() {
            CreatableTopicCollection _duplicate = new CreatableTopicCollection(size());
            for (CreatableTopic _element : this) {
                _duplicate.add(_element.duplicate());
            }
            return _duplicate;
        }
    }
}
