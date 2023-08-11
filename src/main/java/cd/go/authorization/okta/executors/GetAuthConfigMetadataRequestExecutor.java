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

package cd.go.authorization.okta.executors;

import cd.go.authorization.okta.annotation.FieldMetadata;
import cd.go.authorization.okta.annotation.MetadataHelper;
import cd.go.authorization.okta.annotation.ProfileMetadata;
import cd.go.authorization.okta.models.OktaConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.List;

public class GetAuthConfigMetadataRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public GoPluginApiResponse execute() throws Exception {
        final List<ProfileMetadata<FieldMetadata>> authConfigMetadata = MetadataHelper.getMetadata(OktaConfiguration.class);
        return DefaultGoPluginApiResponse.success(GSON.toJson(authConfigMetadata));
    }
}
