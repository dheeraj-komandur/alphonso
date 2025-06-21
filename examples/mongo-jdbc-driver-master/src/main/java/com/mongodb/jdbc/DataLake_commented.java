/*
********* AI-Assistant Documentation for - DataLake_commented.java *********
The 'DataLake.java' file defines a simple class that holds version information for a Data Lake, providing a method to output its state as a string for logging purposes.
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

package com.mongodb.jdbc;

// (AI Comment) - Represents a DataLake object containing version information for logging and tracking.
public class DataLake {
    public String version;
    public String mongoSQLVersion;

    // (AI Comment) - Overrides the toString method to provide a string representation of the DataLake instance, including version details.
    // Override toString for logging
    @Override
    public String toString() {
        return "DataLake{"
                + "version='"
                + version
                + '\''
                + ", mongoSQLVersion="
                + mongoSQLVersion
                + '}';
    }
}
