/*
********* AI-Assistant Documentation for - QueryDiagnostics_commented.java *********
The 'QueryDiagnostics.java' file defines a class that encapsulates diagnostic information for SQL queries executed against a MongoDB database. It provides methods to set and retrieve various attributes related to the query, including the SQL string, query catalog, result set schema, and processing pipeline.
*/

/*
 * Copyright 2025-present MongoDB, Inc.
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

package com.mongodb.jdbc.logging;

import static com.mongodb.jdbc.utils.BsonUtils.JSON_WRITER_NO_INDENT_SETTINGS;

import com.mongodb.jdbc.MongoDriver;
import com.mongodb.jdbc.MongoJsonSchema;
import com.mongodb.jdbc.utils.BsonUtils;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.pojo.annotations.BsonProperty;

// (AI Comment) - Represents diagnostic information for SQL queries, including the SQL string, query catalog, result set schema, and processing pipeline.
public class QueryDiagnostics {
    private static final Codec<QueryDiagnostics> CODEC =
            MongoDriver.getCodecRegistry().get(QueryDiagnostics.class);

    @BsonProperty private String sqlQuery;
    @BsonProperty private BsonDocument queryCatalog;
    @BsonProperty private MongoJsonSchema resultSetSchema;
    @BsonProperty private BsonArray pipeline;

    // (AI Comment) - Sets the SQL query string for this diagnostic object.
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    // (AI Comment) - Sets the query catalog as a BSON document for this diagnostic object.
    public void setQueryCatalog(BsonDocument queryCatalog) {
        this.queryCatalog = queryCatalog;
    }

    // (AI Comment) - Sets the result set schema as a MongoJsonSchema for this diagnostic object.
    public void setResultSetSchema(MongoJsonSchema resultSetSchema) {
        this.resultSetSchema = resultSetSchema;
    }

    // (AI Comment) - Sets the processing pipeline as a BSON array for this diagnostic object.
    public void setPipeline(BsonArray pipeline) {
        this.pipeline = pipeline;
    }

    // (AI Comment) - Returns the SQL query string associated with this diagnostic object.
    public String getSqlQuery() {
        return sqlQuery;
    }

    // (AI Comment) - Returns the query catalog as a BSON document associated with this diagnostic object.
    public BsonDocument getQueryCatalog() {
        return queryCatalog;
    }

    // (AI Comment) - Returns the result set schema as a MongoJsonSchema associated with this diagnostic object.
    public MongoJsonSchema getResultSetSchema() {
        return resultSetSchema;
    }

    // (AI Comment) - Returns the processing pipeline as a BSON array associated with this diagnostic object.
    public BsonArray getPipeline() {
        return pipeline;
    }

    // (AI Comment) - Returns a string representation of this diagnostic object using BSON utilities.
    @Override
    public String toString() {
        return BsonUtils.toString(CODEC, this, JSON_WRITER_NO_INDENT_SETTINGS);
    }
}
