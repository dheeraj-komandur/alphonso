/*
********* AI-Assistant Documentation for - MongoJsonSchemaTest_commented.java *********
This file contains unit tests for the deserialization and simplification of JSON schemas in the MongoDB JDBC context. It validates that schemas returned from SQL commands are correctly processed and that empty schemas are handled appropriately.
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

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriterSettings;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

// (AI Comment) - Test the deserialization and simplification of JsonSchema returned by a sqlgetschema command.
/** Test the deserialization and simplifacation of JsonSchema returned by a sqlgetschema command. */
public class MongoJsonSchemaTest {
    static final CodecRegistry REGISTRY =
            fromProviders(
                    new BsonValueCodecProvider(),
                    new ValueCodecProvider(),
                    PojoCodecProvider.builder().automatic(true).build());
    static final Codec<MongoJsonSchema> MONGO_JSON_SCHEMA_CODEC =
            REGISTRY.get(MongoJsonSchema.class);
    static final Codec<MongoVersionedJsonSchema> MONGO_VERSIONED_JSON_SCHEMA_CODEC =
            REGISTRY.get(MongoVersionedJsonSchema.class);
    static final Codec<JsonSchema> JSON_SCHEMA_CODEC = REGISTRY.get(JsonSchema.class);

    // (AI Comment) - Runs integration tests by comparing generated schemas against expected output from files in specified directories.
    @TestFactory
    Collection<DynamicTest> runIntegrationTests() throws SQLException {
        ClassLoader classLoader = getClass().getClassLoader();
        File input = new File(classLoader.getResource("mongoJsonSchemaTest/input").getFile());
        assertTrue(input.isDirectory(), input.getPath() + " is not a directory.");
        File expectedOutput =
                new File(classLoader.getResource("mongoJsonSchemaTest/expectedOutput").getFile());
        assertTrue(expectedOutput.isDirectory(), expectedOutput.getPath() + " is not a directory.");
        List<DynamicTest> dynamicTests = new ArrayList<>();
        for (File testEntry : input.listFiles()) {
            File output = new File(expectedOutput.getAbsoluteFile() + "/" + testEntry.getName());
            dynamicTests.add(
                    DynamicTest.dynamicTest(
                            testEntry.getName(),
                            () -> {
                                System.out.println(
                                        "Comparing "
                                                + testEntry.getName()
                                                + " with "
                                                + output.getName());
                                testDeserializeAndSimplifySchema(testEntry, output);
                            }));
        }
        return dynamicTests;
    }

    // (AI Comment) - Tests the deserialization of empty schemas and verifies that encoding and decoding produce consistent results.
    @Test
    public void testEmptySchema() throws Exception {
        JsonWriterSettings settings = JsonWriterSettings.builder().indent(true).build();

        // (AI Comment) - Deserializes empty documents.
        // Deserializes empty documents
        Codec[] schemaCodecs = {
            JSON_SCHEMA_CODEC, MONGO_JSON_SCHEMA_CODEC, MONGO_VERSIONED_JSON_SCHEMA_CODEC
        };
        for (Codec codec : schemaCodecs) {
            Class encoderClass = codec.getEncoderClass();
            // (AI Comment) - Encode an 'empty' object using the default constructor with no arguments for the class associated to the codec.
            // Encode an "emtpy" object using the default constructor with no arguments for the class associated to the codec
            BsonDocument docFromEmptyObj = new BsonDocument();
            BsonWriter writer = new BsonDocumentWriter(docFromEmptyObj);
            codec.encode(
                    writer,
                    encoderClass.getConstructor().newInstance(),
                    EncoderContext.builder().build());
            writer.flush();

            try (JsonReader reader = new JsonReader(new StringReader("{}"))) {
                // (AI Comment) - Decode the empty document.
                // Decode the empty document
                Object encodedObj = codec.decode(reader, DecoderContext.builder().build());
                assertEquals(encoderClass, encodedObj.getClass());

                // Re-encode the decoded schema and check its content, verify that it matches a new "empty" instance
                BsonDocument docFromDecodedEmptyJson = new BsonDocument();
                writer = new BsonDocumentWriter(docFromDecodedEmptyJson);
                codec.encode(writer, encodedObj, EncoderContext.builder().build());
                writer.flush();

                assertEquals(
                        docFromEmptyObj.toJson(settings), docFromDecodedEmptyJson.toJson(settings));
            }
        }
    }

    // (AI Comment) - Deserializes input and output schemas from files, transforms the input schema, and asserts equality with the expected output schema.
    private void testDeserializeAndSimplifySchema(File input, File output)
            throws FileNotFoundException {
        JsonSchema in_schema = null;
        MongoJsonSchema out_schema = null;
        // Decode the input
        try (JsonReader reader = new JsonReader(new FileReader(input))) {
            in_schema = JSON_SCHEMA_CODEC.decode(reader, DecoderContext.builder().build());
        }
        // (AI Comment) - Decode the expected output as JsonSchema to ensure no simplification occurs except necessary transformations.
        // Decode the expected out as jsonSchema to make sure that no simplification is happening
        // except the transformation from String to Set<String> if necessary for bsonType.
        try (JsonReader reader = new JsonReader(new FileReader(output))) {
            out_schema = MONGO_JSON_SCHEMA_CODEC.decode(reader, DecoderContext.builder().build());
        }

        // (AI Comment) - Transform the MongoJsonSchema to a JsonSchema for comparison.
        // Transform the mongoJsonSchema to a JsonSchema for comparing each other
        MongoJsonSchema simplifiedSchema = MongoJsonSchema.toSimplifiedMongoJsonSchema(in_schema);

        assertEquals(simplifiedSchema, out_schema);
    }
}
