/*
********* AI-Assistant Documentation for - TestOidcCallbackWithShortTimeout_commented.java *********
This file contains a test class for validating the behavior of OIDC callbacks with a short timeout in a MongoDB JDBC context. It aims to ensure that the system correctly handles timeout scenarios when user input is required.
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
import com.mongodb.jdbc.oidc.OidcTimeoutException;
import java.time.Duration;

// (AI Comment) - This class tests the OIDC callback functionality with a deliberately short timeout to ensure that a timeout exception is triggered when user input is required.
public class TestOidcCallbackWithShortTimeout {

    // (AI Comment) - Main method that initiates the OIDC callback test with a short timeout and handles the expected timeout exception.
    public static void main(String[] args) {
        OidcCallback oidcCallback = new JdbcOidcCallback();

        // (AI Comment) - Sets a short timeout duration of 2 seconds for the OIDC callback context, which is intentionally brief to trigger a timeout during the test.
        Duration shortTimeout = Duration.ofSeconds(2); // intentionally short to trigger timeout
        OidcCallbackContext context =
                new JdbcOidcCallbackContext(shortTimeout, 1, null, TestOidcUtils.IDP_INFO, null);

        // (AI Comment) - Try-catch block that executes the OIDC callback and handles the expected OidcTimeoutException, logging appropriate messages based on the outcome.
        try {
            OidcCallbackResult result = oidcCallback.onRequest(context);
            // Timeout is expected when user input is required as it should take longer than 2 second.
            // It may pass if the user is already signed in and credentials are saved in the browser.
            System.out.println(
                    "This should not print, timeout expected. Sign out of the IdP or clear the browser cache "
                            + "to trigger a timeout.");
            System.out.println(result);
        } catch (Exception e) {
            // (AI Comment) - Handles exceptions thrown during the OIDC callback execution, specifically checking for the OidcTimeoutException and logging the result.
            if (e.getCause() instanceof OidcTimeoutException) {
                System.err.println(
                        "Expected OidcTimeoutException occurred: " + e.getCause().getMessage());
            } else {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
