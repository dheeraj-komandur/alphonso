/*
********* AI-Assistant Documentation for - TestOidcAuthFlow_commented.java *********
This file provides a test implementation for the OIDC authentication flow using JDBC, initializing necessary components and executing the authentication process to validate its functionality.
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
import com.mongodb.jdbc.oidc.JdbcOidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import java.time.Duration;

public class TestOidcAuthFlow {
    // (AI Comment) - This class serves as a test harness for the OIDC authentication flow, initializing necessary components and executing the authentication process.
    public static void main(String[] args) {
        // (AI Comment) - Main method that sets up the OIDC authentication flow, creates a callback context with a specified timeout, and invokes the authentication test.
        OidcAuthFlow authFlow = new OidcAuthFlow();

        Duration timeout = Duration.ofMinutes(5);
        // (AI Comment) - Creates a Duration object representing a 5-minute timeout for the OIDC authentication process.
        OidcCallbackContext callbackContext =
                new JdbcOidcCallbackContext(timeout, 1, null, TestOidcUtils.IDP_INFO, null);
        // (AI Comment) - Initializes the JdbcOidcCallbackContext with parameters including timeout, IDP information, and invokes the test for the authentication code flow.

        TestOidcUtils.testAuthCodeFlow(callbackContext, authFlow);
    }
}
