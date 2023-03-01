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
import org.apache.kafka.common.protocol.types.ArrayOf;
import org.apache.kafka.common.protocol.types.CompactArrayOf;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.protocol.types.RawTaggedField;
import org.apache.kafka.common.protocol.types.RawTaggedFieldWriter;
import org.apache.kafka.common.protocol.types.Schema;
import org.apache.kafka.common.protocol.types.Type;
import org.apache.kafka.common.utils.ByteUtils;

import static org.apache.kafka.common.protocol.types.Field.TaggedFieldsSection;


public class DeleteAclsResponseData implements ApiMessage {
    int throttleTimeMs;
    List<DeleteAclsFilterResult> filterResults;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("filter_results", new ArrayOf(DeleteAclsFilterResult.SCHEMA_0), "The results for each filter.")
        );
    
    public static final Schema SCHEMA_1 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("filter_results", new ArrayOf(DeleteAclsFilterResult.SCHEMA_1), "The results for each filter.")
        );
    
    public static final Schema SCHEMA_2 =
        new Schema(
            new Field("throttle_time_ms", Type.INT32, "The duration in milliseconds for which the request was throttled due to a quota violation, or zero if the request did not violate any quota."),
            new Field("filter_results", new CompactArrayOf(DeleteAclsFilterResult.SCHEMA_2), "The results for each filter."),
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
    
    public DeleteAclsResponseData(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public DeleteAclsResponseData() {
        this.throttleTimeMs = 0;
        this.filterResults = new ArrayList<DeleteAclsFilterResult>(0);
    }
    
    @Override
    public short apiKey() {
        return 31;
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
        this.throttleTimeMs = _readable.readInt();
        {
            if (_version >= 2) {
                int arrayLength;
                arrayLength = _readable.readUnsignedVarint() - 1;
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field filterResults was serialized as null");
                } else {
                    ArrayList<DeleteAclsFilterResult> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DeleteAclsFilterResult(_readable, _version));
                    }
                    this.filterResults = newCollection;
                }
            } else {
                int arrayLength;
                arrayLength = _readable.readInt();
                if (arrayLength < 0) {
                    throw new RuntimeException("non-nullable field filterResults was serialized as null");
                } else {
                    ArrayList<DeleteAclsFilterResult> newCollection = new ArrayList<>(arrayLength);
                    for (int i = 0; i < arrayLength; i++) {
                        newCollection.add(new DeleteAclsFilterResult(_readable, _version));
                    }
                    this.filterResults = newCollection;
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
        _writable.writeInt(throttleTimeMs);
        if (_version >= 2) {
            _writable.writeUnsignedVarint(filterResults.size() + 1);
            for (DeleteAclsFilterResult filterResultsElement : filterResults) {
                filterResultsElement.write(_writable, _cache, _version);
            }
        } else {
            _writable.writeInt(filterResults.size());
            for (DeleteAclsFilterResult filterResultsElement : filterResults) {
                filterResultsElement.write(_writable, _cache, _version);
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
        _size.addBytes(4);
        {
            if (_version >= 2) {
                _size.addBytes(ByteUtils.sizeOfUnsignedVarint(filterResults.size() + 1));
            } else {
                _size.addBytes(4);
            }
            for (DeleteAclsFilterResult filterResultsElement : filterResults) {
                filterResultsElement.addSize(_size, _cache, _version);
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
        if (!(obj instanceof DeleteAclsResponseData)) return false;
        DeleteAclsResponseData other = (DeleteAclsResponseData) obj;
        if (throttleTimeMs != other.throttleTimeMs) return false;
        if (this.filterResults == null) {
            if (other.filterResults != null) return false;
        } else {
            if (!this.filterResults.equals(other.filterResults)) return false;
        }
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + throttleTimeMs;
        hashCode = 31 * hashCode + (filterResults == null ? 0 : filterResults.hashCode());
        return hashCode;
    }
    
    @Override
    public DeleteAclsResponseData duplicate() {
        DeleteAclsResponseData _duplicate = new DeleteAclsResponseData();
        _duplicate.throttleTimeMs = throttleTimeMs;
        ArrayList<DeleteAclsFilterResult> newFilterResults = new ArrayList<DeleteAclsFilterResult>(filterResults.size());
        for (DeleteAclsFilterResult _element : filterResults) {
            newFilterResults.add(_element.duplicate());
        }
        _duplicate.filterResults = newFilterResults;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "DeleteAclsResponseData("
            + "throttleTimeMs=" + throttleTimeMs
            + ", filterResults=" + MessageUtil.deepToString(filterResults.iterator())
            + ")";
    }
    
    public int throttleTimeMs() {
        return this.throttleTimeMs;
    }
    
    public List<DeleteAclsFilterResult> filterResults() {
        return this.filterResults;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public DeleteAclsResponseData setThrottleTimeMs(int v) {
        this.throttleTimeMs = v;
        return this;
    }
    
    public DeleteAclsResponseData setFilterResults(List<DeleteAclsFilterResult> v) {
        this.filterResults = v;
        return this;
    }
    
    public static class DeleteAclsFilterResult implements Message {
        short errorCode;
        String errorMessage;
        List<DeleteAclsMatchingAcl> matchingAcls;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, "The error code, or 0 if the filter succeeded."),
                new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if the filter succeeded."),
                new Field("matching_acls", new ArrayOf(DeleteAclsMatchingAcl.SCHEMA_0), "The ACLs which matched this filter.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("error_code", Type.INT16, "The error code, or 0 if the filter succeeded."),
                new Field("error_message", Type.NULLABLE_STRING, "The error message, or null if the filter succeeded."),
                new Field("matching_acls", new ArrayOf(DeleteAclsMatchingAcl.SCHEMA_1), "The ACLs which matched this filter.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("error_code", Type.INT16, "The error code, or 0 if the filter succeeded."),
                new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The error message, or null if the filter succeeded."),
                new Field("matching_acls", new CompactArrayOf(DeleteAclsMatchingAcl.SCHEMA_2), "The ACLs which matched this filter."),
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
        
        public DeleteAclsFilterResult(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DeleteAclsFilterResult() {
            this.errorCode = (short) 0;
            this.errorMessage = "";
            this.matchingAcls = new ArrayList<DeleteAclsMatchingAcl>(0);
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DeleteAclsFilterResult");
            }
            this.errorCode = _readable.readShort();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.errorMessage = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field errorMessage had invalid length " + length);
                } else {
                    this.errorMessage = _readable.readString(length);
                }
            }
            {
                if (_version >= 2) {
                    int arrayLength;
                    arrayLength = _readable.readUnsignedVarint() - 1;
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field matchingAcls was serialized as null");
                    } else {
                        ArrayList<DeleteAclsMatchingAcl> newCollection = new ArrayList<>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new DeleteAclsMatchingAcl(_readable, _version));
                        }
                        this.matchingAcls = newCollection;
                    }
                } else {
                    int arrayLength;
                    arrayLength = _readable.readInt();
                    if (arrayLength < 0) {
                        throw new RuntimeException("non-nullable field matchingAcls was serialized as null");
                    } else {
                        ArrayList<DeleteAclsMatchingAcl> newCollection = new ArrayList<>(arrayLength);
                        for (int i = 0; i < arrayLength; i++) {
                            newCollection.add(new DeleteAclsMatchingAcl(_readable, _version));
                        }
                        this.matchingAcls = newCollection;
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
            _writable.writeShort(errorCode);
            if (errorMessage == null) {
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 2) {
                _writable.writeUnsignedVarint(matchingAcls.size() + 1);
                for (DeleteAclsMatchingAcl matchingAclsElement : matchingAcls) {
                    matchingAclsElement.write(_writable, _cache, _version);
                }
            } else {
                _writable.writeInt(matchingAcls.size());
                for (DeleteAclsMatchingAcl matchingAclsElement : matchingAcls) {
                    matchingAclsElement.write(_writable, _cache, _version);
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of DeleteAclsFilterResult");
            }
            _size.addBytes(2);
            if (errorMessage == null) {
                if (_version >= 2) {
                    _size.addBytes(1);
                } else {
                    _size.addBytes(2);
                }
            } else {
                byte[] _stringBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'errorMessage' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(errorMessage, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                if (_version >= 2) {
                    _size.addBytes(ByteUtils.sizeOfUnsignedVarint(matchingAcls.size() + 1));
                } else {
                    _size.addBytes(4);
                }
                for (DeleteAclsMatchingAcl matchingAclsElement : matchingAcls) {
                    matchingAclsElement.addSize(_size, _cache, _version);
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
            if (!(obj instanceof DeleteAclsFilterResult)) return false;
            DeleteAclsFilterResult other = (DeleteAclsFilterResult) obj;
            if (errorCode != other.errorCode) return false;
            if (this.errorMessage == null) {
                if (other.errorMessage != null) return false;
            } else {
                if (!this.errorMessage.equals(other.errorMessage)) return false;
            }
            if (this.matchingAcls == null) {
                if (other.matchingAcls != null) return false;
            } else {
                if (!this.matchingAcls.equals(other.matchingAcls)) return false;
            }
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
            hashCode = 31 * hashCode + (matchingAcls == null ? 0 : matchingAcls.hashCode());
            return hashCode;
        }
        
        @Override
        public DeleteAclsFilterResult duplicate() {
            DeleteAclsFilterResult _duplicate = new DeleteAclsFilterResult();
            _duplicate.errorCode = errorCode;
            if (errorMessage == null) {
                _duplicate.errorMessage = null;
            } else {
                _duplicate.errorMessage = errorMessage;
            }
            ArrayList<DeleteAclsMatchingAcl> newMatchingAcls = new ArrayList<DeleteAclsMatchingAcl>(matchingAcls.size());
            for (DeleteAclsMatchingAcl _element : matchingAcls) {
                newMatchingAcls.add(_element.duplicate());
            }
            _duplicate.matchingAcls = newMatchingAcls;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "DeleteAclsFilterResult("
                + "errorCode=" + errorCode
                + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
                + ", matchingAcls=" + MessageUtil.deepToString(matchingAcls.iterator())
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String errorMessage() {
            return this.errorMessage;
        }
        
        public List<DeleteAclsMatchingAcl> matchingAcls() {
            return this.matchingAcls;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DeleteAclsFilterResult setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public DeleteAclsFilterResult setErrorMessage(String v) {
            this.errorMessage = v;
            return this;
        }
        
        public DeleteAclsFilterResult setMatchingAcls(List<DeleteAclsMatchingAcl> v) {
            this.matchingAcls = v;
            return this;
        }
    }
    
    public static class DeleteAclsMatchingAcl implements Message {
        short errorCode;
        String errorMessage;
        byte resourceType;
        String resourceName;
        byte patternType;
        String principal;
        String host;
        byte operation;
        byte permissionType;
        private List<RawTaggedField> _unknownTaggedFields;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("error_code", Type.INT16, "The deletion error code, or 0 if the deletion succeeded."),
                new Field("error_message", Type.NULLABLE_STRING, "The deletion error message, or null if the deletion succeeded."),
                new Field("resource_type", Type.INT8, "The ACL resource type."),
                new Field("resource_name", Type.STRING, "The ACL resource name."),
                new Field("principal", Type.STRING, "The ACL principal."),
                new Field("host", Type.STRING, "The ACL host."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The ACL permission type.")
            );
        
        public static final Schema SCHEMA_1 =
            new Schema(
                new Field("error_code", Type.INT16, "The deletion error code, or 0 if the deletion succeeded."),
                new Field("error_message", Type.NULLABLE_STRING, "The deletion error message, or null if the deletion succeeded."),
                new Field("resource_type", Type.INT8, "The ACL resource type."),
                new Field("resource_name", Type.STRING, "The ACL resource name."),
                new Field("pattern_type", Type.INT8, "The ACL resource pattern type."),
                new Field("principal", Type.STRING, "The ACL principal."),
                new Field("host", Type.STRING, "The ACL host."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The ACL permission type.")
            );
        
        public static final Schema SCHEMA_2 =
            new Schema(
                new Field("error_code", Type.INT16, "The deletion error code, or 0 if the deletion succeeded."),
                new Field("error_message", Type.COMPACT_NULLABLE_STRING, "The deletion error message, or null if the deletion succeeded."),
                new Field("resource_type", Type.INT8, "The ACL resource type."),
                new Field("resource_name", Type.COMPACT_STRING, "The ACL resource name."),
                new Field("pattern_type", Type.INT8, "The ACL resource pattern type."),
                new Field("principal", Type.COMPACT_STRING, "The ACL principal."),
                new Field("host", Type.COMPACT_STRING, "The ACL host."),
                new Field("operation", Type.INT8, "The ACL operation."),
                new Field("permission_type", Type.INT8, "The ACL permission type."),
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
        
        public DeleteAclsMatchingAcl(Readable _readable, short _version) {
            read(_readable, _version);
        }
        
        public DeleteAclsMatchingAcl() {
            this.errorCode = (short) 0;
            this.errorMessage = "";
            this.resourceType = (byte) 0;
            this.resourceName = "";
            this.patternType = (byte) 3;
            this.principal = "";
            this.host = "";
            this.operation = (byte) 0;
            this.permissionType = (byte) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of DeleteAclsMatchingAcl");
            }
            this.errorCode = _readable.readShort();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    this.errorMessage = null;
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field errorMessage had invalid length " + length);
                } else {
                    this.errorMessage = _readable.readString(length);
                }
            }
            this.resourceType = _readable.readByte();
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field resourceName was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field resourceName had invalid length " + length);
                } else {
                    this.resourceName = _readable.readString(length);
                }
            }
            if (_version >= 1) {
                this.patternType = _readable.readByte();
            } else {
                this.patternType = (byte) 3;
            }
            {
                int length;
                if (_version >= 2) {
                    length = _readable.readUnsignedVarint() - 1;
                } else {
                    length = _readable.readShort();
                }
                if (length < 0) {
                    throw new RuntimeException("non-nullable field principal was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field principal had invalid length " + length);
                } else {
                    this.principal = _readable.readString(length);
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
                    throw new RuntimeException("non-nullable field host was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field host had invalid length " + length);
                } else {
                    this.host = _readable.readString(length);
                }
            }
            this.operation = _readable.readByte();
            this.permissionType = _readable.readByte();
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
            if (errorMessage == null) {
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(0);
                } else {
                    _writable.writeShort((short) -1);
                }
            } else {
                byte[] _stringBytes = _cache.getSerializedValue(errorMessage);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeByte(resourceType);
            {
                byte[] _stringBytes = _cache.getSerializedValue(resourceName);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            if (_version >= 1) {
                _writable.writeByte(patternType);
            } else {
                if (this.patternType != (byte) 3) {
                    throw new UnsupportedVersionException("Attempted to write a non-default patternType at version " + _version);
                }
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(principal);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(host);
                if (_version >= 2) {
                    _writable.writeUnsignedVarint(_stringBytes.length + 1);
                } else {
                    _writable.writeShort((short) _stringBytes.length);
                }
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeByte(operation);
            _writable.writeByte(permissionType);
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
                throw new UnsupportedVersionException("Can't size version " + _version + " of DeleteAclsMatchingAcl");
            }
            _size.addBytes(2);
            if (errorMessage == null) {
                if (_version >= 2) {
                    _size.addBytes(1);
                } else {
                    _size.addBytes(2);
                }
            } else {
                byte[] _stringBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'errorMessage' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(errorMessage, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            _size.addBytes(1);
            {
                byte[] _stringBytes = resourceName.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'resourceName' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(resourceName, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            if (_version >= 1) {
                _size.addBytes(1);
            }
            {
                byte[] _stringBytes = principal.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'principal' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(principal, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            {
                byte[] _stringBytes = host.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'host' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(host, _stringBytes);
                if (_version >= 2) {
                    _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
                } else {
                    _size.addBytes(_stringBytes.length + 2);
                }
            }
            _size.addBytes(1);
            _size.addBytes(1);
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
            if (!(obj instanceof DeleteAclsMatchingAcl)) return false;
            DeleteAclsMatchingAcl other = (DeleteAclsMatchingAcl) obj;
            if (errorCode != other.errorCode) return false;
            if (this.errorMessage == null) {
                if (other.errorMessage != null) return false;
            } else {
                if (!this.errorMessage.equals(other.errorMessage)) return false;
            }
            if (resourceType != other.resourceType) return false;
            if (this.resourceName == null) {
                if (other.resourceName != null) return false;
            } else {
                if (!this.resourceName.equals(other.resourceName)) return false;
            }
            if (patternType != other.patternType) return false;
            if (this.principal == null) {
                if (other.principal != null) return false;
            } else {
                if (!this.principal.equals(other.principal)) return false;
            }
            if (this.host == null) {
                if (other.host != null) return false;
            } else {
                if (!this.host.equals(other.host)) return false;
            }
            if (operation != other.operation) return false;
            if (permissionType != other.permissionType) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + errorCode;
            hashCode = 31 * hashCode + (errorMessage == null ? 0 : errorMessage.hashCode());
            hashCode = 31 * hashCode + resourceType;
            hashCode = 31 * hashCode + (resourceName == null ? 0 : resourceName.hashCode());
            hashCode = 31 * hashCode + patternType;
            hashCode = 31 * hashCode + (principal == null ? 0 : principal.hashCode());
            hashCode = 31 * hashCode + (host == null ? 0 : host.hashCode());
            hashCode = 31 * hashCode + operation;
            hashCode = 31 * hashCode + permissionType;
            return hashCode;
        }
        
        @Override
        public DeleteAclsMatchingAcl duplicate() {
            DeleteAclsMatchingAcl _duplicate = new DeleteAclsMatchingAcl();
            _duplicate.errorCode = errorCode;
            if (errorMessage == null) {
                _duplicate.errorMessage = null;
            } else {
                _duplicate.errorMessage = errorMessage;
            }
            _duplicate.resourceType = resourceType;
            _duplicate.resourceName = resourceName;
            _duplicate.patternType = patternType;
            _duplicate.principal = principal;
            _duplicate.host = host;
            _duplicate.operation = operation;
            _duplicate.permissionType = permissionType;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "DeleteAclsMatchingAcl("
                + "errorCode=" + errorCode
                + ", errorMessage=" + ((errorMessage == null) ? "null" : "'" + errorMessage.toString() + "'")
                + ", resourceType=" + resourceType
                + ", resourceName=" + ((resourceName == null) ? "null" : "'" + resourceName.toString() + "'")
                + ", patternType=" + patternType
                + ", principal=" + ((principal == null) ? "null" : "'" + principal.toString() + "'")
                + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
                + ", operation=" + operation
                + ", permissionType=" + permissionType
                + ")";
        }
        
        public short errorCode() {
            return this.errorCode;
        }
        
        public String errorMessage() {
            return this.errorMessage;
        }
        
        public byte resourceType() {
            return this.resourceType;
        }
        
        public String resourceName() {
            return this.resourceName;
        }
        
        public byte patternType() {
            return this.patternType;
        }
        
        public String principal() {
            return this.principal;
        }
        
        public String host() {
            return this.host;
        }
        
        public byte operation() {
            return this.operation;
        }
        
        public byte permissionType() {
            return this.permissionType;
        }
        
        @Override
        public List<RawTaggedField> unknownTaggedFields() {
            if (_unknownTaggedFields == null) {
                _unknownTaggedFields = new ArrayList<>(0);
            }
            return _unknownTaggedFields;
        }
        
        public DeleteAclsMatchingAcl setErrorCode(short v) {
            this.errorCode = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setErrorMessage(String v) {
            this.errorMessage = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setResourceType(byte v) {
            this.resourceType = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setResourceName(String v) {
            this.resourceName = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setPatternType(byte v) {
            this.patternType = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setPrincipal(String v) {
            this.principal = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setHost(String v) {
            this.host = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setOperation(byte v) {
            this.operation = v;
            return this;
        }
        
        public DeleteAclsMatchingAcl setPermissionType(byte v) {
            this.permissionType = v;
            return this;
        }
    }
}
