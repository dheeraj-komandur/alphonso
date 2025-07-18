/*
********* AI-Assistant Documentation for - OidcTimeoutException_commented.java *********
The 'OidcTimeoutException.java' file defines a custom exception for signaling timeout errors in OpenID Connect operations, enhancing error handling in the MongoDB JDBC implementation.
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

package com.mongodb.jdbc.oidc;

// (AI Comment) - Custom exception class for handling OIDC timeout errors, extending the base Exception class.
public class OidcTimeoutException extends Exception {
    public OidcTimeoutException(String message) {
        super(message);
    // (AI Comment) - Constructor that initializes the exception with a specific error message.
    }
}
