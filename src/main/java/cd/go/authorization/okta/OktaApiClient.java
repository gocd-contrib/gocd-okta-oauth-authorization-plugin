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
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cd.go.authorization.okta.OktaPlugin.LOG;
import static cd.go.authorization.okta.utils.Util.isBlank;
import static cd.go.authorization.okta.utils.Util.isNotBlank;
import static java.text.MessageFormat.format;

public class OktaApiClient {
    private static final String API_ERROR_MSG = "Api call to `{0}` failed with error: `{1}`";
    private final OktaConfiguration oktaConfiguration;
    private final OkHttpClient httpClient;

    public OktaApiClient(OktaConfiguration oktaConfiguration) {
        this(oktaConfiguration,
                new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build()
        );
    }

    public OktaApiClient(OktaConfiguration oktaConfiguration, OkHttpClient httpClient) {
        this.oktaConfiguration = oktaConfiguration;
        this.httpClient = httpClient;
    }

    public void verifyConnection() throws Exception {
        //TODO:
    }

    public String authorizationServerUrl(String callbackUrl) throws Exception {
        LOG.debug("[OktaApiClient] Generating Okta oauth url.");

        return HttpUrl.parse(oktaConfiguration.oktaEndpoint())
                .newBuilder()
                .addPathSegments("v1")
                .addPathSegments("authorize")
                .addQueryParameter("client_id", oktaConfiguration.clientId())
                .addQueryParameter("redirect_uri", callbackUrl)
                .addQueryParameter("response_type", "code")
                .addQueryParameter("scope", "openid profile email groups")
                .addQueryParameter("state", UUID.randomUUID().toString())
                .addQueryParameter("nonce", UUID.randomUUID().toString())
                .build().toString();
    }

    public TokenInfo fetchAccessToken(Map<String, String> params) throws Exception {
        final String code = params.get("code");
        if (isBlank(code)) {
            throw new RuntimeException("[OktaApiClient] Authorization code must not be null.");
        }

        LOG.debug("[OktaApiClient] Fetching access token using authorization code.");

        final String accessTokenUrl = HttpUrl.parse(oktaConfiguration.oktaEndpoint())
                .newBuilder()
                .addPathSegments("v1")
                .addPathSegments("token")
                .build().toString();

        final FormBody formBody = new FormBody.Builder()
                .add("client_id", oktaConfiguration.clientId())
                .add("client_secret", oktaConfiguration.clientSecret())
                .add("code", code)
                .add("grant_type", "authorization_code")
                .add("redirect_uri", CallbackURL.instance().getCallbackURL()).build();

        final Request request = new Request.Builder()
                .url(accessTokenUrl)
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        return executeRequest(request, response -> TokenInfo.fromJSON(response.body().string()));
    }

    public OktaUser userProfile(TokenInfo tokenInfo) throws Exception {
        validateTokenInfo(tokenInfo);

        LOG.debug("[OktaApiClient] Fetching user profile using access token.");

        final String userProfileUrl = HttpUrl.parse(oktaConfiguration.oktaEndpoint())
                .newBuilder()
                .addPathSegments("v1")
                .addPathSegments("userinfo")
                .toString();

        final RequestBody formBody = RequestBody.create("", null);

        final Request request = new Request.Builder()
                .url(userProfileUrl)
                .addHeader("Authorization", "Bearer " + tokenInfo.accessToken())
                .post(formBody)
                .build();

        return executeRequest(request, response -> OktaUser.fromJSON(response.body().string()));
    }

    private interface Callback<T> {
        T onResponse(Response response) throws IOException;
    }

    private <T> T executeRequest(Request request, Callback<T> callback) throws IOException {
        final Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            final String responseBody = response.body().string();
            final String errorMessage = isNotBlank(responseBody) ? responseBody : response.message();
            throw new RuntimeException(format(API_ERROR_MSG, request.url().encodedPath(), errorMessage));
        }

        return callback.onResponse(response);
    }

    private void validateTokenInfo(TokenInfo tokenInfo) {
        if (tokenInfo == null) {
            throw new RuntimeException("[OktaApiClient] TokenInfo must not be null.");
        }
    }
}
