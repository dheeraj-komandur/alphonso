/*
********* AI-Assistant Documentation for - MongoJsonSchemaResult_commented.java *********
This file defines the 'MongoJsonSchemaResult' class, which serves as a data structure to represent the outcome of a JSON schema operation in MongoDB, including its success status, associated metadata, the schema itself, and the order of selected fields.
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

import java.util.List;
import java.util.Map;

// (AI Comment) - Represents the result of a MongoDB JSON schema operation, encapsulating status, metadata, schema details, and selection order.
public class MongoJsonSchemaResult {
    // (AI Comment) - Indicates the success status of the operation, where 1 means success and 0 means failure.
    public int ok;
    // (AI Comment) - Contains metadata related to the JSON schema operation, represented as key-value pairs.
    public Map<String, String> metadata;
    // (AI Comment) - Holds the versioned JSON schema object that defines the structure of the data.
    public MongoVersionedJsonSchema schema;
    // (AI Comment) - Specifies the order of selection for the fields in the JSON schema, represented as a list of lists.
    public List<List<String>> selectOrder;
}
