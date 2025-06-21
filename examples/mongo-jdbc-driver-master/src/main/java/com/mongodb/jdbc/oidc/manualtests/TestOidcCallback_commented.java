/*
********* AI-Assistant Documentation for - TestOidcCallback_commented.java *********
The 'TestOidcCallback.java' file is designed to test the OpenID Connect (OIDC) callback functionality, specifically focusing on the processes of obtaining and refreshing access tokens. It provides a simple command-line interface to demonstrate these flows and handle potential errors during the authentication process.
*/

/*
 * Copyright 2024-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.MongoCredential.OidcCallback;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.oidc.JdbcOidcCallback;
import com.mongodb.jdbc.oidc.JdbcOidcCallbackContext;

// (AI Comment) - This class serves as a test harness for the OIDC callback functionality, demonstrating the request and refresh token flows.
public class TestOidcCallback {

    // (AI Comment) - Main method that initiates the OIDC callback test, handling both initial authentication and token refresh.
    public static void main(String[] args) {
        OidcCallback oidcCallback = new JdbcOidcCallback();

        // (AI Comment) - Creates the initial context for the OIDC callback request, including necessary parameters for authentication.
        OidcCallbackContext initialContext =
                new JdbcOidcCallbackContext(null, 1, null, TestOidcUtils.IDP_INFO, null);
        try {
            OidcCallbackResult initialResult = oidcCallback.onRequest(initialContext);
            // (AI Comment) - Handles the result of the initial OIDC callback request, printing access and refresh tokens or an error message.
            if (initialResult != null) {
                System.out.println("Access Token: " + initialResult.getAccessToken());
                System.out.println("Refresh Token: " + initialResult.getRefreshToken());
            } else {
                System.out.println("Authentication failed.");
            }
            // (AI Comment) - Creates a new context for refreshing the access token using the refresh token obtained from the initial request.
            OidcCallbackContext refreshContext =
                    new JdbcOidcCallbackContext(
                            null, 1, initialResult.getRefreshToken(), TestOidcUtils.IDP_INFO, null);
            OidcCallbackResult refreshResult = oidcCallback.onRequest(refreshContext);
            // (AI Comment) - Handles the result of the refresh token request, printing the new tokens or an error message.
            if (refreshResult != null) {
                System.out.println("Refreshed Access Token: " + refreshResult.getAccessToken());
                System.out.println("Refreshed Refresh Token: " + refreshResult.getRefreshToken());
            } else {
                System.out.println("Refresh token flow failed.");
            }
        // (AI Comment) - Catches and logs any exceptions that occur during the OIDC callback test.
        } catch (Exception e) {
            System.err.println("Error during OIDC callback test: " + e.getMessage());
        }
    }
}
