/*
********* AI-Assistant Documentation for - RemoteMemberRegistrationIT_commented.java *********
The 'RemoteMemberRegistrationIT.java' file contains integration tests for the member registration functionality of a web service, validating the creation and cleanup of member records through HTTP requests.
*/

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.test;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

// (AI Comment) - This class contains integration tests for remote member registration, ensuring that members can be created and cleaned up correctly via the HTTP API.
public class RemoteMemberRegistrationIT {

    private static final Logger log = Logger.getLogger(RemoteMemberRegistrationIT.class.getName());

    private BigInteger createdId;

    // (AI Comment) - Constructs the HTTP endpoint URI for member registration, defaulting to localhost if no server host is specified.
    protected URI getHTTPEndpoint() {
        String host = getServerHost();
        if (host == null) {
            host = "http://localhost:8080";
        }
        try {
            return new URI(host + "/api/members");
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    // (AI Comment) - Retrieves the server host from environment variables or system properties, returning null if not found.
    private String getServerHost() {
        String host = System.getenv("SERVER_HOST");
        if (host == null) {
            host = System.getProperty("server.host");
        }
        return host;
    }

    // (AI Comment) - Tests the registration of a new member by sending a POST request to the member registration endpoint and verifying the response.
    @Test
    public void testRegister() throws Exception {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane@mailinator.com");
        newMember.setPhoneNumber("2125551234");
        JsonObject json = Json.createObjectBuilder()
                .add("name", "Jane Doe")
                .add("email", "jane@mailinator.com")
                .add("phoneNumber", "2125551234").build();
        HttpRequest request = HttpRequest.newBuilder(getHTTPEndpoint())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(201, response.statusCode());
        JSONObject jsonObject = new JSONObject(response.body().toString());
        log.info("Member was created: " + jsonObject);
        createdId = new BigInteger(jsonObject.getString("id"));
    }

    // (AI Comment) - Cleans up the created member after each test by sending a DELETE request to the member endpoint.
    @AfterEach
    public void cleanUp() throws Exception {
        if (createdId != null) {
            log.info("Attempting cleanup of test member " + createdId + "...");
            HttpRequest request = HttpRequest.newBuilder(getHTTPEndpoint().resolve("/api/members/" + createdId))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Cleanup test member response: " + response);
        }
    }
}
