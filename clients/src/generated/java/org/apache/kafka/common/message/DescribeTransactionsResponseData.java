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


public class DescribeTransactionsResponseData implements ApiMessage {
    int throttleTimeMs;
    List<TransactionState> transactionStates;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("transaction_states", new CompactArrayOf(TransactionState.SCHEMA_0), ""),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public DescribeTransactionsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeTransactionsResponseData() {
        this.throttleTimeMs = 0;
        this.transactionStates = new ArrayList<TransactionState>(0);
    }
    
    @Override
    public short apiKey() {
        return 65;
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
        this.throttleTimeMs = _readable.readInt();
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field transactionStates was serialized as null");
            } else {
                ArrayList<TransactionState> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new TransactionState(_readable, _version));
                }
                this.transactionStates = newCollection;
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
        _writable.writeInt(throttleTimeMs);
        _writable.writeUnsignedVarint(transactionStates.size() + 1);
        for (TransactionState transactionStatesElement : transactionStates) {
            transactionStatesElement.write(_writable, _cache, _version);
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _size.addBytes(4);
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(transactionStates.size() + 1));
            for (TransactionState transactionStatesElement : transactionStates) {
                transactionStatesElement.addSize(_size, _cache, _version);
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
        if (!(obj instanceof DescribeTransactionsResponseData)) return false;
        DescribeTransactionsResponseData other = (DescribeTransactionsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.transactionStates == null) {
            if (other.transactionStates != null) return false;
        } else {
            if (!this.transactionStates.equals(other.transactionStates)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (transactionStates == null ? 0 : transactionStates.hashCode());
        return hashCode;
    }
    
    @Override
    public DescribeTransactionsResponseData duplicate() {
        DescribeTransactionsResponseData _duplicate = new DescribeTransactionsResponseData();
        _duplicate.throttleTimeMs = throttleTimeMs;
        ArrayList<TransactionState> newTransactionStates = new ArrayList<TransactionState>(transactionStates.size());
        for (TransactionState _element : transactionStates) {
            newTransactionStates.add(_element.duplicate());
        }
        _duplicate.transactionStates = newTransactionStates;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "DescribeTransactionsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", transactionStates=" + MessageUtil.deepToString(transactionStates.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<TransactionState> transactionStates() {
        return this.transactionStates;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeTransactionsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public DescribeTransactionsResponseData setTransactionStates(List<TransactionState> v) {
        this.transactionStates = v;
        return this;
    }
    
    public static class TransactionState implements Message {
        short errorCode;
        String transactionalId;
        String transactionState;
        int transactionTimeoutMs;
        long transactionStartTimeMs;
        long producerId;
        short producerEpoch;
        TopicDataCollection topics;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, ""),
                new Field("transactional_id", Type.COMPACT_STRING, ""),
                new Field("transaction_state", Type.COMPACT_STRING, ""),
                new Field("transaction_timeout_ms", Type.INT32, ""),
                new Field("transaction_start_time_ms", Type.INT64, ""),
                new Field("producer_id", Type.INT64, ""),
                new Field("producer_epoch", Type.INT16, ""),
                new Field("topics", new CompactArrayOf(TopicData.SCHEMA_0), "The set of partitions included in the current transaction (if active). When a transaction is preparing to commit or abort, this will include only partitions which do not have markers."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public TransactionState(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public TransactionState() {
            this.errorCode = (short) 0;
            this.transactionalId = "";
            this.transactionState = "";
            this.transactionTimeoutMs = 0;
            this.transactionStartTimeMs = 0L;
            this.producerId = 0L;
            this.producerEpoch = (short) 0;
            this.topics = new TopicDataCollection(0);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of TransactionState");
            }
            this.errorCode = _readable.readShort();
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    throw new RuntimeException("non-nullable field transactionalId was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field transactionalId had invalid length " + length);
                } else {
                    this.transactionalId = _readable.readString(length);
                }
            }
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    throw new RuntimeException("non-nullable field transactionState was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field transactionState had invalid length " + length);
                } else {
                    this.transactionState = _readable.readString(length);
                }
            }
            this.transactionTimeoutMs = _readable.readInt();
            this.transactionStartTimeMs = _readable.readLong();
            this.producerId = _readable.readLong();
            this.producerEpoch = _readable.readShort();
            {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field topics was serialized as null");
                } else {
                    TopicDataCollection newCollection = new TopicDataCollection(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new TopicData(_readable, _version));
                    }
                    this.topics = newCollection;
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
            _writable.writeShort(errorCode);
            {
                byte[] _stringBytes = _cache.getSerializedValue(transactionalId);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(transactionState);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeInt(transactionTimeoutMs);
            _writable.writeLong(transactionStartTimeMs);
            _writable.writeLong(producerId);
            _writable.writeShort(producerEpoch);
            _writable.writeUnsignedVarint(topics.size() + 1);
            for (TopicData topicsElement : topics) {
                topicsElement.write(_writable, _cache, _version);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of TransactionState");
            }
            _size.addBytes(2);
            {
                byte[] _stringBytes = transactionalId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'transactionalId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(transactionalId, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            {
                byte[] _stringBytes = transactionState.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'transactionState' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(transactionState, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            _size.addBytes(4);
            _size.addBytes(8);
            _size.addBytes(8);
            _size.addBytes(2);
            {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(topics.size() + 1));
                for (TopicData topicsElement : topics) {
                    topicsElement.addSize(_size, _cache, _version);
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
            if (!(obj instanceof TransactionState)) return false;
            TransactionState other = (TransactionState) obj;
            if (errorCode != other.errorCode) return false;
            if (this.transactionalId == null) {
                if (other.transactionalId != null) return false;
            } else {
                if (!this.transactionalId.equals(other.transactionalId)) return false;
            }
            if (this.transactionState == null) {
                if (other.transactionState != null) return false;
            } else {
                if (!this.transactionState.equals(other.transactionState)) return false;
            }
            if (transactionTimeoutMs != other.transactionTimeoutMs) return false;
            if (transactionStartTimeMs != other.transactionStartTimeMs) return false;
            if (producerId != other.producerId) return false;
            if (producerEpoch != other.producerEpoch) return false;
            if (this.topics == null) {
                if (other.topics != null) return false;
            } else {
                if (!this.topics.equals(other.topics)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
            hashCode = 31 * hashCode + (transactionState == null ? 0 : transactionState.hashCode());
            hashCode = 31 * hashCode + transactionTimeoutMs;
            hashCode = 31 * hashCode + ((int) (transactionStartTimeMs >> 32) ^ (int) transactionStartTimeMs);
            hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
            hashCode = 31 * hashCode + producerEpoch;
            hashCode = 31 * hashCode + (topics == null ? 0 : topics.hashCode());
            return hashCode;
        }
        
        @Override
        public TransactionState duplicate() {
            TransactionState _duplicate = new TransactionState();
            _duplicate.errorCode = errorCode;
            _duplicate.transactionalId = transactionalId;
            _duplicate.transactionState = transactionState;
            _duplicate.transactionTimeoutMs = transactionTimeoutMs;
            _duplicate.transactionStartTimeMs = transactionStartTimeMs;
            _duplicate.producerId = producerId;
            _duplicate.producerEpoch = producerEpoch;
            TopicDataCollection newTopics = new TopicDataCollection(topics.size());
            for (TopicData _element : topics) {
                newTopics.add(_element.duplicate());
            }
            _duplicate.topics = newTopics;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "TransactionState("
                + "errorCode=" + errorCode
                + ", transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
                + ", transactionState=" + ((transactionState == null) ? "null" : "'" + transactionState.toString() + "'")
                + ", transactionTimeoutMs=" + transactionTimeoutMs
                + ", transactionStartTimeMs=" + transactionStartTimeMs
                + ", producerId=" + producerId
                + ", producerEpoch=" + producerEpoch
                + ", topics=" + MessageUtil.deepToString(topics.iterator())
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String transactionalId() {
            return this.transactionalId;
        }
        
        public String transactionState() {
            return this.transactionState;
        }
        
        public int transactionTimeoutMs() {
            return this.transactionTimeoutMs;
        }
        
        public long transactionStartTimeMs() {
            return this.transactionStartTimeMs;
        }
        
        public long producerId() {
            return this.producerId;
        }
        
        public short producerEpoch() {
            return this.producerEpoch;
        }
        
        public TopicDataCollection topics() {
            return this.topics;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TransactionState setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public TransactionState setTransactionalId(String v) {
            this.transactionalId = v;
            return this;
        }
        
        public TransactionState setTransactionState(String v) {
            this.transactionState = v;
            return this;
        }
        
        public TransactionState setTransactionTimeoutMs(int v) {
            this.transactionTimeoutMs = v;
            return this;
        }
        
        public TransactionState setTransactionStartTimeMs(long v) {
            this.transactionStartTimeMs = v;
            return this;
        }
        
        public TransactionState setProducerId(long v) {
            this.producerId = v;
            return this;
        }
        
        public TransactionState setProducerEpoch(short v) {
            this.producerEpoch = v;
            return this;
        }
        
        public TransactionState setTopics(TopicDataCollection v) {
            this.topics = v;
            return this;
        }
    }
    
    public static class TopicData implements Message, ImplicitLinkedHashMultiCollection.Element {
        String topic;
        List<Integer> partitions;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("topic", Type.COMPACT_STRING, ""),
                new Field("partitions", new CompactArrayOf(Type.INT32), ""),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public TopicData(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public TopicData() {
            this.topic = "";
            this.partitions = new ArrayList<Integer>(0);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of TopicData");
            }
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    throw new RuntimeException("non-nullable field topic was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field topic had invalid length " + length);
                } else {
                    this.topic = _readable.readString(length);
                }
            }
            {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field partitions was serialized as null");
                } else {
                    ArrayList<Integer> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(_readable.readInt());
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
                byte[] _stringBytes = _cache.getSerializedValue(topic);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeUnsignedVarint(partitions.size() + 1);
            for (Integer partitionsElement : partitions) {
                _writable.writeInt(partitionsElement);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of TopicData");
            }
            {
                byte[] _stringBytes = topic.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'topic' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(topic, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(partitions.size() + 1));
                _size.addBytes(partitions.size() * 4);
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
        public boolean elementKeysAreEqual(Object obj) {
            if (!(obj instanceof TopicData)) return false;
            TopicData other = (TopicData) obj;
            if (this.topic == null) {
                if (other.topic != null) return false;
            } else {
                if (!this.topic.equals(other.topic)) return false;
            }
            return true;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TopicData)) return false;
            TopicData other = (TopicData) obj;
            if (this.topic == null) {
                if (other.topic != null) return false;
            } else {
                if (!this.topic.equals(other.topic)) return false;
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
            hashCode = 31 * hashCode + (topic == null ? 0 : topic.hashCode());
            return hashCode;
        }
        
        @Override
        public TopicData duplicate() {
            TopicData _duplicate = new TopicData();
            _duplicate.topic = topic;
            ArrayList<Integer> newPartitions = new ArrayList<Integer>(partitions.size());
            for (Integer _element : partitions) {
                newPartitions.add(_element);
            }
            _duplicate.partitions = newPartitions;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "TopicData("
                + "topic=" + ((topic == null) ? "null" : "'" + topic.toString() + "'")
                + ", partitions=" + MessageUtil.deepToString(partitions.iterator())
                + ")";
        }
        
        public String topic() {
            return this.topic;
        }
        
        public List<Integer> partitions() {
            return this.partitions;
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
        
        public TopicData setTopic(String v) {
            this.topic = v;
            return this;
        }
        
        public TopicData setPartitions(List<Integer> v) {
            this.partitions = v;
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
    
    public static class TopicDataCollection extends ImplicitLinkedHashMultiCollection<TopicData> {
        public TopicDataCollection() {
            super();
        }
        
        public TopicDataCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public TopicDataCollection(Iterator<TopicData> iterator) {
            super(iterator);
        }
        
        public TopicData find(String topic) {
            TopicData _key = new TopicData();
            _key.setTopic(topic);
            return find(_key);
        }
        
        public List<TopicData> findAll(String topic) {
            TopicData _key = new TopicData();
            _key.setTopic(topic);
            return findAll(_key);
        }
        
        public TopicDataCollection duplicate() {
            TopicDataCollection _duplicate = new TopicDataCollection(size());
            for (TopicData _element : this) {
                _duplicate.add(_element.duplicate());
            }
            return _duplicate;
        }
    }
}
