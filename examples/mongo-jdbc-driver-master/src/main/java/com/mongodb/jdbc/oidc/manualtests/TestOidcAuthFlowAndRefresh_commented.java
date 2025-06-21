/*
********* AI-Assistant Documentation for - TestOidcAuthFlowAndRefresh_commented.java *********
The 'TestOidcAuthFlowAndRefresh.java' file is designed to test the OIDC authentication flow and the refresh token mechanism within the MongoDB JDBC OIDC package. It initializes the authentication process, handles the retrieval of access and refresh tokens, and manages the refresh token flow, providing a console output of the results.
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

import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.oidc.JdbcOidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import java.time.Duration;

// (AI Comment) - This class contains the main method to test the OIDC authentication flow and refresh token functionality.
public class TestOidcAuthFlowAndRefresh {
    // (AI Comment) - Main entry point for the application that initiates the OIDC authentication flow and handles token refresh.
    public static void main(String[] args) {
        // (AI Comment) - Initializes the OIDC authentication flow and sets up the callback context with a timeout and IDP information.
        OidcAuthFlow authFlow = new OidcAuthFlow();

        Duration timeout = Duration.ofMinutes(5);
        OidcCallbackContext callbackContext =
                new JdbcOidcCallbackContext(timeout, 1, null, TestOidcUtils.IDP_INFO, null);

        OidcCallbackResult result = TestOidcUtils.testAuthCodeFlow(callbackContext, authFlow);
        // (AI Comment) - Handles the result of the authentication flow, retrieves the refresh token, and attempts to refresh the access token.
        if (result != null) {
            // get refresh token from the AuthCodeFLow result
            // (AI Comment) - Creates a new callback context for refreshing the token using the refresh token obtained from the initial flow.
            OidcCallbackContext refreshContext =
                    new JdbcOidcCallbackContext(
                            timeout, 1, result.getRefreshToken(), TestOidcUtils.IDP_INFO, null);
            try {
                // (AI Comment) - Executes the refresh token flow and handles potential exceptions, printing results or errors to the console.
                OidcCallbackResult refreshResult = authFlow.doRefresh(refreshContext);
                if (refreshResult != null) {
                    System.out.println("Refreshed Access Token: " + refreshResult.getAccessToken());
                    System.out.println(
                            "Refreshed Refresh Token: " + refreshResult.getRefreshToken());
                } else {
                    System.out.println("Refresh token flow failed.");
                }
            } catch (Exception e) {
                System.err.println(
                        "An error occurred while running the refresh token flow: "
                                + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
