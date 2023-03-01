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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;

public class MetadataJsonConverters {
    public static JsonNode writeJson(ApiMessage apiMessage, short apiVersion) {
        switch (apiMessage.apiKey()) {
            case 0:
                return RemoteLogSegmentMetadataRecordJsonConverter.write((RemoteLogSegmentMetadataRecord) apiMessage, apiVersion);
            case 1:
                return RemoteLogSegmentMetadataUpdateRecordJsonConverter.write((RemoteLogSegmentMetadataUpdateRecord) apiMessage, apiVersion);
            case 2:
                return RemotePartitionDeleteMetadataRecordJsonConverter.write((RemotePartitionDeleteMetadataRecord) apiMessage, apiVersion);
            default:
                throw new UnsupportedVersionException("Unknown metadata id " + apiMessage.apiKey());
        }
    }
    
    public static ApiMessage readJson(JsonNode json, short apiKey, short apiVersion) {
        switch (apiKey) {
            case 0:
                return RemoteLogSegmentMetadataRecordJsonConverter.read(json, apiVersion);
            case 1:
                return RemoteLogSegmentMetadataUpdateRecordJsonConverter.read(json, apiVersion);
            case 2:
                return RemotePartitionDeleteMetadataRecordJsonConverter.read(json, apiVersion);
            default:
                throw new UnsupportedVersionException("Unknown metadata id " + apiKey);
        }
    }
    
}
