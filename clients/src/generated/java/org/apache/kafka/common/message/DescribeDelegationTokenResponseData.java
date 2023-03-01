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
import java.util.Arrays;
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
import org.apache.kafka.common.utils.Bytes;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class DescribeDelegationTokenResponseData implements ApiMessage {
    short errorCode;
    List<DescribedDelegationToken> tokens;
    int throttleTimeMs;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("tokens", new ArrayOf(DescribedDelegationToken.SCHEMA_0), "The tokens."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota.")
        );
    
    public static final Schema SCHEMA_1 = SCHEMA_0;
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("error_code", Type.INT16, "The error code, or 0 if there was no error."),
            new Field("tokens", new CompactArrayOf(DescribedDelegationToken.SCHEMA_2), "The tokens."),
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0,
        SCHEMA_1,
        SCHEMA_2
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 2;
    
    public DescribeDelegationTokenResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DescribeDelegationTokenResponseData() {
        this.errorCode = (short) 0;
        this.tokens = new ArrayList<DescribedDelegationToken>(0);
        this.throttleTimeMs = 0;
    }
    
    @Override
    public short apiKey() {
        return 41;
    }
    
    @Override
    public short lowestSupportedVersion() {
        return 0;
    }
    
    @Override
    public short highestSupportedVersion() {
        return 2;
    }
    
    @Override
    public void read(Readable _readable, short _version) {
        this.errorCode = _readable.readShort();
        {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field tokens was serialized as null");
                } else {
                    ArrayList<DescribedDelegationToken> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribedDelegationToken(_readable, _version));
                    }
                    this.tokens = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field tokens was serialized as null");
                } else {
                    ArrayList<DescribedDelegationToken> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DescribedDelegationToken(_readable, _version));
                    }
                    this.tokens = newCollection;
                }
            }
        }
        this.throttleTimeMs = _readable.readInt();
        this._unknownTaggedFields = null;
        if (_version >= 2) {
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
        _writable.writeShort(errorCode);
        if (_version >= 2) {
            _writable.writeUnsignedVarint(tokens.size() + 1);
            for (DescribedDelegationToken tokensElement : tokens) {
                tokensElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(tokens.size());
            for (DescribedDelegationToken tokensElement : tokens) {
                tokensElement.write(_writable, _cache, _version);
            }
        }
        _writable.writeInt(throttleTimeMs);
        RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
        _numTaggedFields += _rawWriter.numFields();
        if (_version >= 2) {
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
        _size.addBytes(2);
        {
            if (_version >= 2) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(tokens.size() + 1));
            } else {
                _size.addBytes(4);
            }
            for (DescribedDelegationToken tokensElement : tokens) {
                tokensElement.addSize(_size, _cache, _version);
            }
        }
        _size.addBytes(4);
        if (_unknownTaggedFields != null) {
            _numTaggedFields += _unknownTaggedFields.size();
            for (RawTaggedField _field : _unknownTaggedFields) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.tag()));
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_field.size()));
                _size.addBytes(_field.size());
            }
        }
        if (_version >= 2) {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
        } else {
            if (_numTaggedFields > 0) {
                throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DescribeDelegationTokenResponseData)) return false;
        DescribeDelegationTokenResponseData other = (DescribeDelegationTokenResponseData) obj;
        if (errorCode != other.errorCode) return false;
        if (this.tokens == null) {
            if (other.tokens != null) return false;
        } else {
            if (!this.tokens.equals(other.tokens)) return false;
        }
        if (throttleTimeMs != other.throttleTimeMs) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + errorCode;
        hashCode = 31 * hashCode + (tokens == null ? 0 : tokens.hashCode());
        hashCode = 31 * hashCode + throttleTimeMs;
        return hashCode;
    }
    
    @Override
    public DescribeDelegationTokenResponseData duplicate() {
        DescribeDelegationTokenResponseData _duplicate = new DescribeDelegationTokenResponseData();
        _duplicate.errorCode = errorCode;
        ArrayList<DescribedDelegationToken> newTokens = new ArrayList<DescribedDelegationToken>(tokens.size());
        for (DescribedDelegationToken _element : tokens) {
            newTokens.add(_element.duplicate());
        }
        _duplicate.tokens = newTokens;
        _duplicate.throttleTimeMs = throttleTimeMs;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "DescribeDelegationTokenResponseData("
            + "errorCode=" + errorCode
            + ", tokens=" + MessageUtil.deepToString(tokens.iterator())
            + ", throttleTimeMs=" + throttleTimeMs
            + ")";
    }
    
    public short errorCode() {
        return this.errorCode;
    }
    
    public List<DescribedDelegationToken> tokens() {
        return this.tokens;
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DescribeDelegationTokenResponseData setErrorCode(short v) {
        this.errorCode = v;
        return this;
    }
    
    public DescribeDelegationTokenResponseData setTokens(List<DescribedDelegationToken> v) {
        this.tokens = v;
        return this;
    }
    
    public DescribeDelegationTokenResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public static class DescribedDelegationToken implements Message {
        String principalType;
        String principalName;
        long issueTimestamp;
        long expiryTimestamp;
        long maxTimestamp;
        String tokenId;
        byte[] hmac;
        List<DescribedDelegationTokenRenewer> renewers;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("principal_type", Type.STRING, "The token principal type."),
                new Field("principal_name", Type.STRING, "The token principal name."),
                new Field("issue_timestamp", Type.INT64, "The token issue timestamp in milliseconds."),
                new Field("expiry_timestamp", Type.INT64, "The token expiry timestamp in milliseconds."),
                new Field("max_timestamp", Type.INT64, "The token maximum timestamp length in milliseconds."),
                new Field("token_id", Type.STRING, "The token ID."),
                new Field("hmac", Type.BYTES, "The token HMAC."),
                new Field("renewers", new ArrayOf(DescribedDelegationTokenRenewer.SCHEMA_0), "Those who are able to renew this token before it expires.")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("principal_type", Type.COMPACT_STRING, "The token principal type."),
                new Field("principal_name", Type.COMPACT_STRING, "The token principal name."),
                new Field("issue_timestamp", Type.INT64, "The token issue timestamp in milliseconds."),
                new Field("expiry_timestamp", Type.INT64, "The token expiry timestamp in milliseconds."),
                new Field("max_timestamp", Type.INT64, "The token maximum timestamp length in milliseconds."),
                new Field("token_id", Type.COMPACT_STRING, "The token ID."),
                new Field("hmac", Type.COMPACT_BYTES, "The token HMAC."),
                new Field("renewers", new CompactArrayOf(DescribedDelegationTokenRenewer.SCHEMA_2), "Those who are able to renew this token before it expires."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 2;
        
        public DescribedDelegationToken(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribedDelegationToken() {
            this.principalType = "";
            this.principalName = "";
            this.issueTimestamp = 0L;
            this.expiryTimestamp = 0L;
            this.maxTimestamp = 0L;
            this.tokenId = "";
            this.hmac = Bytes.EMPTY;
            this.renewers = new ArrayList<DescribedDelegationTokenRenewer>(0);
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribedDelegationToken");
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalType was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalType had invalid length " + length);
                } else {
                    this.principalType = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalName had invalid length " + length);
                } else {
                    this.principalName = _readable.readString(length);
                }
            }
            this.issueTimestamp = _readable.readLong();
            this.expiryTimestamp = _readable.readLong();
            this.maxTimestamp = _readable.readLong();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field tokenId was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field tokenId had invalid length " + length);
                } else {
                    this.tokenId = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readInt();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field hmac was serialized as null");
                } else {
                    byte[] newBytes = new byte[length];
                    _readable.readArray(newBytes);
                    this.hmac = newBytes;
                }
            }
            {
                if (_version >= 2) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field renewers was serialized as null");
                    } else {
                        ArrayList<DescribedDelegationTokenRenewer> newCollection = new ArrayList<>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new DescribedDelegationTokenRenewer(_readable, _version));
                        }
                        this.renewers = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field renewers was serialized as null");
                    } else {
                        ArrayList<DescribedDelegationTokenRenewer> newCollection = new ArrayList<>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new DescribedDelegationTokenRenewer(_readable, _version));
                        }
                        this.renewers = newCollection;
                    }
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 2) {
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
                byte[] _stringBytes = _cache.getSerializedValue(principalType);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(principalName);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeLong(issueTimestamp);
            _writable.writeLong(expiryTimestamp);
            _writable.writeLong(maxTimestamp);
            {
                byte[] _stringBytes = _cache.getSerializedValue(tokenId);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 2) {
                _writable.writeUnsignedVarint(hmac.length + 1);
            } else {
                _writable.writeInt(hmac.length);
            }
            _writable.writeByteArray(hmac);
            if (_version >= 2) {
                _writable.writeUnsignedVarint(renewers.size() + 1);
                for (DescribedDelegationTokenRenewer renewersElement : renewers) {
                    renewersElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(renewers.size());
                for (DescribedDelegationTokenRenewer renewersElement : renewers) {
                    renewersElement.write(_writable, _cache, _version);
                }
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribedDelegationToken");
            }
            {
                byte[] _stringBytes = principalType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalType, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                byte[] _stringBytes = principalName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalName, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            _size.addBytes(8);
            _size.addBytes(8);
            _size.addBytes(8);
            {
                byte[] _stringBytes = tokenId.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'tokenId' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(tokenId, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                _size.addBytes(hmac.length);
                if (_version >= 2) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(hmac.length + 1));
                } else {
                    _size.addBytes(4);
                }
            }
            {
                if (_version >= 2) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(renewers.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                for (DescribedDelegationTokenRenewer renewersElement : renewers) {
                    renewersElement.addSize(_size, _cache, _version);
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
            if (_version >= 2) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DescribedDelegationToken)) return false;
            DescribedDelegationToken other = (DescribedDelegationToken) obj;
            if (this.principalType == null) {
                if (other.principalType != null) return false;
            } else {
                if (!this.principalType.equals(other.principalType)) return false;
            }
            if (this.principalName == null) {
                if (other.principalName != null) return false;
            } else {
                if (!this.principalName.equals(other.principalName)) return false;
            }
            if (issueTimestamp != other.issueTimestamp) return false;
            if (expiryTimestamp != other.expiryTimestamp) return false;
            if (maxTimestamp != other.maxTimestamp) return false;
            if (this.tokenId == null) {
                if (other.tokenId != null) return false;
            } else {
                if (!this.tokenId.equals(other.tokenId)) return false;
            }
            if (!Arrays.equals(this.hmac, other.hmac)) return false;
            if (this.renewers == null) {
                if (other.renewers != null) return false;
            } else {
                if (!this.renewers.equals(other.renewers)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (principalType == null ? 0 : principalType.hashCode());
            hashCode = 31 * hashCode + (principalName == null ? 0 : principalName.hashCode());
            hashCode = 31 * hashCode + ((int) (issueTimestamp >> 32) ^ (int) issueTimestamp);
            hashCode = 31 * hashCode + ((int) (expiryTimestamp >> 32) ^ (int) expiryTimestamp);
            hashCode = 31 * hashCode + ((int) (maxTimestamp >> 32) ^ (int) maxTimestamp);
            hashCode = 31 * hashCode + (tokenId == null ? 0 : tokenId.hashCode());
            hashCode = 31 * hashCode + Arrays.hashCode(hmac);
            hashCode = 31 * hashCode + (renewers == null ? 0 : renewers.hashCode());
            return hashCode;
        }
        
        @Override
        public DescribedDelegationToken duplicate() {
            DescribedDelegationToken _duplicate = new DescribedDelegationToken();
            _duplicate.principalType = principalType;
            _duplicate.principalName = principalName;
            _duplicate.issueTimestamp = issueTimestamp;
            _duplicate.expiryTimestamp = expiryTimestamp;
            _duplicate.maxTimestamp = maxTimestamp;
            _duplicate.tokenId = tokenId;
            _duplicate.hmac = MessageUtil.duplicate(hmac);
            ArrayList<DescribedDelegationTokenRenewer> newRenewers = new ArrayList<DescribedDelegationTokenRenewer>(renewers.size());
            for (DescribedDelegationTokenRenewer _element : renewers) {
                newRenewers.add(_element.duplicate());
            }
            _duplicate.renewers = newRenewers;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "DescribedDelegationToken("
                + "principalType=" + ((principalType == null) ? "null" : "'" + principalType.toString() + "'")
                + ", principalName=" + ((principalName == null) ? "null" : "'" + principalName.toString() + "'")
                + ", issueTimestamp=" + issueTimestamp
                + ", expiryTimestamp=" + expiryTimestamp
                + ", maxTimestamp=" + maxTimestamp
                + ", tokenId=" + ((tokenId == null) ? "null" : "'" + tokenId.toString() + "'")
                + ", hmac=" + Arrays.toString(hmac)
                + ", renewers=" + MessageUtil.deepToString(renewers.iterator())
                + ")";
        }
        
        public String principalType() {
            return this.principalType;
        }
        
        public String principalName() {
            return this.principalName;
        }
        
        public long issueTimestamp() {
            return this.issueTimestamp;
        }
        
        public long expiryTimestamp() {
            return this.expiryTimestamp;
        }
        
        public long maxTimestamp() {
            return this.maxTimestamp;
        }
        
        public String tokenId() {
            return this.tokenId;
        }
        
        public byte[] hmac() {
            return this.hmac;
        }
        
        public List<DescribedDelegationTokenRenewer> renewers() {
            return this.renewers;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribedDelegationToken setPrincipalType(String v) {
            this.principalType = v;
            return this;
        }
        
        public DescribedDelegationToken setPrincipalName(String v) {
            this.principalName = v;
            return this;
        }
        
        public DescribedDelegationToken setIssueTimestamp(long v) {
            this.issueTimestamp = v;
            return this;
        }
        
        public DescribedDelegationToken setExpiryTimestamp(long v) {
            this.expiryTimestamp = v;
            return this;
        }
        
        public DescribedDelegationToken setMaxTimestamp(long v) {
            this.maxTimestamp = v;
            return this;
        }
        
        public DescribedDelegationToken setTokenId(String v) {
            this.tokenId = v;
            return this;
        }
        
        public DescribedDelegationToken setHmac(byte[] v) {
            this.hmac = v;
            return this;
        }
        
        public DescribedDelegationToken setRenewers(List<DescribedDelegationTokenRenewer> v) {
            this.renewers = v;
            return this;
        }
    }
    
    public static class DescribedDelegationTokenRenewer implements Message {
        String principalType;
        String principalName;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("principal_type", Type.STRING, "The renewer principal type"),
                new Field("principal_name", Type.STRING, "The renewer principal name")
            );
        
        public static final Schema SCHEMA_1 = SCHEMA_0;
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("principal_type", Type.COMPACT_STRING, "The renewer principal type"),
                new Field("principal_name", Type.COMPACT_STRING, "The renewer principal name"),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0,
            SCHEMA_1,
            SCHEMA_2
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 2;
        
        public DescribedDelegationTokenRenewer(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DescribedDelegationTokenRenewer() {
            this.principalType = "";
            this.principalName = "";
        }
        
        
        @Override
        public short lowestSupportedVersion() {
            return 0;
        }
        
        @Override
        public short highestSupportedVersion() {
            return 2;
        }
        
        @Override
        public void read(Readable _readable, short _version) {
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't read version " + _version + " of DescribedDelegationTokenRenewer");
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalType was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalType had invalid length " + length);
                } else {
                    this.principalType = _readable.readString(length);
                }
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principalName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principalName had invalid length " + length);
                } else {
                    this.principalName = _readable.readString(length);
                }
            }
            this._unknownTaggedFields = null;
            if (_version >= 2) {
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
                byte[] _stringBytes = _cache.getSerializedValue(principalType);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(principalName);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            if (_version >= 2) {
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
            if (_version > 2) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of DescribedDelegationTokenRenewer");
            }
            {
                byte[] _stringBytes = principalType.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalType' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalType, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                byte[] _stringBytes = principalName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principalName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principalName, _stringBytes);
                if (_version >= 2) {
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
            if (_version >= 2) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(_numTaggedFields));
            } else {
                if (_numTaggedFields > 0) {
                    throw new UnsupportedVersionException("Tagged fields were set, but version " + _version + " of this message does not support them.");
                }
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DescribedDelegationTokenRenewer)) return false;
            DescribedDelegationTokenRenewer other = (DescribedDelegationTokenRenewer) obj;
            if (this.principalType == null) {
                if (other.principalType != null) return false;
            } else {
                if (!this.principalType.equals(other.principalType)) return false;
            }
            if (this.principalName == null) {
                if (other.principalName != null) return false;
            } else {
                if (!this.principalName.equals(other.principalName)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (principalType == null ? 0 : principalType.hashCode());
            hashCode = 31 * hashCode + (principalName == null ? 0 : principalName.hashCode());
            return hashCode;
        }
        
        @Override
        public DescribedDelegationTokenRenewer duplicate() {
            DescribedDelegationTokenRenewer _duplicate = new DescribedDelegationTokenRenewer();
            _duplicate.principalType = principalType;
            _duplicate.principalName = principalName;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "DescribedDelegationTokenRenewer("
                + "principalType=" + ((principalType == null) ? "null" : "'" + principalType.toString() + "'")
                + ", principalName=" + ((principalName == null) ? "null" : "'" + principalName.toString() + "'")
                + ")";
        }
        
        public String principalType() {
            return this.principalType;
        }
        
        public String principalName() {
            return this.principalName;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DescribedDelegationTokenRenewer setPrincipalType(String v) {
            this.principalType = v;
            return this;
        }
        
        public DescribedDelegationTokenRenewer setPrincipalName(String v) {
            this.principalName = v;
            return this;
        }
    }
}
