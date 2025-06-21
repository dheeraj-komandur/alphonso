/*
********* AI-Assistant Documentation for - RFC8252HttpServerTest_commented.java *********
This file contains unit tests for the RFC8252HttpServer class, validating its handling of OIDC responses through various scenarios, including successful callbacks, error responses, and parameter handling.
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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// (AI Comment) - Test class for RFC8252HttpServer, validating its behavior with various OIDC response scenarios.
class RFC8252HttpServerTest {
    private RFC8252HttpServer server;

    // (AI Comment) - Sets up the RFC8252HttpServer instance before each test, ensuring a fresh server state.
    @BeforeEach
    void setUp() throws IOException {
        server = new RFC8252HttpServer();
        server.start();
    }

    // (AI Comment) - Stops the RFC8252HttpServer instance after each test to clean up resources.
    @AfterEach
    void tearDown() {
        server.stop();
    }

    // (AI Comment) - Tests the server's response to a valid OIDC callback, expecting a 200 response and correct parameters.
    @Test
    void testAcceptedResponse() throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?code=1234&state=foo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        assertNull(connection.getHeaderField("Location"));

        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getCode());
        assertEquals("foo", oidcResponse.getState());
    }

    // (AI Comment) - Tests the server's response to an error callback, expecting a 400 response and correct error details.
    @Test
    void testErrorResponse() throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?error=1234&error_description=foo");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(400, connection.getResponseCode());

        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getError());
        assertEquals("foo", oidcResponse.getErrorDescription());
    }

    // (AI Comment) - Tests the server's behavior when required parameters are missing, expecting a 404 response.
    @Test
    void testMissingParameters() throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(404, connection.getResponseCode());

        OidcResponse oidcResponse = server.getOidcResponse();
        assertNull(oidcResponse.getCode());
        assertNull(oidcResponse.getState());
        assert (oidcResponse.getError().equals("Not found"));
        assert (oidcResponse.getErrorDescription().equals("Not found. Parameters: No parameters"));
    }

    // (AI Comment) - Tests the server's response to unknown parameters in the redirect, expecting a 404 response.
    @Test
    void testRedirectUnknownParameters()
            throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/redirect?foo=bar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(404, connection.getResponseCode());

        OidcResponse oidcResponse = server.getOidcResponse();
        assertNull(oidcResponse.getCode());
        assertNull(oidcResponse.getState());
        assert (oidcResponse.getError().equals("Not found"));
        assert (oidcResponse.getErrorDescription().equals("Not found. Parameters: foo=bar"));
    }

    // (AI Comment) - Tests the server's handling of ampersands in parameter values, expecting a 200 response.
    @Test
    void testAmpersandInParameterValue()
            throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?code=1234&state=foo%26bar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getCode());
        assertEquals("foo&bar", oidcResponse.getState());
    }

    // (AI Comment) - Tests the server's handling of equals signs in parameter values, expecting a 200 response.
    @Test
    void testEqualsInParameterValue()
            throws OidcTimeoutException, IOException, InterruptedException {
        URL url =
                new URL(
                        "http://localhost:"
                                + RFC8252HttpServer.DEFAULT_REDIRECT_PORT
                                + "/callback?code=1234&state=foo%3Dbar");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        assertEquals(200, connection.getResponseCode());
        OidcResponse oidcResponse = server.getOidcResponse();
        assertEquals("1234", oidcResponse.getCode());
        assertEquals("foo=bar", oidcResponse.getState());
    }
}
