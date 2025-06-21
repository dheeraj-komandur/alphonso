/*
********* AI-Assistant Documentation for - BuildInfo_commented.java *********
The 'BuildInfo.java' file defines a class that holds and manages build-related information for MongoDB, including version details and associated modules. It provides methods to retrieve version information and a string representation for logging purposes.
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

import java.util.List;
import java.util.Set;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents build information including version details and associated modules. It encapsulates version strings, an array of version integers, and additional data related to DataLake.
public class BuildInfo {
    private String fullVersion;
    private List<Integer> versionArray;
    public Set<String> modules;
    public int ok;

    public DataLake dataLake;

    @BsonCreator
    // (AI Comment) - Constructor that initializes BuildInfo with version details, version array, modules, status, and DataLake information. It constructs the full version string based on the provided parameters.
    public BuildInfo(
            @BsonProperty("version") String version,
            @BsonProperty("versionArray") List<Integer> versionArray,
            @BsonProperty("modules") Set<String> modules,
            @BsonProperty("ok") int ok,
            @BsonProperty("dataLake") DataLake dataLake)
            throws IndexOutOfBoundsException {
        this.fullVersion = version;
        this.versionArray = versionArray;
        if (dataLake != null) {
            this.fullVersion += "." + dataLake.version + "." + dataLake.mongoSQLVersion;
        }
        this.dataLake = dataLake;
        this.ok = ok;
        this.modules = modules;
    }

    // (AI Comment) - Returns the full version string of the build.
    public String getFullVersion() {
        return this.fullVersion;
    }

    // (AI Comment) - Returns the major version number from the version array, throwing an exception if the array is empty.
    public int getMajorVersion() throws IndexOutOfBoundsException {
        return this.versionArray.get(0);
    }

    // (AI Comment) - Returns the minor version number from the version array, throwing an exception if the array is empty.
    public int getMinorVersion() throws IndexOutOfBoundsException {
        return this.versionArray.get(1);
    }

    // (AI Comment) - Overrides the toString method to provide a string representation of the BuildInfo object, including all relevant fields for logging purposes.
    // Override toString for logging
    @Override
    public String toString() {
        return "BuildInfo{"
                + "fullVersion='"
                + fullVersion
                + '\''
                + ", versionArray="
                + versionArray
                + ", majorVersion="
                + this.getMajorVersion()
                + ", minorVersion="
                + this.getMinorVersion()
                + ", modules="
                + modules
                + ", ok="
                + ok
                + ", dataLake="
                + dataLake
                + '}';
    }
}
