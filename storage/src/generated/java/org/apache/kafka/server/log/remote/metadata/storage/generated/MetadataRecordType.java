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

package  org.apache.kafka.server.log.remote.metadata.storage.generated;

import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;

public enum MetadataRecordType {
    REMOTE_LOG_SEGMENT_METADATA_RECORD("RemoteLogSegmentMetadataRecord", (short) 0, (short) 0, (short) 0),
    REMOTE_LOG_SEGMENT_METADATA_UPDATE_RECORD("RemoteLogSegmentMetadataUpdateRecord", (short) 1, (short) 0, (short) 0),
    REMOTE_PARTITION_DELETE_METADATA_RECORD("RemotePartitionDeleteMetadataRecord", (short) 2, (short) 0, (short) 0);
    
    private final String name;
    private final short id;
    private final short lowestSupportedVersion;
    private final short highestSupportedVersion;
    
    MetadataRecordType(String name, short id, short lowestSupportedVersion, short highestSupportedVersion) {
        this.name = name;
        this.id = id;
        this.lowestSupportedVersion = lowestSupportedVersion;
        this.highestSupportedVersion = highestSupportedVersion;
    }
    
    public static MetadataRecordType fromId(short id) {
        switch (id) {
            case 0:
                return REMOTE_LOG_SEGMENT_METADATA_RECORD;
            case 1:
                return REMOTE_LOG_SEGMENT_METADATA_UPDATE_RECORD;
            case 2:
                return REMOTE_PARTITION_DELETE_METADATA_RECORD;
            default:
                throw new UnsupportedVersionException("Unknown metadata id " + id);
        }
    }
    
    public ApiMessage newMetadataRecord() {
        switch (id) {
            case 0:
                return new RemoteLogSegmentMetadataRecord();
            case 1:
                return new RemoteLogSegmentMetadataUpdateRecord();
            case 2:
                return new RemotePartitionDeleteMetadataRecord();
            default:
                throw new UnsupportedVersionException("Unknown metadata id " + id);
        }
    }
    
    public short id() {
        return this.id;
    }
    
    public short lowestSupportedVersion() {
        return this.lowestSupportedVersion;
    }
    
    public short highestSupportedVersion() {
        return this.highestSupportedVersion;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}
