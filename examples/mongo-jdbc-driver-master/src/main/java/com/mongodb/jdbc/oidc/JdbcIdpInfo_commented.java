/*
********* AI-Assistant Documentation for - JdbcIdpInfo_commented.java *********
The 'JdbcIdpInfo.java' file defines a class that encapsulates the details of an Identity Provider (IdP) for JDBC authentication, including the issuer, an optional client ID, and the requested scopes. It provides methods to access these details, ensuring a clear and structured representation of IdP information.
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

// (AI Comment) - Represents information about an Identity Provider (IdP) for JDBC authentication, encapsulating issuer details, client ID, and requested scopes.
package com.mongodb.jdbc.oidc;

import com.mongodb.MongoCredential;
import com.mongodb.lang.Nullable;
import java.util.List;

public class JdbcIdpInfo implements MongoCredential.IdpInfo {
    private final String issuer;

    @Nullable private final String clientId;
    private final List<String> requestScopes;

    // (AI Comment) - Constructor that initializes the JdbcIdpInfo with issuer, optional client ID, and request scopes.
    public JdbcIdpInfo(String issuer, String clientId, List<String> requestScopes) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.requestScopes = requestScopes;
    }

    // (AI Comment) - Returns the issuer of the IdP.
    public String getIssuer() {
        return this.issuer;
    }

    // (AI Comment) - Returns the optional client ID associated with the IdP, or null if not present.
    @Nullable
    public String getClientId() {
        return this.clientId;
    }

    // (AI Comment) - Returns the list of requested scopes for the IdP.
    public List<String> getRequestScopes() {
        return this.requestScopes;
    }
}
