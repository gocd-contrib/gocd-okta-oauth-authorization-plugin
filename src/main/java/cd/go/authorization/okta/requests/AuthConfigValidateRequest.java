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

package cd.go.authorization.okta.requests;

import cd.go.authorization.okta.executors.AuthConfigValidateRequestExecutor;
import cd.go.authorization.okta.models.OktaConfiguration;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;

public class AuthConfigValidateRequest extends Request {
    private final OktaConfiguration oktaConfiguration;

    public AuthConfigValidateRequest(OktaConfiguration oktaConfiguration) {
        this.oktaConfiguration = oktaConfiguration;
    }

    public static final AuthConfigValidateRequest from(GoPluginApiRequest apiRequest) {
        return new AuthConfigValidateRequest(OktaConfiguration.fromJSON(apiRequest.requestBody()));
    }

    public OktaConfiguration oktaConfiguration() {
        return oktaConfiguration;
    }

    @Override
    public AuthConfigValidateRequestExecutor executor() {
        return new AuthConfigValidateRequestExecutor(this);
    }
}
