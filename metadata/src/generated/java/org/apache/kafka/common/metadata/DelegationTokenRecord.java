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


public class DelegationTokenRecord implements ApiMessage {
    String owner;
    List<String> renewers;
    long issueTimestamp;
    long maxTimestamp;
    long expirationTimestamp;
    String tokenId;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("owner", Type.COMPACT_STRING, "The delegation token owner."),
            new Field("renewers", new CompactArrayOf(Type.COMPACT_STRING), "The principals which have renewed this token."),
            new Field("issue_timestamp", Type.INT64, "The time at which this timestamp was issued."),
            new Field("max_timestamp", Type.INT64, "The time at which this token cannot be renewed any more."),
            new Field("expiration_timestamp", Type.INT64, "The next time at which this token must be renewed."),
            new Field("token_id", Type.COMPACT_STRING, "The token id."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public DelegationTokenRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DelegationTokenRecord() {
        this.owner = "";
        this.renewers = new ArrayList<String>(0);
        this.issueTimestamp = 0L;
        this.maxTimestamp = 0L;
        this.expirationTimestamp = 0L;
        this.tokenId = "";
    }
    
    @Override
    public short apiKey() {
        return 10;
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
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field owner was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field owner had invalid length " + length);
            } else {
                this.owner = _readable.readString(length);
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field renewers was serialized as null");
            } else {
                ArrayList<String> newCollection = new ArrayList<>(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    int length;
                    length = _readable.readUnsignedVarint() - 1;
                    if (length < 0) {
                        throw new RuntimeException("non-nullable field renewers element was serialized as null");
                    } else if (length > 0x7fff) {
                        throw new RuntimeException("string field renewers element had invalid length " + length);
                    } else {
                        newCollection.add(_readable.readString(length));
                    }
                }
                this.renewers = newCollection;
            }
        }
        this.issueTimestamp = _readable.readLong();
        this.maxTimestamp = _readable.readLong();
        this.expirationTimestamp = _readable.readLong();
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                throw new RuntimeException("non-nullable field tokenId was serialized as null");
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field tokenId had invalid length " + length);
            } else {
                this.tokenId = _readable.readString(length);
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
            byte[] _stringBytes = _cache.getSerializedValue(owner);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeUnsignedVarint(renewers.size() + 1);
        for (String renewersElement : renewers) {
            {
                byte[] _stringBytes = _cache.getSerializedValue(renewersElement);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
        }
        _writable.writeLong(issueTimestamp);
        _writable.writeLong(maxTimestamp);
        _writable.writeLong(expirationTimestamp);
        {
            byte[] _stringBytes = _cache.getSerializedValue(tokenId);
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
        {
            byte[] _stringBytes = owner.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'owner' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(owner, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(renewers.size() + 1));
            for (String renewersElement : renewers) {
                byte[] _stringBytes = renewersElement.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'renewersElement' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(renewersElement, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
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
        if (!(obj instanceof DelegationTokenRecord)) return false;
        DelegationTokenRecord other = (DelegationTokenRecord) obj;
        if (this.owner == null) {
            if (other.owner != null) return false;
        } else {
            if (!this.owner.equals(other.owner)) return false;
        }
        if (this.renewers == null) {
            if (other.renewers != null) return false;
        } else {
            if (!this.renewers.equals(other.renewers)) return false;
        }
        if (issueTimestamp != other.issueTimestamp) return false;
        if (maxTimestamp != other.maxTimestamp) return false;
        if (expirationTimestamp != other.expirationTimestamp) return false;
        if (this.tokenId == null) {
            if (other.tokenId != null) return false;
        } else {
            if (!this.tokenId.equals(other.tokenId)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + (owner == null ? 0 : owner.hashCode());
        hashCode = 31 * hashCode + (renewers == null ? 0 : renewers.hashCode());
        hashCode = 31 * hashCode + ((int) (issueTimestamp >> 32) ^ (int) issueTimestamp);
        hashCode = 31 * hashCode + ((int) (maxTimestamp >> 32) ^ (int) maxTimestamp);
        hashCode = 31 * hashCode + ((int) (expirationTimestamp >> 32) ^ (int) expirationTimestamp);
        hashCode = 31 * hashCode + (tokenId == null ? 0 : tokenId.hashCode());
        return hashCode;
    }
    
    @Override
    public DelegationTokenRecord duplicate() {
        DelegationTokenRecord _duplicate = new DelegationTokenRecord();
        _duplicate.owner = owner;
        ArrayList<String> newRenewers = new ArrayList<String>(renewers.size());
        for (String _element : renewers) {
            newRenewers.add(_element);
        }
        _duplicate.renewers = newRenewers;
        _duplicate.issueTimestamp = issueTimestamp;
        _duplicate.maxTimestamp = maxTimestamp;
        _duplicate.expirationTimestamp = expirationTimestamp;
        _duplicate.tokenId = tokenId;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "DelegationTokenRecord("
            + "owner=" + ((owner == null) ? "null" : "'" + owner.toString() + "'")
            + ", renewers=" + MessageUtil.deepToString(renewers.iterator())
            + ", issueTimestamp=" + issueTimestamp
            + ", maxTimestamp=" + maxTimestamp
            + ", expirationTimestamp=" + expirationTimestamp
            + ", tokenId=" + ((tokenId == null) ? "null" : "'" + tokenId.toString() + "'")
            + ")";
    }
    
    public String owner() {
        return this.owner;
    }
    
    public List<String> renewers() {
        return this.renewers;
    }
    
    public long issueTimestamp() {
        return this.issueTimestamp;
    }
    
    public long maxTimestamp() {
        return this.maxTimestamp;
    }
    
    public long expirationTimestamp() {
        return this.expirationTimestamp;
    }
    
    public String tokenId() {
        return this.tokenId;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DelegationTokenRecord setOwner(String v) {
        this.owner = v;
        return this;
    }
    
    public DelegationTokenRecord setRenewers(List<String> v) {
        this.renewers = v;
        return this;
    }
    
    public DelegationTokenRecord setIssueTimestamp(long v) {
        this.issueTimestamp = v;
        return this;
    }
    
    public DelegationTokenRecord setMaxTimestamp(long v) {
        this.maxTimestamp = v;
        return this;
    }
    
    public DelegationTokenRecord setExpirationTimestamp(long v) {
        this.expirationTimestamp = v;
        return this;
    }
    
    public DelegationTokenRecord setTokenId(String v) {
        this.tokenId = v;
        return this;
    }
}
