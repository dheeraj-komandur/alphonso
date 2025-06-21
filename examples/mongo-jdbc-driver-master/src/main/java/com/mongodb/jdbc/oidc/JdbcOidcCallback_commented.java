/*
********* AI-Assistant Documentation for - JdbcOidcCallback_commented.java *********
The JdbcOidcCallback class facilitates OIDC authentication in JDBC applications by implementing the OidcCallback interface, managing both refresh token and authorization code flows.
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

// (AI Comment) - Defines the package for JDBC OIDC callback handling.
package com.mongodb.jdbc.oidc;

import com.mongodb.MongoCredential.OidcCallback;
import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.MongoCredential.OidcCallbackResult;
import com.mongodb.jdbc.logging.MongoLogger;
import javax.security.auth.RefreshFailedException;

// (AI Comment) - Implements the OidcCallback interface to handle OIDC authentication flows, including refresh token and authorization code flows.
public class JdbcOidcCallback implements OidcCallback {
    private final OidcAuthFlow oidcAuthFlow;

    // (AI Comment) - Default constructor initializes the OidcAuthFlow without a logger.
    public JdbcOidcCallback() {
        this.oidcAuthFlow = new OidcAuthFlow();
    }

    // (AI Comment) - Constructor that initializes the OidcAuthFlow with a specified logger for logging purposes.
    public JdbcOidcCallback(MongoLogger parentLogger) {
        this.oidcAuthFlow = new OidcAuthFlow(parentLogger);
    }

    // (AI Comment) - Handles OIDC callback requests by determining whether to refresh the token or perform an authorization code flow based on the presence of a refresh token.
    public OidcCallbackResult onRequest(OidcCallbackContext callbackContext) {
        String refreshToken = callbackContext.getRefreshToken();
        // (AI Comment) - Checks if a refresh token is available and executes the appropriate authentication flow.
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                return oidcAuthFlow.doRefresh(callbackContext);
            // (AI Comment) - Catches RefreshFailedException and wraps it in a RuntimeException for higher-level handling.
            } catch (RefreshFailedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                // (AI Comment) - Catches OidcTimeoutException and wraps it in a RuntimeException for higher-level handling.
                return oidcAuthFlow.doAuthCodeFlow(callbackContext);
            } catch (OidcTimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
