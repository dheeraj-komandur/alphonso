/*
********* AI-Assistant Documentation for - TranslateResult_commented.java *********
The 'TranslateResult.java' file defines a class that encapsulates the results of a translation operation in a MongoDB context, including details such as the target database, collection, processing pipeline, schema, and selection order. It provides a constructor for initialization and overrides the toString method for JSON representation.
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

package com.mongodb.jdbc.mongosql;

import static com.mongodb.jdbc.utils.BsonUtils.JSON_WRITER_NO_INDENT_SETTINGS;

import com.mongodb.jdbc.JsonSchema;
import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.MongoJsonSchema;
import com.mongodb.jdbc.utils.BsonUtils;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents the result of a translation operation, encapsulating target database, collection, pipeline, schema, and selection order.
public class TranslateResult {

    private static final Codec<TranslateResult> CODEC =
            MongoDriver.getCodecRegistry().get(TranslateResult.class);

    public final String targetDb;
    public final String targetCollection;
    public final List<BsonDocument> pipeline;
    public final MongoJsonSchema resultSetSchema;
    public final List<List<String>> selectOrder;

    // (AI Comment) - Constructor that initializes the TranslateResult with target database, collection, pipeline, result set schema, and selection order, converting the schema if necessary.
    @BsonCreator
    public TranslateResult(
            @BsonProperty("target_db") String targetDb,
            @BsonProperty("target_collection") String targetCollection,
            @BsonProperty("pipeline") List<BsonDocument> pipeline,
            @BsonProperty("result_set_schema") JsonSchema resultSetSchema,
            @BsonProperty("select_order") List<List<String>> selectOrder) {
        this.targetDb = targetDb;
        this.targetCollection = targetCollection;
        this.pipeline = pipeline;
        this.resultSetSchema =
                (resultSetSchema != null)
                        ? MongoJsonSchema.toSimplifiedMongoJsonSchema(resultSetSchema)
                        : null;
        this.selectOrder = selectOrder;
    }

    // (AI Comment) - Overrides the toString method to provide a JSON representation of the TranslateResult using the defined codec.
    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
