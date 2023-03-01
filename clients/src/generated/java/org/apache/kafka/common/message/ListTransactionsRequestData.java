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


public class ListTransactionsRequestData implements ApiMessage {
    List<String> stateFilters;
    List<Long> producerIdFilters;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("state_filters", new CompactArrayOf(Type.COMPACT_STRING), "The transaction states to filter by: if empty, all transactions are returned; if non-empty, then only transactions matching one of the filtered states will be returned"),
            new Field("producer_id_filters", new CompactArrayOf(Type.INT64), "The producerIds to filter by: if empty, all transactions will be returned; if non-empty, only transactions which match one of the filtered producerIds will be returned"),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public ListTransactionsRequestData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public ListTransactionsRequestData() {
        this.stateFilters = new ArrayList<String>(0);
        this.producerIdFilters = new ArrayList<Long>(0);
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
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field stateFilters was serialized as null");
            } else {
                ArrayList<String> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    int length;
                    length = _readable.readUnsignedVarint() - 1;
                    if (length < 0) {
                        throw new RuntimeException("non-nullable field stateFilters element was serialized as null");
                    } else if (length > 0x7fff) {
                        throw new RuntimeException("string field stateFilters element had invalid length " + length);
                    } else {
                        newCollection.add(_readable.readString(length));
                    }
                }
                this.stateFilters = newCollection;
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field producerIdFilters was serialized as null");
            } else {
                ArrayList<Long> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(_readable.readLong());
                }
                this.producerIdFilters = newCollection;
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
        _writable.writeUnsignedVarint(stateFilters.size() + 1);
        for (String stateFiltersElement : stateFilters) {
            {
                byte[] _stringBytes = _cache.getSerializedValue(stateFiltersElement);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        }
        _writable.writeUnsignedVarint(producerIdFilters.size() + 1);
        for (Long producerIdFiltersElement : producerIdFilters) {
            _writable.writeLong(producerIdFiltersElement);
        }
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        _writable.writeUnsignedVarint(_numTaggedFields);
        _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
    }
    
    @Override
    public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(stateFilters.size() + 1));
            for (String stateFiltersElement : stateFilters) {
                byte[] _stringBytes = stateFiltersElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'stateFiltersElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(stateFiltersElement, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
        }
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(producerIdFilters.size() + 1));
            _size.addBytes(producerIdFilters.size() * 8);
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
        if (!(obj instanceof ListTransactionsRequestData)) return false;
        ListTransactionsRequestData other = (ListTransactionsRequestData) obj;
        if (this.stateFilters == null) {
            if (other.stateFilters != null) return false;
        } else {
            if (!this.stateFilters.equals(other.stateFilters)) return false;
        }
        if (this.producerIdFilters == null) {
            if (other.producerIdFilters != null) return false;
        } else {
            if (!this.producerIdFilters.equals(other.producerIdFilters)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (stateFilters == null ? 0 : stateFilters.hashCode());
        hashCode = 31 * hashCode + (producerIdFilters == null ? 0 : producerIdFilters.hashCode());
        return hashCode;
    }
    
    @Override
    public ListTransactionsRequestData duplicate() {
        ListTransactionsRequestData _duplicate = new ListTransactionsRequestData();
        ArrayList<String> newStateFilters = new ArrayList<String>(stateFilters.size());
        for (String _element : stateFilters) {
            newStateFilters.add(_element);
        }
        _duplicate.stateFilters = newStateFilters;
        ArrayList<Long> newProducerIdFilters = new ArrayList<Long>(producerIdFilters.size());
        for (Long _element : producerIdFilters) {
            newProducerIdFilters.add(_element);
        }
        _duplicate.producerIdFilters = newProducerIdFilters;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "ListTransactionsRequestData("
            + "stateFilters=" + MessageUtil.deepToString(stateFilters.iterator())
            + ", producerIdFilters=" + MessageUtil.deepToString(producerIdFilters.iterator())
            + ")";
    }
    
    public List<String> stateFilters() {
        return this.stateFilters;
    }
    
    public List<Long> producerIdFilters() {
        return this.producerIdFilters;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public ListTransactionsRequestData setStateFilters(List<String> v) {
        this.stateFilters = v;
        return this;
    }
    
    public ListTransactionsRequestData setProducerIdFilters(List<Long> v) {
        this.producerIdFilters = v;
        return this;
    }
}
