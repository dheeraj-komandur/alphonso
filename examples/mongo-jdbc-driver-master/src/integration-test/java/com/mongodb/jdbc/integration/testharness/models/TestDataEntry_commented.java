/*
********* AI-Assistant Documentation for - TestDataEntry_commented.java *********
The 'TestDataEntry.java' file defines a data model for integration testing with MongoDB, encapsulating essential attributes such as database name, collection name, view name, documents, schema, and indexes.
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

package com.mongodb.jdbc.integration.testharness.models;

import java.util.List;
import java.util.Map;

// (AI Comment) - Represents a data entry for testing, encapsulating database, collection, view, documents, schema, and indexes.
public class TestDataEntry {
    // (AI Comment) - The name of the database associated with the test data entry.
    public String db;
    // (AI Comment) - The name of the collection associated with the test data entry.
    public String collection;
    // (AI Comment) - The name of the view associated with the test data entry.
    public String view;
    // (AI Comment) - A list of documents represented as maps, containing the main test data.
    public List<Map<String, Object>> docs;
    // (AI Comment) - A list of documents represented as maps, containing extended JSON test data.
    public List<Map<String, Object>> docsExtJson;
    // (AI Comment) - A map representing the schema of the test data entry.
    public Map<String, Object> schema;
    // (AI Comment) - A list of non-unique indexes represented as maps, associated with the test data entry.
    public List<Map<String, Object>> nonuniqueIndexes;
}
