/*
 * Copyright 2017 Svetlin Zamfirov
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

package cd.go.authorization.okta;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static cd.go.authorization.okta.utils.Util.GSON;

public class OktaUser {
    @Expose
    @SerializedName("email")
    private String email;

    @Expose
    @SerializedName("email_verified")
    private boolean verifiedEmail;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("given_name")
    private String givenName;

    @Expose
    @SerializedName("family_name")
    private String familyName;

    @Expose
    @SerializedName("locale")
    private String locale;

    @Expose
    @SerializedName("preferred_username")
    private String preferredUsername;

    @Expose
    @SerializedName("sub")
    private String sub;

    @Expose
    @SerializedName("updated_at")
    private int updatedAt;

    @Expose
    @SerializedName("zoneinfo")
    private String zoneInfo;

    @Expose
    @SerializedName("groups")
    private List<String> groups;

    OktaUser() {
    }

    public OktaUser(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getLocale() {
        return locale;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public String getSub() {
        return sub;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public String getZoneInfo() {
        return zoneInfo;
    }

    public List<String> groups() {
        return groups;
    }

    public String toJSON() {
        return GSON.toJson(this);
    }

    public static OktaUser fromJSON(String json) {
        return GSON.fromJson(json, OktaUser.class);
    }
}
