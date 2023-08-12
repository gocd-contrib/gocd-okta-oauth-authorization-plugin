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

import cd.go.authorization.okta.models.OktaConfiguration;
import cd.go.authorization.okta.models.TokenInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class OktaApiClientTest {

    @Mock
    private OktaConfiguration oktaConfiguration;
    private MockWebServer server;
    private OktaApiClient oktaApiClient;



    @BeforeEach
    public void setUp() throws Exception {
        openMocks(this);

        server = new MockWebServer();
        server.start();

        when(oktaConfiguration.oktaEndpoint()).thenReturn("https://example.com");
        when(oktaConfiguration.clientId()).thenReturn("client-id");
        when(oktaConfiguration.clientSecret()).thenReturn("client-secret");

        CallbackURL.instance().updateRedirectURL("callback-url");

        oktaApiClient = new OktaApiClient(oktaConfiguration);
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void shouldReturnAuthorizationServerUrl() throws Exception {
        final String authorizationServerUrl = oktaApiClient.authorizationServerUrl("call-back-url");

        assertThat(authorizationServerUrl, startsWith("https://example.com/v1/authorize?client_id=client-id&redirect_uri=call-back-url&response_type=code&scope=openid%20profile%20email%20groups&state="));
    }

    @Test
    public void shouldFetchTokenInfoUsingAuthorizationCode() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new TokenInfo("token-444248275346-5758603453985735", 7200, "bearer", "refresh-token").toJSON()));

        when(oktaConfiguration.oktaEndpoint()).thenReturn(server.url("/").toString());

        final TokenInfo tokenInfo = oktaApiClient.fetchAccessToken(Collections.singletonMap("code", "some-code"));

        assertThat(tokenInfo.accessToken(), is("token-444248275346-5758603453985735"));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST /v1/token HTTP/1.1", request.getRequestLine());
        assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
        assertEquals("client_id=client-id&client_secret=client-secret&code=some-code&grant_type=authorization_code&redirect_uri=callback-url", request.getBody().readUtf8());
    }

    @Test
    public void shouldFetchUserProfile() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", 7200, "bearer", "refresh-token");
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new OktaUser("foo@bar.com", "Display Name").toJSON()));

        when(oktaConfiguration.oktaEndpoint()).thenReturn(server.url("/").toString());

        final OktaUser oktaUser = oktaApiClient.userProfile(tokenInfo);

        assertThat(oktaUser.getEmail(), is("foo@bar.com"));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST /v1/userinfo HTTP/1.1", request.getRequestLine());
        assertEquals("Bearer token-444248275346-5758603453985735", request.getHeader("Authorization"));
    }

    @Test
    public void shouldErrorOutWhenAPIRequestFails() throws Exception {
        final TokenInfo tokenInfo = new TokenInfo("token-444248275346-5758603453985735", 7200, "bearer", "refresh-token");

        server.enqueue(new MockResponse().setResponseCode(403).setBody("Unauthorized"));

        when(oktaConfiguration.oktaEndpoint()).thenReturn(server.url("/").toString());

        Throwable thrown = assertThrows(RuntimeException.class, () -> oktaApiClient.userProfile(tokenInfo));
        assertThat(thrown.getMessage(), is("Api call to `/v1/userinfo` failed with error: `Unauthorized`"));
    }
}
