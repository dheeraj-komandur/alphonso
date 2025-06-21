/*
********* AI-Assistant Documentation for - MongoConnectionProperties_commented.java *********
The 'MongoConnectionProperties.java' file defines a class that encapsulates the properties required for establishing a connection to a MongoDB database, including connection string, database name, logging configurations, and client-specific information. It provides methods to retrieve these properties and generate a unique key for connection caching.
*/

/*
 * Copyright 2022-present MongoDB, Inc.
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

package com.mongodb.jdbc;

import com.mongodb.ConnectionString;
import java.io.File;
import java.util.logging.Level;

// (AI Comment) - Represents the connection properties for MongoDB, encapsulating details such as connection string, database name, logging configuration, and client information.
public class MongoConnectionProperties {
    private ConnectionString connectionString;
    private String database;
    private Level logLevel;
    private File logDir;
    private String clientInfo;
    private boolean extJsonMode;
    private String x509PemPath;

    // (AI Comment) - Constructor that initializes MongoConnectionProperties with the specified connection string, database name, logging level, log directory, client information, JSON mode, and X.509 PEM path.
    public MongoConnectionProperties(
            ConnectionString connectionString,
            String database,
            Level logLevel,
            File logDir,
            String clientInfo,
            boolean extJsonMode,
            String x509PemPath) {
        this.connectionString = connectionString;
        this.database = database;
        this.logLevel = logLevel;
        this.logDir = logDir;
        this.clientInfo = clientInfo;
        this.extJsonMode = extJsonMode;
        this.x509PemPath = x509PemPath;
    }

    // (AI Comment) - Returns the connection string used for MongoDB connections.
    public ConnectionString getConnectionString() {
        return connectionString;
    }

    // (AI Comment) - Returns the name of the database to connect to.
    public String getDatabase() {
        return database;
    }

    // (AI Comment) - Returns the logging level for the connection.
    public Level getLogLevel() {
        return logLevel;
    }

    // (AI Comment) - Returns the directory where logs are stored.
    public File getLogDir() {
        return logDir;
    }

    // (AI Comment) - Returns client information associated with the connection.
    public String getClientInfo() {
        return clientInfo;
    }

    // (AI Comment) - Indicates whether extended JSON mode is enabled for the connection.
    public boolean getExtJsonMode() {
        return extJsonMode;
    }

    // (AI Comment) - Returns the path to the X.509 PEM file used for authentication.
    public String getX509PemPath() {
        return x509PemPath;
    }

    /*
     * Generate a unique key for the connection properties. This key is used to identify the connection properties in the
     * connection cache. Properties that do not differentiate a specific client such as the log level are not included in the key.
     */
    // (AI Comment) - Generates a unique key for the connection properties, excluding non-distinguishing properties like log level, to identify the connection in the cache.
    public Integer generateKey() {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(connectionString.toString());
        if (clientInfo != null) {
            keyBuilder.append(":clientInfo=").append(clientInfo);
        }
        return keyBuilder.toString().hashCode();
    }
}
