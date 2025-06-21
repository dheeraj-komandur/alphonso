/*
********* AI-Assistant Documentation for - TestRFC8252Server_commented.java *********
TestRFC8252Server.java is a utility for testing the RFC8252HttpServer's functionality in handling OIDC responses. It initializes the server, waits for a response, and manages exceptions, providing a simple way to verify the server's operation.
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

// (AI Comment) - Package declaration for the manual tests related to OIDC functionality.
package com.mongodb.jdbc.oidc.manualtests;

import com.mongodb.jdbc.oidc.OidcResponse;
import com.mongodb.jdbc.oidc.OidcTimeoutException;
import com.mongodb.jdbc.oidc.RFC8252HttpServer;
import java.io.IOException;
// (AI Comment) - Main class to start the RFC8252HttpServer and wait for the OIDC response. Used for testing the serving of the HTML pages and the OIDC response.
/**
 * Main class to start the RFC8252HttpServer and wait for the OIDC response Used for testing the
 * serving of the HTML pages and the OIDC response
 */
public class TestRFC8252Server {
    // (AI Comment) - Main method that initializes and starts the RFC8252HttpServer, waits for an OIDC response, and handles exceptions.
    public static void main(String[] args) {
        // (AI Comment) - Defines the port for the RFC8252HttpServer using the default redirect port.
        int port = RFC8252HttpServer.DEFAULT_REDIRECT_PORT;
        // (AI Comment) - Instantiates the RFC8252HttpServer to handle OIDC requests.
        RFC8252HttpServer server = new RFC8252HttpServer();
        // (AI Comment) - Starts the RFC8252HttpServer to begin listening for requests.
        try {
            server.start();
            System.out.println("Server started on port " + port);

            // (AI Comment) - Retrieves the OIDC response from the server and prints the result to the console.
            // Wait for the OIDC response
            OidcResponse oidcResponse = server.getOidcResponse();
            System.out.println("Server Result:\n" + oidcResponse.toString());

            // (AI Comment) - Pauses the main thread for 2 seconds to allow for processing.
            Thread.sleep(2000);
        // (AI Comment) - Catches and handles IO, OIDC timeout, and interruption exceptions, printing the stack trace.
        } catch (IOException | OidcTimeoutException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // (AI Comment) - Stops the RFC8252HttpServer to clean up resources.
            server.stop();
        }
    }
}
