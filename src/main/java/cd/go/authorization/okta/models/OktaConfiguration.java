/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.authorization.okta.models;

import cd.go.authorization.okta.OktaApiClient;
import cd.go.authorization.okta.annotation.ProfileField;
import cd.go.authorization.okta.annotation.Validatable;
import cd.go.authorization.okta.utils.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import static cd.go.authorization.okta.utils.Util.GSON;

public class OktaConfiguration implements Validatable {
    @Expose
    @SerializedName("OktaEndpoint")
    @ProfileField(key = "OktaEndpoint", required = true, secure = false)
    private String oktaEndpoint;

    @Expose
    @SerializedName("ClientId")
    @ProfileField(key = "ClientId", required = true, secure = true)
    private String clientId;

    @Expose
    @SerializedName("ClientSecret")
    @ProfileField(key = "ClientSecret", required = true, secure = true)
    private String clientSecret;

    private OktaApiClient oktaApiClient;

    public OktaConfiguration() {
    }

    public OktaConfiguration(String oktaEndpoint, String clientId, String clientSecret) {
        this.oktaEndpoint = oktaEndpoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String oktaEndpoint() {
        return oktaEndpoint;
    }

    public String clientId() {
        return clientId;
    }

    public String clientSecret() {
        return clientSecret;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public static OktaConfiguration fromJSON(String json) {
        return GSON.fromJson(json, OktaConfiguration.class);
    }

    public Map<String, String> toProperties() {
        return GSON.fromJson(toJSON(), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public OktaApiClient oktaApiClient() {
        if (oktaApiClient == null) {
            oktaApiClient = new OktaApiClient(this);
        }

        return oktaApiClient;
    }
}
