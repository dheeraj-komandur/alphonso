/*
********* AI-Assistant Documentation for - OidcResponse_commented.java *********
The 'OidcResponse.java' file defines a class that models the response from an OpenID Connect authentication process, encapsulating fields for the authorization code, state, error, and error description, along with appropriate accessors and a string representation method.
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

// (AI Comment) - Represents an OIDC response containing authorization code, state, error, and error description.
public class OidcResponse {
    private String code;
    private String state;
    private String error;
    private String errorDescription;

    // (AI Comment) - Returns the authorization code received in the OIDC response.
    public String getCode() {
        return code;
    }

    // (AI Comment) - Returns the state parameter received in the OIDC response.
    public String getState() {
        return state;
    }

    // (AI Comment) - Returns the error code if an error occurred during the OIDC process.
    public String getError() {
        return error;
    }

    // (AI Comment) - Returns a description of the error that occurred during the OIDC process.
    public String getErrorDescription() {
        return errorDescription;
    }

    // (AI Comment) - Sets the authorization code for the OIDC response.
    public void setCode(String code) {
        this.code = code;
    }

    // (AI Comment) - Sets the state parameter for the OIDC response.
    public void setState(String state) {
        this.state = state;
    }

    // (AI Comment) - Sets the error code for the OIDC response.
    public void setError(String error) {
        this.error = error;
    }

    // (AI Comment) - Sets the error description for the OIDC response.
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    // (AI Comment) - Generates a string representation of the OIDC response, including code, state, error, and error description if present.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (code != null) {
            sb.append("Code: ").append(code).append("\n");
        }
        if (state != null) {
            sb.append("State: ").append(state).append("\n");
        }
        if (error != null) {
            sb.append("Error: ").append(error).append("\n");
        }
        if (errorDescription != null) {
            sb.append("Error Description: ").append(errorDescription).append("\n");
        }
        return sb.toString();
    }
}
