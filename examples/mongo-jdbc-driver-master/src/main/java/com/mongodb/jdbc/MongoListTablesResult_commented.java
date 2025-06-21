/*
********* AI-Assistant Documentation for - MongoListTablesResult_commented.java *********
This file defines the 'MongoListTablesResult' class, which models the result of a MongoDB operation that lists tables, including their names and types. It provides a method to set the type of the table with specific mapping logic for MongoDB collections.
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

// (AI Comment) - Represents the result of a MongoDB list tables operation, encapsulating table name and type.
public class MongoListTablesResult {
    public static final String TABLE = "table";
    public static final String COLLECTION = "collection";

    public String name;
    public String type;

    // (AI Comment) - Sets the type of the table; maps 'collection' to 'table' for consistency.
    public void setType(String type) {
        // If mongodb type is COLLECTION, map it as TABLE.
        // Otherwise, keep the type as is.
        this.type = type.equalsIgnoreCase(COLLECTION) ? TABLE : type;
    }
}
