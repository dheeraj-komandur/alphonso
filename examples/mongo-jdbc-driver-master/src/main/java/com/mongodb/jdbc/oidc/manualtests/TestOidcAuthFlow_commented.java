/*
********* AI-Assistant Documentation for - TestOidcAuthFlow_commented.java *********
This file contains a test class for the OIDC authentication flow using JDBC, demonstrating how to set up and execute an authorization code flow test.
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

// (AI Comment) - Defines the package for the OIDC manual tests related to MongoDB JDBC.
package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.MongoCredential.OidcCallbackContext;
import com.mongodb.jdbc.oidc.JdbcOidcCallbackContext;
import com.mongodb.jdbc.oidc.OidcAuthFlow;
import java.time.Duration;

// (AI Comment) - Test class for demonstrating the OIDC authentication flow using JDBC. It initializes the authentication flow and executes a test for the authorization code flow.
public class TestOidcAuthFlow {
    // (AI Comment) - Main method that serves as the entry point for the test. It sets up the OIDC authentication flow and initiates the test for the authorization code flow.
    public static void main(String[] args) {
        // (AI Comment) - Creates an instance of OidcAuthFlow to manage the authentication process.
        OidcAuthFlow authFlow = new OidcAuthFlow();

        // (AI Comment) - Sets a timeout duration for the authentication process and initializes the callback context with necessary parameters.
        Duration timeout = Duration.ofMinutes(5);
        OidcCallbackContext callbackContext =
                new JdbcOidcCallbackContext(timeout, 1, null, TestOidcUtils.IDP_INFO, null);

        // (AI Comment) - Calls the utility method to test the authorization code flow using the provided callback context and authentication flow.
        TestOidcUtils.testAuthCodeFlow(callbackContext, authFlow);
    }
}
