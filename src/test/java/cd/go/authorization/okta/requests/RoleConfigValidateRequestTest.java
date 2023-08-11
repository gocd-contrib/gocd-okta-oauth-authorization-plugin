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

import cd.go.authorization.okta.models.OktaRoleConfiguration;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RoleConfigValidateRequestTest {

    @Mock
    private GoPluginApiRequest apiRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldDeserializeGoPluginApiRequestToRoleConfigValidateRequest() throws Exception {
        String responseBody = "{\n" +
                "  \"Groups\": \"group-1,group-2\",\n" +
                "  \"Users\": \"bob,alice\"\n" +
                "}";

        when(apiRequest.requestBody()).thenReturn(responseBody);

        final RoleConfigValidateRequest request = RoleConfigValidateRequest.from(apiRequest);
        final OktaRoleConfiguration oktaRoleConfiguration = request.oktaRoleConfiguration();

        assertThat(oktaRoleConfiguration.groups(), contains("group-1", "group-2"));
        assertThat(oktaRoleConfiguration.users(), contains("bob", "alice"));
    }
}
