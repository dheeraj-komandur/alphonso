/*
********* AI-Assistant Documentation for - JdbcOidcCallbackContext_commented.java *********
This file defines the JdbcOidcCallbackContext class, which encapsulates the context for OIDC callbacks in JDBC, including user information and session details. It provides methods to access various attributes related to the OIDC callback, ensuring a structured approach to handling authentication contexts.
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

// (AI Comment) - Defines the package for JDBC OIDC callback context handling.
package com.mongodb.jdbc.oidc;

import com.mongodb.MongoCredential.IdpInfo;
import com.mongodb.MongoCredential.OidcCallbackContext;
import java.time.Duration;

// (AI Comment) - Represents the context for OIDC callbacks in JDBC, encapsulating user and session information.
public class JdbcOidcCallbackContext implements OidcCallbackContext {
    private Duration timeout;
    private int version;
    private String refreshToken;
    private IdpInfo idpInfo;
    private String userName;

    // (AI Comment) - Constructor that initializes the JdbcOidcCallbackContext with timeout, version, refresh token, IDP info, and username.
    public JdbcOidcCallbackContext(
            Duration timeout, int version, String refreshToken, IdpInfo idpInfo, String userName) {
        this.timeout = timeout;
        this.version = version;
        this.refreshToken = refreshToken;
        this.idpInfo = idpInfo;
        this.userName = userName;
    }

    // (AI Comment) - Returns the username associated with the OIDC callback context.
    public String getUserName() {
        return this.userName;
    }

    // (AI Comment) - Returns the timeout duration for the OIDC callback context.
    public Duration getTimeout() {
        return this.timeout;
    }

    // (AI Comment) - Returns the version of the OIDC callback context.
    public int getVersion() {
        return this.version;
    }

    // (AI Comment) - Returns the refresh token associated with the OIDC callback context.
    public String getRefreshToken() {
        return this.refreshToken;
    }

    // (AI Comment) - Returns the IDP information associated with the OIDC callback context.
    public IdpInfo getIdpInfo() {
        return this.idpInfo;
    }
}
