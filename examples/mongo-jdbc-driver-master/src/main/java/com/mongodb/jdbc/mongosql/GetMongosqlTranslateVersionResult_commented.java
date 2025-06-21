/*
********* AI-Assistant Documentation for - GetMongosqlTranslateVersionResult_commented.java *********
This file defines the GetMongosqlTranslateVersionResult class, which encapsulates the version information returned from a MongoSQL translation request. It provides mechanisms for BSON serialization and a string representation in JSON format.
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

// (AI Comment) - Defines the package for MongoDB JDBC MongoSQL translation results.
package com.mongodb.jdbc.mongosql;

import static com.mongodb.jdbc.utils.BsonUtils.JSON_WRITER_NO_INDENT_SETTINGS;

import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.utils.BsonUtils;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents the result of a MongoSQL translation version request, encapsulating the version information.
public class GetMongosqlTranslateVersionResult {

    // (AI Comment) - Codec for serializing and deserializing GetMongosqlTranslateVersionResult instances.
    private static final Codec<GetMongosqlTranslateVersionResult> CODEC =
            MongoDriver.getCodecRegistry().get(GetMongosqlTranslateVersionResult.class);

    // (AI Comment) - Holds the version string of the MongoSQL translation result.
    @BsonProperty("version")
    public final String version;

    // (AI Comment) - Constructor that initializes the version field with the provided value.
    @BsonCreator
    public GetMongosqlTranslateVersionResult(@BsonProperty("version") String version) {
        this.version = version;
    }

    // (AI Comment) - Returns a string representation of the GetMongosqlTranslateVersionResult instance in JSON format.
    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
