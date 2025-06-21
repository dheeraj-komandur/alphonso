/*
********* AI-Assistant Documentation for - JsonSchema_commented.java *********
The 'JsonSchema.java' file defines a class that serves as a data structure for deserializing JSON schema representations in MongoDB, providing methods for equality comparison and hash code generation.
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
// (AI Comment) - This file defines the JsonSchema class, which serves as a simple POJO for deserializing JSON schema structures used in MongoDB.

package com.mongodb.jdbc;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bson.BsonValue;

// Simple POJO for deserializing jsonschema.
// For more details on jsonSchema, see https://docs.mongodb.com/manual/reference/operator/query/jsonSchema/.
public class JsonSchema {

    // (AI Comment) - Fields representing the schema's BSON type, properties, validation criteria, and additional properties.
    public BsonValue bsonType;
    public Map<String, JsonSchema> properties;
    public Set<JsonSchema> anyOf;
    public Set<String> required;
    public BsonValue items;
    public BsonValue additionalProperties;

    // (AI Comment) - Overrides the equals method to provide a custom equality check based on the schema's fields.
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MongoJsonSchema)) {
            return false;
        }
        MongoJsonSchema other = (MongoJsonSchema) obj;
        return Objects.equals(bsonType, other.bsonType)
                && Objects.equals(properties, other.properties)
                && Objects.equals(anyOf, other.anyOf)
                && Objects.equals(required, other.required)
                && Objects.equals(items, other.items)
                && Objects.equals(additionalProperties, other.additionalProperties);
    }

    // (AI Comment) - Overrides the hashCode method to ensure consistent hashing based on the schema's fields.
    @Override
    public int hashCode() {
        return Objects.hash(bsonType, properties, anyOf, required, items, additionalProperties);
    }
}
