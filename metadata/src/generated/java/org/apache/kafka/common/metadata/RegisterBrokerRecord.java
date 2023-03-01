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
import java.util.Iterator;
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


public class RegisterBrokerRecord implements ApiMessage {
    int brokerId;
    Uuid incarnationId;
    long brokerEpoch;
    BrokerEndpointCollection endPoints;
    BrokerFeatureCollection features;
    String rack;
    boolean fenced;
    private List<RawTaggedField> _unknownTaggedFields;
    
    public static final Schema SCHEMA_0 =
        new Schema(
            new Field("broker_id", Type.INT32, "The broker id."),
            new Field("incarnation_id", Type.UUID, "The incarnation ID of the broker process"),
            new Field("broker_epoch", Type.INT64, "The broker epoch assigned by the controller."),
            new Field("end_points", new CompactArrayOf(BrokerEndpoint.SCHEMA_0), "The endpoints that can be used to communicate with this broker."),
            new Field("features", new CompactArrayOf(BrokerFeature.SCHEMA_0), "The features on this broker"),
            new Field("rack", Type.COMPACT_NULLABLE_STRING, "The broker rack."),
            new Field("fenced", Type.BOOLEAN, "True if the broker is fenced."),
            TaggedFieldsSection.of(
            )
        );
    
    public static final Schema[] SCHEMAS = new Schema[] {
        SCHEMA_0
    };
    
    public static final short LOWEST_SUPPORTED_VERSION = 0;
    public static final short HIGHEST_SUPPORTED_VERSION = 0;
    
    public RegisterBrokerRecord(Readable _readable, short _version) {
        read(_readable, _version);
    }
    
    public RegisterBrokerRecord() {
        this.brokerId = 0;
        this.incarnationId = Uuid.ZERO_UUID;
        this.brokerEpoch = 0L;
        this.endPoints = new BrokerEndpointCollection(0);
        this.features = new BrokerFeatureCollection(0);
        this.rack = "";
        this.fenced = true;
    }
    
    @Override
    public short apiKey() {
        return 0;
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
        this.brokerId = _readable.readInt();
        this.incarnationId = _readable.readUuid();
        this.brokerEpoch = _readable.readLong();
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field endPoints was serialized as null");
            } else {
                BrokerEndpointCollection newCollection = new BrokerEndpointCollection(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new BrokerEndpoint(_readable, _version));
                }
                this.endPoints = newCollection;
            }
        }
        {
            int arrayLength;
            arrayLength = _readable.readUnsignedVarint() - 1;
            if (arrayLength < 0) {
                throw new RuntimeException("non-nullable field features was serialized as null");
            } else {
                BrokerFeatureCollection newCollection = new BrokerFeatureCollection(arrayLength);
                for (int i = 0; i < arrayLength; i++) {
                    newCollection.add(new BrokerFeature(_readable, _version));
                }
                this.features = newCollection;
            }
        }
        {
            int length;
            length = _readable.readUnsignedVarint() - 1;
            if (length < 0) {
                this.rack = null;
            } else if (length > 0x7fff) {
                throw new RuntimeException("string field rack had invalid length " + length);
            } else {
                this.rack = _readable.readString(length);
            }
        }
        this.fenced = _readable.readByte() != 0;
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
        _writable.writeInt(brokerId);
        _writable.writeUuid(incarnationId);
        _writable.writeLong(brokerEpoch);
        _writable.writeUnsignedVarint(endPoints.size() + 1);
        for (BrokerEndpoint endPointsElement : endPoints) {
            endPointsElement.write(_writable, _cache, _version);
        }
        _writable.writeUnsignedVarint(features.size() + 1);
        for (BrokerFeature featuresElement : features) {
            featuresElement.write(_writable, _cache, _version);
        }
        if (rack == null) {
            _writable.writeUnsignedVarint(0);
        } else {
            byte[] _stringBytes = _cache.getSerializedValue(rack);
            _writable.writeUnsignedVarint(_stringBytes.length + 1);
            _writable.writeByteArray(_stringBytes);
        }
        _writable.writeByte(fenced ? (byte) 1 : (byte) 0);
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
        _size.addBytes(8);
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(endPoints.size() + 1));
            for (BrokerEndpoint endPointsElement : endPoints) {
                endPointsElement.addSize(_size, _cache, _version);
            }
        }
        {
            _size.addBytes(ByteUtils.sizeOfUnsignedVarint(features.size() + 1));
            for (BrokerFeature featuresElement : features) {
                featuresElement.addSize(_size, _cache, _version);
            }
        }
        if (rack == null) {
            _size.addBytes(1);
        } else {
            byte[] _stringBytes = rack.getBytes(StandardCharsets.UTF_8);
            if (_stringBytes.length > 0x7fff) {
                throw new RuntimeException("'rack' field is too long to be serialized");
            }
            _cache.cacheSerializedValue(rack, _stringBytes);
            _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
        }
        _size.addBytes(1);
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
        if (!(obj instanceof RegisterBrokerRecord)) return false;
        RegisterBrokerRecord other = (RegisterBrokerRecord) obj;
        if (brokerId != other.brokerId) return false;
        if (!this.incarnationId.equals(other.incarnationId)) return false;
        if (brokerEpoch != other.brokerEpoch) return false;
        if (this.endPoints == null) {
            if (other.endPoints != null) return false;
        } else {
            if (!this.endPoints.equals(other.endPoints)) return false;
        }
        if (this.features == null) {
            if (other.features != null) return false;
        } else {
            if (!this.features.equals(other.features)) return false;
        }
        if (this.rack == null) {
            if (other.rack != null) return false;
        } else {
            if (!this.rack.equals(other.rack)) return false;
        }
        if (fenced != other.fenced) return false;
        return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode = 31 * hashCode + brokerId;
        hashCode = 31 * hashCode + incarnationId.hashCode();
        hashCode = 31 * hashCode + ((int) (brokerEpoch >> 32) ^ (int) brokerEpoch);
        hashCode = 31 * hashCode + (endPoints == null ? 0 : endPoints.hashCode());
        hashCode = 31 * hashCode + (features == null ? 0 : features.hashCode());
        hashCode = 31 * hashCode + (rack == null ? 0 : rack.hashCode());
        hashCode = 31 * hashCode + (fenced ? 1231 : 1237);
        return hashCode;
    }
    
    @Override
    public RegisterBrokerRecord duplicate() {
        RegisterBrokerRecord _duplicate = new RegisterBrokerRecord();
        _duplicate.brokerId = brokerId;
        _duplicate.incarnationId = incarnationId;
        _duplicate.brokerEpoch = brokerEpoch;
        BrokerEndpointCollection newEndPoints = new BrokerEndpointCollection(endPoints.size());
        for (BrokerEndpoint _element : endPoints) {
            newEndPoints.add(_element.duplicate());
        }
        _duplicate.endPoints = newEndPoints;
        BrokerFeatureCollection newFeatures = new BrokerFeatureCollection(features.size());
        for (BrokerFeature _element : features) {
            newFeatures.add(_element.duplicate());
        }
        _duplicate.features = newFeatures;
        if (rack == null) {
            _duplicate.rack = null;
        } else {
            _duplicate.rack = rack;
        }
        _duplicate.fenced = fenced;
        return _duplicate;
    }
    
    @Override
    public String toString() {
        return "RegisterBrokerRecord("
            + "brokerId=" + brokerId
            + ", incarnationId=" + incarnationId.toString()
            + ", brokerEpoch=" + brokerEpoch
            + ", endPoints=" + MessageUtil.deepToString(endPoints.iterator())
            + ", features=" + MessageUtil.deepToString(features.iterator())
            + ", rack=" + ((rack == null) ? "null" : "'" + rack.toString() + "'")
            + ", fenced=" + (fenced ? "true" : "false")
            + ")";
    }
    
    public int brokerId() {
        return this.brokerId;
    }
    
    public Uuid incarnationId() {
        return this.incarnationId;
    }
    
    public long brokerEpoch() {
        return this.brokerEpoch;
    }
    
    public BrokerEndpointCollection endPoints() {
        return this.endPoints;
    }
    
    public BrokerFeatureCollection features() {
        return this.features;
    }
    
    public String rack() {
        return this.rack;
    }
    
    public boolean fenced() {
        return this.fenced;
    }
    
    @Override
    public List<RawTaggedField> unknownTaggedFields() {
        if (_unknownTaggedFields == null) {
            _unknownTaggedFields = new ArrayList<>(0);
        }
        return _unknownTaggedFields;
    }
    
    public RegisterBrokerRecord setBrokerId(int v) {
        this.brokerId = v;
        return this;
    }
    
    public RegisterBrokerRecord setIncarnationId(Uuid v) {
        this.incarnationId = v;
        return this;
    }
    
    public RegisterBrokerRecord setBrokerEpoch(long v) {
        this.brokerEpoch = v;
        return this;
    }
    
    public RegisterBrokerRecord setEndPoints(BrokerEndpointCollection v) {
        this.endPoints = v;
        return this;
    }
    
    public RegisterBrokerRecord setFeatures(BrokerFeatureCollection v) {
        this.features = v;
        return this;
    }
    
    public RegisterBrokerRecord setRack(String v) {
        this.rack = v;
        return this;
    }
    
    public RegisterBrokerRecord setFenced(boolean v) {
        this.fenced = v;
        return this;
    }
    
    public static class BrokerEndpoint implements Message, ImplicitLinkedHashMultiCollection.Element {
        String name;
        String host;
        int port;
        short securityProtocol;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The name of the endpoint."),
                new Field("host", Type.COMPACT_STRING, "The hostname."),
                new Field("port", Type.UINT16, "The port."),
                new Field("security_protocol", Type.INT16, "The security protocol."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public BrokerEndpoint(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public BrokerEndpoint() {
            this.name = "";
            this.host = "";
            this.port = 0;
            this.securityProtocol = (short) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of BrokerEndpoint");
            }
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
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
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    throw new RuntimeException("non-nullable field host was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field host had invalid length " + length);
                } else {
                    this.host = _readable.readString(length);
                }
            }
            this.port = _readable.readUnsignedShort();
            this.securityProtocol = _readable.readShort();
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
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            {
                byte[] _stringBytes = _cache.getSerializedValue(host);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeUnsignedShort(port);
            _writable.writeShort(securityProtocol);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of BrokerEndpoint");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            {
                byte[] _stringBytes = host.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'host' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(host, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            _size.addBytes(2);
            _size.addBytes(2);
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
            if (!(obj instanceof BrokerEndpoint)) return false;
            BrokerEndpoint other = (BrokerEndpoint) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BrokerEndpoint)) return false;
            BrokerEndpoint other = (BrokerEndpoint) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (this.host == null) {
                if (other.host != null) return false;
            } else {
                if (!this.host.equals(other.host)) return false;
            }
            if (port != other.port) return false;
            if (securityProtocol != other.securityProtocol) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public BrokerEndpoint duplicate() {
            BrokerEndpoint _duplicate = new BrokerEndpoint();
            _duplicate.name = name;
            _duplicate.host = host;
            _duplicate.port = port;
            _duplicate.securityProtocol = securityProtocol;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "BrokerEndpoint("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", host=" + ((host == null) ? "null" : "'" + host.toString() + "'")
                + ", port=" + port
                + ", securityProtocol=" + securityProtocol
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public String host() {
            return this.host;
        }
        
        public int port() {
            return this.port;
        }
        
        public short securityProtocol() {
            return this.securityProtocol;
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
        
        public BrokerEndpoint setName(String v) {
            this.name = v;
            return this;
        }
        
        public BrokerEndpoint setHost(String v) {
            this.host = v;
            return this;
        }
        
        public BrokerEndpoint setPort(int v) {
            if (v < 0 || v > 65535) {
                throw new RuntimeException("Invalid value " + v + " for unsigned short field.");
            }
            this.port = v;
            return this;
        }
        
        public BrokerEndpoint setSecurityProtocol(short v) {
            this.securityProtocol = v;
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
    
    public static class BrokerEndpointCollection extends ImplicitLinkedHashMultiCollection<BrokerEndpoint> {
        public BrokerEndpointCollection() {
            super();
        }
        
        public BrokerEndpointCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public BrokerEndpointCollection(Iterator<BrokerEndpoint> iterator) {
            super(iterator);
        }
        
        public BrokerEndpoint find(String name) {
            BrokerEndpoint _key = new BrokerEndpoint();
            _key.setName(name);
            return find(_key);
        }
        
        public List<BrokerEndpoint> findAll(String name) {
            BrokerEndpoint _key = new BrokerEndpoint();
            _key.setName(name);
            return findAll(_key);
        }
        
        public BrokerEndpointCollection duplicate() {
            BrokerEndpointCollection _duplicate = new BrokerEndpointCollection(size());
            for (BrokerEndpoint _element : this) {
                _duplicate.add(_element.duplicate());
            }
            return _duplicate;
        }
    }
    
    public static class BrokerFeature implements Message, ImplicitLinkedHashMultiCollection.Element {
        String name;
        short minSupportedVersion;
        short maxSupportedVersion;
        private List<RawTaggedField> _unknownTaggedFields;
        private int next;
        private int prev;
        
        public static final Schema SCHEMA_0 =
            new Schema(
                new Field("name", Type.COMPACT_STRING, "The feature name."),
                new Field("min_supported_version", Type.INT16, "The minimum supported feature level."),
                new Field("max_supported_version", Type.INT16, "The maximum supported feature level."),
                TaggedFieldsSection.of(
                )
            );
        
        public static final Schema[] SCHEMAS = new Schema[] {
            SCHEMA_0
        };
        
        public static final short LOWEST_SUPPORTED_VERSION = 0;
        public static final short HIGHEST_SUPPORTED_VERSION = 0;
        
        public BrokerFeature(Readable _readable, short _version) {
            read(_readable, _version);
            this.prev = ImplicitLinkedHashCollection.INVALID_INDEX;
            this.next = ImplicitLinkedHashCollection.INVALID_INDEX;
        }
        
        public BrokerFeature() {
            this.name = "";
            this.minSupportedVersion = (short) 0;
            this.maxSupportedVersion = (short) 0;
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
                throw new UnsupportedVersionException("Can't read version " + _version + " of BrokerFeature");
            }
            {
                int length;
                length = _readable.readUnsignedVarint() - 1;
                if (length < 0) {
                    throw new RuntimeException("non-nullable field name was serialized as null");
                } else if (length > 0x7fff) {
                    throw new RuntimeException("string field name had invalid length " + length);
                } else {
                    this.name = _readable.readString(length);
                }
            }
            this.minSupportedVersion = _readable.readShort();
            this.maxSupportedVersion = _readable.readShort();
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
                byte[] _stringBytes = _cache.getSerializedValue(name);
                _writable.writeUnsignedVarint(_stringBytes.length + 1);
                _writable.writeByteArray(_stringBytes);
            }
            _writable.writeShort(minSupportedVersion);
            _writable.writeShort(maxSupportedVersion);
            RawTaggedFieldWriter _rawWriter = RawTaggedFieldWriter.forFields(_unknownTaggedFields);
            _numTaggedFields += _rawWriter.numFields();
            _writable.writeUnsignedVarint(_numTaggedFields);
            _rawWriter.writeRawTags(_writable, Integer.MAX_VALUE);
        }
        
        @Override
        public void addSize(MessageSizeAccumulator _size, ObjectSerializationCache _cache, short _version) {
            int _numTaggedFields = 0;
            if (_version > 0) {
                throw new UnsupportedVersionException("Can't size version " + _version + " of BrokerFeature");
            }
            {
                byte[] _stringBytes = name.getBytes(StandardCharsets.UTF_8);
                if (_stringBytes.length > 0x7fff) {
                    throw new RuntimeException("'name' field is too long to be serialized");
                }
                _cache.cacheSerializedValue(name, _stringBytes);
                _size.addBytes(_stringBytes.length + ByteUtils.sizeOfUnsignedVarint(_stringBytes.length + 1));
            }
            _size.addBytes(2);
            _size.addBytes(2);
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
            if (!(obj instanceof BrokerFeature)) return false;
            BrokerFeature other = (BrokerFeature) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            return true;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BrokerFeature)) return false;
            BrokerFeature other = (BrokerFeature) obj;
            if (this.name == null) {
                if (other.name != null) return false;
            } else {
                if (!this.name.equals(other.name)) return false;
            }
            if (minSupportedVersion != other.minSupportedVersion) return false;
            if (maxSupportedVersion != other.maxSupportedVersion) return false;
            return MessageUtil.compareRawTaggedFields(_unknownTaggedFields, other._unknownTaggedFields);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            hashCode = 31 * hashCode + (name == null ? 0 : name.hashCode());
            return hashCode;
        }
        
        @Override
        public BrokerFeature duplicate() {
            BrokerFeature _duplicate = new BrokerFeature();
            _duplicate.name = name;
            _duplicate.minSupportedVersion = minSupportedVersion;
            _duplicate.maxSupportedVersion = maxSupportedVersion;
            return _duplicate;
        }
        
        @Override
        public String toString() {
            return "BrokerFeature("
                + "name=" + ((name == null) ? "null" : "'" + name.toString() + "'")
                + ", minSupportedVersion=" + minSupportedVersion
                + ", maxSupportedVersion=" + maxSupportedVersion
                + ")";
        }
        
        public String name() {
            return this.name;
        }
        
        public short minSupportedVersion() {
            return this.minSupportedVersion;
        }
        
        public short maxSupportedVersion() {
            return this.maxSupportedVersion;
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
        
        public BrokerFeature setName(String v) {
            this.name = v;
            return this;
        }
        
        public BrokerFeature setMinSupportedVersion(short v) {
            this.minSupportedVersion = v;
            return this;
        }
        
        public BrokerFeature setMaxSupportedVersion(short v) {
            this.maxSupportedVersion = v;
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
    
    public static class BrokerFeatureCollection extends ImplicitLinkedHashMultiCollection<BrokerFeature> {
        public BrokerFeatureCollection() {
            super();
        }
        
        public BrokerFeatureCollection(int expectedNumElements) {
            super(expectedNumElements);
        }
        
        public BrokerFeatureCollection(Iterator<BrokerFeature> iterator) {
            super(iterator);
        }
        
        public BrokerFeature find(String name) {
            BrokerFeature _key = new BrokerFeature();
            _key.setName(name);
            return find(_key);
        }
        
        public List<BrokerFeature> findAll(String name) {
            BrokerFeature _key = new BrokerFeature();
            _key.setName(name);
            return findAll(_key);
        }
        
        public BrokerFeatureCollection duplicate() {
            BrokerFeatureCollection _duplicate = new BrokerFeatureCollection(size());
            for (BrokerFeature _element : this) {
                _duplicate.add(_element.duplicate());
            }
            return _duplicate;
        }
    }
}
