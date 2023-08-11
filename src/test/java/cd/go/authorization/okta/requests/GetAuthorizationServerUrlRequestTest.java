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

import cd.go.authorization.okta.executors.GetAuthorizationServerUrlRequestExecutor;
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

public class GetAuthorizationServerUrlRequestTest {
    @Mock
    private GoPluginApiRequest apiRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldDeserializeGoPluginApiRequestToGetAuthorizationServerUrlRequest() throws Exception {
        String responseBody = "{\n" +
                "  \"authorization_server_callback_url\": \"https://redirect.url\",\n" +
                "  \"auth_configs\": [\n" +
                "    {\n" +
                "      \"id\": \"google-config\",\n" +
                "      \"configuration\": {\n" +
                "        \"OktaEndpoint\": \"https://example.com\",\n" +
                "        \"ClientId\": \"client-id\",\n" +
                "        \"ClientSecret\": \"client-secret\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        when(apiRequest.requestBody()).thenReturn(responseBody);

        final GetAuthorizationServerUrlRequest request = GetAuthorizationServerUrlRequest.from(apiRequest);

        assertThat(request.authConfigs(), hasSize(1));
        assertThat(request.executor(), instanceOf(GetAuthorizationServerUrlRequestExecutor.class));

        final AuthConfig authConfig = request.authConfigs().get(0);

        assertThat(request.callbackUrl(), is("https://redirect.url"));

        assertThat(authConfig.getId(), is("google-config"));
        assertThat(authConfig.getConfiguration().oktaEndpoint(), is("https://example.com"));
        assertThat(authConfig.getConfiguration().clientId(), is("client-id"));
        assertThat(authConfig.getConfiguration().clientSecret(), is("client-secret"));

    }
}
