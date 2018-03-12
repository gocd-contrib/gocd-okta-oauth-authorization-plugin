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

import cd.go.authorization.okta.annotation.ProfileField;
import cd.go.authorization.okta.annotation.Validatable;
import cd.go.authorization.okta.utils.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

import static cd.go.authorization.okta.utils.Util.*;

public class OktaRoleConfiguration implements Validatable {
    @Expose
    @SerializedName("Groups")
    @ProfileField(key = "Groups", required = false, secure = false)
    private String groups;

    @Expose
    @SerializedName("Users")
    @ProfileField(key = "Users", required = false, secure = false)
    private String users;

    public OktaRoleConfiguration() {
    }

    public OktaRoleConfiguration(String groups, String users) {
        this.groups = groups;
        this.users = users;
    }

    public List<String> groups() {
        return listFromCommaSeparatedString(groups);
    }

    public List<String> users() {
        return listFromCommaSeparatedString(toLowerCase(users));
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public static OktaRoleConfiguration fromJSON(String json) {
        return GSON.fromJson(json, OktaRoleConfiguration.class);
    }

    public Map<String, String> toProperties() {
        return GSON.fromJson(toJSON(), new TypeToken<Map<String, String>>() {
        }.getType());
    }

    public boolean hasConfiguration() {
        return isNotBlank(groups) || isNotBlank(users);
    }
}
