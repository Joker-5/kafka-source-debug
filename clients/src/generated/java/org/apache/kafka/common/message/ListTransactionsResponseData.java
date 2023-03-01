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
import org.apache.kafka.common.protocol.types.CompactArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class ListTransactionsResponseData implements ApiMessage {
    int throttleTimeMs;
    short errorCode;
    List<String> unknownStateFilters;
    List<TransactionState> transactionStates;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("error_code", Type.INT16, ""),
            new Field("unknown_state_filters", new CompactArrayOf(Type.COMPACT_STRING), "Set of state filters provided in the request which were unknown to the transaction coordinator"),
            new Field("transaction_states", new CompactArrayOf(TransactionState.SCHEMA_0), ""),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public ListTransactionsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ListTransactionsResponseData() {
        this.throttleTimeMs = 0;
        this.errorCode = (short) 0;
        this.unknownStateFilters = new ArrayList<String>(0);
        this.transactionStates = new ArrayList<TransactionState>(0);
    }
    
    @Override
    public short apiKey() {
        return 66;
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
        this.errorCode = _readable.readShort();
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field unknownStateFilters was serialized as null");
            } else {
                ArrayList<String> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    int length;
                    length = _readable.readUnsignedVarint() - 1;
                    if (length < 0) {
                        throw new RuntimeException("non-nullable field unknownStateFilters element was serialized as null");
                    } else if (length > 0x7fff) {
                        throw new RuntimeException("string field unknownStateFilters element had invalid length " + length);
                    } else {
                        newCollection.add(_readable.readString(length));
                    }
                }
                this.unknownStateFilters = newCollection;
            }
        }
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
        _writable.writeShort(errorCode);
        _writable.writeUnsignedVarint(unknownStateFilters.size() + 1);
        for (String unknownStateFiltersElement : unknownStateFilters) {
            {
                byte[] _stringBytes = _cache.getSerializedValue(unknownStateFiltersElement);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        }
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
        _size.addBytes(2);
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(unknownStateFilters.size() + 1));
            for (String unknownStateFiltersElement : unknownStateFilters) {
                byte[] _stringBytes = unknownStateFiltersElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'unknownStateFiltersElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(unknownStateFiltersElement, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
        }
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
        if (!(obj instanceof ListTransactionsResponseData)) return false;
        ListTransactionsResponseData other = (ListTransactionsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (errorCode != other.errorCode) return false;
        if (this.unknownStateFilters == null) {
            if (other.unknownStateFilters != null) return false;
        } else {
            if (!this.unknownStateFilters.equals(other.unknownStateFilters)) return false;
        }
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
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (unknownStateFilters == null ? 0 : unknownStateFilters.hashCode());
        hashCode = 31 * hashCode + (transactionStates == null ? 0 : transactionStates.hashCode());
        return hashCode;
    }
    
    @Override
    public ListTransactionsResponseData duplicate() {
        ListTransactionsResponseData _duplicate = new ListTransactionsResponseData();
        _duplicate.throttleTimeMs = throttleTimeMs;
        _duplicate.errorCode = errorCode;
        ArrayList<String> newUnknownStateFilters = new ArrayList<String>(unknownStateFilters.size());
        for (String _element : unknownStateFilters) {
            newUnknownStateFilters.add(_element);
        }
        _duplicate.unknownStateFilters = newUnknownStateFilters;
        ArrayList<TransactionState> newTransactionStates = new ArrayList<TransactionState>(transactionStates.size());
        for (TransactionState _element : transactionStates) {
            newTransactionStates.add(_element.duplicate());
        }
        _duplicate.transactionStates = newTransactionStates;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "ListTransactionsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", errorCode=" + errorCode
            + ", unknownStateFilters=" + MessageUtil.deepToString(unknownStateFilters.iterator())
            + ", transactionStates=" + MessageUtil.deepToString(transactionStates.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<String> unknownStateFilters() {
        return this.unknownStateFilters;
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
    
    public ListTransactionsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public ListTransactionsResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public ListTransactionsResponseData setUnknownStateFilters(List<String> v) {
        this.unknownStateFilters = v;
        return this;
    }
    
    public ListTransactionsResponseData setTransactionStates(List<TransactionState> v) {
        this.transactionStates = v;
        return this;
    }
    
    public static class TransactionState implements Message {
        String transactionalId;
        long producerId;
        String transactionState;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("transactional_id", Type.COMPACT_STRING, ""),
                new Field("producer_id", Type.INT64, ""),
                new Field("transaction_state", Type.COMPACT_STRING, "The current transaction state of the producer"),
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
            this.transactionalId = "";
            this.producerId = 0L;
            this.transactionState = "";
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
            this.producerId = _readable.readLong();
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
                byte[] _stringBytes = _cache.getSerializedValue(transactionalId);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeLong(producerId);
            {
                byte[] _stringBytes = _cache.getSerializedValue(transactionState);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
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
            {
                byte[] _stringBytes = transactionalId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'transactionalId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(transactionalId, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            _size.addBytes(8);
            {
                byte[] _stringBytes = transactionState.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'transactionState' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(transactionState, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
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
            if (this.transactionalId == null) {
                if (other.transactionalId != null) return false;
            } else {
                if (!this.transactionalId.equals(other.transactionalId)) return false;
            }
            if (producerId != other.producerId) return false;
            if (this.transactionState == null) {
                if (other.transactionState != null) return false;
            } else {
                if (!this.transactionState.equals(other.transactionState)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (transactionalId == null ? 0 : transactionalId.hashCode());
            hashCode = 31 * hashCode + ((int) (producerId >> 32) ^ (int) producerId);
            hashCode = 31 * hashCode + (transactionState == null ? 0 : transactionState.hashCode());
            return hashCode;
        }
        
        @Override
        public TransactionState duplicate() {
            TransactionState _duplicate = new TransactionState();
            _duplicate.transactionalId = transactionalId;
            _duplicate.producerId = producerId;
            _duplicate.transactionState = transactionState;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "TransactionState("
                + "transactionalId=" + ((transactionalId == null) ? "null" : "'" + transactionalId.toString() + "'")
                + ", producerId=" + producerId
                + ", transactionState=" + ((transactionState == null) ? "null" : "'" + transactionState.toString() + "'")
                + ")";
        }
        
        public String transactionalId() {
            return this.transactionalId;
        }
        
        public long producerId() {
            return this.producerId;
        }
        
        public String transactionState() {
            return this.transactionState;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public TransactionState setTransactionalId(String v) {
            this.transactionalId = v;
            return this;
        }
        
        public TransactionState setProducerId(long v) {
            this.producerId = v;
            return this;
        }
        
        public TransactionState setTransactionState(String v) {
            this.transactionState = v;
            return this;
        }
    }
}
