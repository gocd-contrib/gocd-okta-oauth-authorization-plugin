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

import cd.go.authorization.okta.annotation.MetadataValidator;
import cd.go.authorization.okta.annotation.ValidationResult;
import cd.go.authorization.okta.executors.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class RoleConfigValidateRequestExecutor implements RequestExecutor {
    private final RoleConfigValidateRequest request;

    public RoleConfigValidateRequestExecutor(RoleConfigValidateRequest request) {
        this.request = request;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        final ValidationResult validationResult = new MetadataValidator().validate(request.oktaRoleConfiguration());

        if (!request.oktaRoleConfiguration().hasConfiguration()) {
            validationResult.addError("Groups", "At least one of the fields(groups or users) should be specified.");
            validationResult.addError("Users", "At least one of the fields(groups or users) should be specified.");
        }

        return DefaultGoPluginApiResponse.success(validationResult.toJSON());
    }
}
