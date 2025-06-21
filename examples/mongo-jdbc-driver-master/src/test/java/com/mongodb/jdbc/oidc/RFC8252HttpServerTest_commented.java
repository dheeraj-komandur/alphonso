/*
********* AI-Assistant Documentation for - RFC8252HttpServerTest_commented.java *********
The 'RFC8252HttpServerTest.java' file contains unit tests for the RFC8252HttpServer class, focusing on validating the server's response to various OpenID Connect callback scenarios. It ensures that the server correctly handles valid and invalid requests, returning appropriate HTTP status codes and response data.
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

// (AI Comment) - Test class for RFC8252HttpServer, responsible for validating the server's response handling for various OIDC callback scenarios.
class RFC8252HttpServerTest {
    private RFC8252HttpServer server;

    // (AI Comment) - Sets up the RFC8252HttpServer instance before each test, ensuring the server is started for testing.
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

    // (AI Comment) - Tests the server's response when a valid authorization code is provided, expecting a 200 response and correct OIDC response values.
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

    // (AI Comment) - Tests the server's response when an error is provided in the callback, expecting a 400 response and correct error details.
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

    // (AI Comment) - Tests the server's response when no parameters are provided, expecting a 404 response and appropriate error messages.
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

    // (AI Comment) - Tests the server's response when unknown parameters are included in the redirect, expecting a 404 response and error details.
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

    @Test
    // (AI Comment) - Tests the server's handling of ampersands in parameter values, expecting a 200 response and correct decoding of the state.
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

    @Test
    // (AI Comment) - Tests the server's handling of equals signs in parameter values, expecting a 200 response and correct decoding of the state.
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
