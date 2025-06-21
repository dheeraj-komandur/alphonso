/*
********* AI-Assistant Documentation for - TestOidcCallbackWithBadRefreshToken_commented.java *********
The file contains a test class that verifies the behavior of the OIDC callback when a bad refresh token is provided, ensuring that the expected exception is thrown and handled correctly.
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
import javax.security.auth.RefreshFailedException;

// (AI Comment) - This class tests the behavior of the OIDC callback when provided with a bad refresh token, expecting a RefreshFailedException.
public class TestOidcCallbackWithBadRefreshToken {

    // (AI Comment) - Main method that initializes the OIDC callback and simulates a request with a bad refresh token.
    public static void main(String[] args) {
        OidcCallback oidcCallback = new JdbcOidcCallback();

        // (AI Comment) - Defines a bad refresh token and creates an OIDC callback context with it.
        String badRefreshToken = "bad-refresh-token";
        OidcCallbackContext context =
                new JdbcOidcCallbackContext(null, 1, badRefreshToken, TestOidcUtils.IDP_INFO, null);

        // (AI Comment) - Attempts to invoke the OIDC callback with the context, handling expected exceptions.
        try {
            OidcCallbackResult result = oidcCallback.onRequest(context);
            System.out.println("This should not print, bad refresh token expected to fail.");
            System.out.println(result);
        } catch (Exception e) {
            // (AI Comment) - Catches exceptions thrown during the callback invocation, specifically checking for RefreshFailedException.
            if (e.getCause() instanceof RefreshFailedException) {
                System.err.println(
                        "Expected RefreshFailedException occurred: " + e.getCause().getMessage());
            } else {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
