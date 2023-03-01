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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.errors.UnsupportedVersionException;
import org.apache.kafka.common.protocol.ApiMessage;

public class MetadataJsonConverters {
    public static JsonNode writeJson(ApiMessage apiMessage, short apiVersion) {
        switch (apiMessage.apiKey()) {
            case 0:
                return RegisterBrokerRecordJsonConverter.write((RegisterBrokerRecord) apiMessage, apiVersion);
            case 1:
                return UnregisterBrokerRecordJsonConverter.write((UnregisterBrokerRecord) apiMessage, apiVersion);
            case 2:
                return TopicRecordJsonConverter.write((TopicRecord) apiMessage, apiVersion);
            case 3:
                return PartitionRecordJsonConverter.write((PartitionRecord) apiMessage, apiVersion);
            case 4:
                return ConfigRecordJsonConverter.write((ConfigRecord) apiMessage, apiVersion);
            case 5:
                return PartitionChangeRecordJsonConverter.write((PartitionChangeRecord) apiMessage, apiVersion);
            case 6:
                return AccessControlRecordJsonConverter.write((AccessControlRecord) apiMessage, apiVersion);
            case 7:
                return FenceBrokerRecordJsonConverter.write((FenceBrokerRecord) apiMessage, apiVersion);
            case 8:
                return UnfenceBrokerRecordJsonConverter.write((UnfenceBrokerRecord) apiMessage, apiVersion);
            case 9:
                return RemoveTopicRecordJsonConverter.write((RemoveTopicRecord) apiMessage, apiVersion);
            case 10:
                return DelegationTokenRecordJsonConverter.write((DelegationTokenRecord) apiMessage, apiVersion);
            case 11:
                return UserScramCredentialRecordJsonConverter.write((UserScramCredentialRecord) apiMessage, apiVersion);
            case 12:
                return FeatureLevelRecordJsonConverter.write((FeatureLevelRecord) apiMessage, apiVersion);
            case 14:
                return ClientQuotaRecordJsonConverter.write((ClientQuotaRecord) apiMessage, apiVersion);
            case 15:
                return ProducerIdsRecordJsonConverter.write((ProducerIdsRecord) apiMessage, apiVersion);
            case 16:
                return RemoveFeatureLevelRecordJsonConverter.write((RemoveFeatureLevelRecord) apiMessage, apiVersion);
            case 17:
                return BrokerRegistrationChangeRecordJsonConverter.write((BrokerRegistrationChangeRecord) apiMessage, apiVersion);
            default:
                throw new UnsupportedVersionException("Unknown metadata id " + apiMessage.apiKey());
        }
    }
    
    public static ApiMessage readJson(JsonNode json, short apiKey, short apiVersion) {
        switch (apiKey) {
            case 0:
                return RegisterBrokerRecordJsonConverter.read(json, apiVersion);
            case 1:
                return UnregisterBrokerRecordJsonConverter.read(json, apiVersion);
            case 2:
                return TopicRecordJsonConverter.read(json, apiVersion);
            case 3:
                return PartitionRecordJsonConverter.read(json, apiVersion);
            case 4:
                return ConfigRecordJsonConverter.read(json, apiVersion);
            case 5:
                return PartitionChangeRecordJsonConverter.read(json, apiVersion);
            case 6:
                return AccessControlRecordJsonConverter.read(json, apiVersion);
            case 7:
                return FenceBrokerRecordJsonConverter.read(json, apiVersion);
            case 8:
                return UnfenceBrokerRecordJsonConverter.read(json, apiVersion);
            case 9:
                return RemoveTopicRecordJsonConverter.read(json, apiVersion);
            case 10:
                return DelegationTokenRecordJsonConverter.read(json, apiVersion);
            case 11:
                return UserScramCredentialRecordJsonConverter.read(json, apiVersion);
            case 12:
                return FeatureLevelRecordJsonConverter.read(json, apiVersion);
            case 14:
                return ClientQuotaRecordJsonConverter.read(json, apiVersion);
            case 15:
                return ProducerIdsRecordJsonConverter.read(json, apiVersion);
            case 16:
                return RemoveFeatureLevelRecordJsonConverter.read(json, apiVersion);
            case 17:
                return BrokerRegistrationChangeRecordJsonConverter.read(json, apiVersion);
            default:
                throw new UnsupportedVersionException("Unknown metadata id " + apiKey);
        }
    }
    
}
