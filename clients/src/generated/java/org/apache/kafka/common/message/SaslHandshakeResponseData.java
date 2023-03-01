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


public class SaslHandshakeResponseData implements ApiMessage {
    short errorCode;
    List<String> mechanisms;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("mechanisms", new ArrayOf(Type.STRING), "The mechanisms enabled in the server.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 1;
    
    public SaslHandshakeResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public SaslHandshakeResponseData() {
        this.errorCode = (short) 0;
        this.mechanisms = new ArrayList<String>(0);
    }
    
    @Override
    public short apiKey() {
        return 17;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 1;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.errorCode = _readable.readShort();
        {
            int arrayLength;
            arrayLength = _readable.readInt();
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field mechanisms was serialized as null");
            } else {
                ArrayList<String> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    int length;
                    length = _readable.readShort();
                    if (length < 0) {
                        throw new RuntimeException("non-nullable field mechanisms element was serialized as null");
                    } else if (length > 0x7fff) {
                        throw new RuntimeException("string field mechanisms element had invalid length " + length);
                    } else {
                        newCollection.add(_readable.readString(length));
                    }
                }
                this.mechanisms = newCollection;
            }
        }
        this._unknownTaggedFields = null;
    }
    
    @Override
    public void write(Writable _writable, ObjectSerializationCache _cache, short _version) {
        int _numTaggedFields = 0;
        _writable.writeShort(errorCode);
        _writable.writeInt(mechanisms.size());
        for (String mechanismsElement : mechanisms) {
            {
                byte[] _stringBytes = _cache.getSerializedValue(mechanismsElement);
                _writable.writeShort((short) _stringBytes.length);
                _writable.writeByteArray(_stringBytes);
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
        _size.addBytes(2);
        {
            _size.addBytes(4);
            for (String mechanismsElement : mechanisms) {
                byte[] _stringBytes = mechanismsElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'mechanismsElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(mechanismsElement, _stringBytes);
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
        if (_numTaggedFields > 0) {
            throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SaslHandshakeResponseData)) return false;
        SaslHandshakeResponseData other = (SaslHandshakeResponseData) obj;
        if (errorCode != other.errorCode) return false;
        if (this.mechanisms == null) {
            if (other.mechanisms != null) return false;
        } else {
            if (!this.mechanisms.equals(other.mechanisms)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (mechanisms == null ? 0 : mechanisms.hashCode());
        return hashCode;
    }
    
    @Override
    public SaslHandshakeResponseData duplicate() {
        SaslHandshakeResponseData _duplicate = new SaslHandshakeResponseData();
        _duplicate.errorCode = errorCode;
        ArrayList<String> newMechanisms = new ArrayList<String>(mechanisms.size());
        for (String _element : mechanisms) {
            newMechanisms.add(_element);
        }
        _duplicate.mechanisms = newMechanisms;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "SaslHandshakeResponseData("
            + "errorCode=" + errorCode
            + ", mechanisms=" + MessageUtil.deepToString(mechanisms.iterator())
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<String> mechanisms() {
        return this.mechanisms;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public SaslHandshakeResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public SaslHandshakeResponseData setMechanisms(List<String> v) {
        this.mechanisms = v;
        return this;
    }
}
