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

import cd.go.authorization.okta.executors.FetchAccessTokenRequestExecutor;
import cd.go.authorization.okta.models.AuthConfig;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FetchAccessTokenRequestTest {
    @Mock
    private GoPluginApiRequest apiRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldDeserializeGoPluginApiRequestToFetchAccessTokenRequest() throws Exception {
        String responseBody = "{\n" +
                "  \"auth_configs\": [\n" +
                "    {\n" +
                "      \"id\": \"google-auth-config\",\n" +
                "      \"configuration\": {\n" +
                "        \"OktaEndpoint\": \"https://example.co.in\",\n" +
                "        \"ClientId\": \"client-id\",\n" +
                "        \"ClientSecret\": \"client-secret\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        when(apiRequest.requestBody()).thenReturn(responseBody);

        final FetchAccessTokenRequest request = FetchAccessTokenRequest.from(apiRequest);

        assertThat(request.authConfigs(), hasSize(1));
        assertThat(request.executor(), instanceOf(FetchAccessTokenRequestExecutor.class));

        final AuthConfig authConfig = request.authConfigs().get(0);
        assertThat(authConfig.getId(), is("google-auth-config"));
        assertThat(authConfig.getConfiguration().oktaEndpoint(), is("https://example.co.in"));
        assertThat(authConfig.getConfiguration().clientId(), is("client-id"));
        assertThat(authConfig.getConfiguration().clientSecret(), is("client-secret"));
    }
}
