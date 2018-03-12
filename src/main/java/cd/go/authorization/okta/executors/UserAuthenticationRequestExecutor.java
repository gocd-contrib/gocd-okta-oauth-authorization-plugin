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

package cd.go.authorization.okta.executors;

import cd.go.authorization.okta.OktaApiClient;
import cd.go.authorization.okta.OktaAuthorizer;
import cd.go.authorization.okta.OktaUser;
import cd.go.authorization.okta.exceptions.NoAuthorizationConfigurationException;
import cd.go.authorization.okta.models.AuthConfig;
import cd.go.authorization.okta.models.OktaConfiguration;
import cd.go.authorization.okta.models.User;
import cd.go.authorization.okta.requests.UserAuthenticationRequest;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static cd.go.authorization.okta.OktaPlugin.LOG;
import static java.text.MessageFormat.format;

public class UserAuthenticationRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new Gson();
    private final UserAuthenticationRequest request;
    private final OktaAuthorizer oktaAuthorizer;

    public UserAuthenticationRequestExecutor(UserAuthenticationRequest request) {
        this(request, new OktaAuthorizer());
    }

    UserAuthenticationRequestExecutor(UserAuthenticationRequest request, OktaAuthorizer oktaAuthorizer) {
        this.request = request;
        this.oktaAuthorizer = oktaAuthorizer;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        if (request.authConfigs() == null || request.authConfigs().isEmpty()) {
            throw new NoAuthorizationConfigurationException("[Authenticate] No authorization configuration found.");
        }

        final AuthConfig authConfig = request.authConfigs().get(0);
        final OktaConfiguration configuration = request.authConfigs().get(0).getConfiguration();
        final OktaApiClient oktaApiClient = configuration.oktaApiClient();
        final OktaUser oktaUser = oktaApiClient.userProfile(request.tokenInfo());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user", new User(oktaUser));
        userMap.put("roles", oktaAuthorizer.authorize(oktaUser, authConfig, request.roles()));

        return DefaultGoPluginApiResponse.success(GSON.toJson(userMap));
    }
}
