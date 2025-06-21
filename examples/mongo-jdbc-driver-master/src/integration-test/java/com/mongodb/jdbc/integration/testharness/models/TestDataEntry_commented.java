/*
********* AI-Assistant Documentation for - TestDataEntry_commented.java *********
This file defines the 'TestDataEntry' class, which is used to represent a test data entry for MongoDB integration tests. It encapsulates essential attributes such as database, collection, view, documents, schema, and indexes, facilitating the management of test data.
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
    // (AI Comment) - The name of the database associated with this test data entry.
    public String db;
    // (AI Comment) - The name of the collection associated with this test data entry.
    public String collection;
    // (AI Comment) - The name of the view associated with this test data entry.
    public String view;
    // (AI Comment) - A list of documents represented as maps, containing the main data for this entry.
    public List<Map<String, Object>> docs;
    // (AI Comment) - A list of documents in extended JSON format for this entry.
    public List<Map<String, Object>> docsExtJson;
    // (AI Comment) - A map representing the schema of the documents in this entry.
    public Map<String, Object> schema;
    // (AI Comment) - A list of non-unique indexes associated with this test data entry.
    public List<Map<String, Object>> nonuniqueIndexes;
}
