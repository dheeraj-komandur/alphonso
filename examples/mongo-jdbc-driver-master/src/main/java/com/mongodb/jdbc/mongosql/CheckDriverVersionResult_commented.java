/*
********* AI-Assistant Documentation for - CheckDriverVersionResult_commented.java *********
This file defines the CheckDriverVersionResult class, which encapsulates the result of checking the compatibility of the MongoDB driver version. It provides a constructor for initialization, a method for string representation, and utilizes BSON for serialization.
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
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents the result of checking the MongoDB driver version compatibility, encapsulating the compatibility status.
public class CheckDriverVersionResult {

    // (AI Comment) - Codec for serializing and deserializing CheckDriverVersionResult instances using BSON.
    private static final Codec<CheckDriverVersionResult> CODEC =
            MongoDriver.getCodecRegistry().get(CheckDriverVersionResult.class);

    // (AI Comment) - Indicates whether the driver version is compatible with the MongoDB server.
    @BsonProperty("compatible")
    public final Boolean compatible;

    // (AI Comment) - Constructor that initializes the compatibility status, defaulting to false if null.
    @BsonCreator
    public CheckDriverVersionResult(@BsonProperty("compatible") Boolean compatible) {
        this.compatible = (compatible != null) ? compatible : false;
    }

    // (AI Comment) - Returns a string representation of the CheckDriverVersionResult instance in BSON format.
    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
