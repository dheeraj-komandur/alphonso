/*
********* AI-Assistant Documentation for - TestOidcUtils_commented.java *********
The 'TestOidcUtils.java' file provides utility functions for testing OIDC authentication flows in the MongoDB JDBC context, including constants for issuer and client ID, and a method to execute the authentication code flow.
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

// (AI Comment) - Defines the package for OIDC manual tests related to MongoDB JDBC.
package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.MongoCredential.IdpInfo;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.oidc.JdbcIdpInfo;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import java.util.Collections;
import java.util.List;

// (AI Comment) - TestOidcUtils class provides utility methods for testing OIDC authentication flows, including constants for issuer and client ID.
public class TestOidcUtils {

    // (AI Comment) - OIDC issuer URL and client ID constants used for authentication.
    public static String OIDC_ISSUER = "https://mongodb-dev.okta.com/oauth2/ausqrxbcr53xakaRR357";
    public static String OIDC_CLIENT_ID = "0oarvap2r7PmNIBsS357";
    public static final List<String> OPENID_SCOPE = Collections.singletonList("openid");

    // (AI Comment) - IDP_INFO constant holds the identity provider information for OIDC authentication.
    public static final IdpInfo IDP_INFO =
            new JdbcIdpInfo(OIDC_ISSUER, OIDC_CLIENT_ID, OPENID_SCOPE);

    // (AI Comment) - testAuthCodeFlow method executes the OIDC authentication code flow and handles the result, including error handling.
    public static OidcCallbackResult testAuthCodeFlow(
            OidcCallbackContext callbackContext, OidcAuthFlow authFlow) {

        // (AI Comment) - Try-catch block for executing the authentication flow and handling exceptions.
        try {
            // (AI Comment) - Executes the authentication code flow and processes the result, printing tokens if successful.
            OidcCallbackResult result = authFlow.doAuthCodeFlow(callbackContext);
            if (result != null) {
                System.out.println("Access Token: " + result.getAccessToken());
                System.out.println("Refresh Token: " + result.getRefreshToken());
                return result;
            } else {
                System.out.println("Authentication failed.");
            }
        } catch (Exception e) {
            System.err.println(
                    "An error occurred while running the OIDC authentication flow: "
                            + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
