/*
********* AI-Assistant Documentation for - MongoVersionedJsonSchema_commented.java *********
This file defines the 'MongoVersionedJsonSchema' class, which represents a versioned JSON schema for MongoDB, including functionality for initialization from a version and a JSON schema.
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

// (AI Comment) - Defines the package for MongoDB JDBC integration.
package com.mongodb.jdbc;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents a versioned JSON schema for MongoDB, encapsulating the schema version and the corresponding JSON schema.
public class MongoVersionedJsonSchema {
    public Integer version;
    public MongoJsonSchema mongoJsonSchema;

    // (AI Comment) - Default constructor for MongoVersionedJsonSchema, initializes an empty schema.
    /** Empty Json schema. */
    public MongoVersionedJsonSchema() {}

    // (AI Comment) - Constructor that initializes the MongoVersionedJsonSchema with a specified version and schema, converting the provided schema to a simplified MongoDB JSON schema.
    /**
     * Deserialized json schema from a 'sqlgetschema' command.
     *
     * @param version The schema version.
     * @param schema The schema.
     */
    @BsonCreator
    public MongoVersionedJsonSchema(
            @BsonProperty("version") final Integer version,
            @BsonProperty("jsonSchema") JsonSchema schema) {
        this.version = version;
        this.mongoJsonSchema = MongoJsonSchema.toSimplifiedMongoJsonSchema(schema);
    }
}
