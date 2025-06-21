/*
********* AI-Assistant Documentation for - GetNamespacesResult_commented.java *********
The GetNamespacesResult.java file defines data structures for handling the results of a GetNamespaces operation in the MongoDB JDBC driver. It encapsulates a list of namespaces, each represented by a database and collection name, and provides methods for serialization to JSON format.
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

// (AI Comment) - Defines the package for MongoDB JDBC SQL integration.
package com.mongodb.jdbc.mongosql;

import static com.mongodb.jdbc.utils.BsonUtils.JSON_WRITER_NO_INDENT_SETTINGS;

import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.utils.BsonUtils;
import java.util.List;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents the result of a GetNamespaces operation, encapsulating a list of Namespace objects.
public class GetNamespacesResult {

    // (AI Comment) - Codec for serializing and deserializing GetNamespacesResult objects.
    private static final Codec<GetNamespacesResult> CODEC =
            MongoDriver.getCodecRegistry().get(GetNamespacesResult.class);

    // (AI Comment) - List of namespaces returned by the GetNamespaces operation.
    @BsonProperty("namespaces")
    public final List<Namespace> namespaces;

    // (AI Comment) - Constructor that initializes the namespaces list.
    @BsonCreator
    public GetNamespacesResult(@BsonProperty("namespaces") List<Namespace> namespaces) {
        this.namespaces = namespaces;
    }

    // (AI Comment) - Represents a single namespace consisting of a database and collection.
    public static class Namespace {
        // (AI Comment) - Codec for serializing and deserializing Namespace objects.
        private static final Codec<Namespace> CODEC =
                MongoDriver.getCodecRegistry().get(Namespace.class);

        // (AI Comment) - Database name associated with the namespace.
        @BsonProperty("database")
        public final String database;
        // (AI Comment) - Collection name associated with the namespace.

        @BsonProperty("collection")
        public final String collection;

        // (AI Comment) - Constructor that initializes the database and collection names.
        @BsonCreator
        public Namespace(
                @BsonProperty("database") String database,
                @BsonProperty("collection") String collection) {
            this.database = database;
            this.collection = collection;
        }

        // (AI Comment) - Returns a string representation of the Namespace object in JSON format.
        @Override
        public String toString() {
            return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
        }
    }

    // (AI Comment) - Returns a string representation of the GetNamespacesResult object in JSON format.
    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
