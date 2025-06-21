/*
********* AI-Assistant Documentation for - MongoRunCmdListTablesResult_commented.java *********
This file defines the 'MongoRunCmdListTablesResult' class, which encapsulates the result of a MongoDB command to list tables, including cursor information and the first batch of results.
*/

/*
 * Copyright 2023-present MongoDB, Inc.
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

import java.util.ArrayList;

// (AI Comment) - Represents the result of a MongoDB command to list tables, encapsulating cursor information.
public class MongoRunCmdListTablesResult {
    public CursorInfo cursor;

    // (AI Comment) - Returns the cursor information associated with the list tables command result.
    public CursorInfo getCursor() {
        return cursor;
    }

    // (AI Comment) - Encapsulates information about the cursor, including its ID, namespace, and the first batch of results.
    public static class CursorInfo {
        public long id;
        public String ns;
        public ArrayList<MongoListTablesResult> firstBatch;

        // (AI Comment) - Returns the ID of the cursor.
        public long getId() {
            return id;
        }

        // (AI Comment) - Returns the namespace associated with the cursor.
        public String getNs() {
            return ns;
        }

        // (AI Comment) - Returns the first batch of results from the cursor.
        public ArrayList<MongoListTablesResult> getFirstBatch() {
            return firstBatch;
        }
    }
}
