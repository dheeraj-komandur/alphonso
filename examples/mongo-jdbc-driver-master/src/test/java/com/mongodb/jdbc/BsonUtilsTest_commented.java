/*
********* AI-Assistant Documentation for - BsonUtilsTest_commented.java *********
The 'BsonUtilsTest.java' file contains unit tests for the BsonUtils class, focusing on the serialization and deserialization of BsonDocuments. It ensures that the methods function correctly and handle edge cases, such as null inputs, thereby contributing to the robustness of the MongoDB JDBC implementation.
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

package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import com.mongodb.jdbc.utils.BsonUtils;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.junit.jupiter.api.Test;

// (AI Comment) - Test class for BsonUtils, validating serialization and deserialization of BsonDocuments.
class BsonUtilsTest {

    // (AI Comment) - Tests the serialization and deserialization process of a BsonDocument, ensuring integrity of data.
    @Test
    void testSerializeDeserialize() throws MongoSerializationException {
        BsonDocument originalDoc =
                new BsonDocument("name", new BsonString("Test"))
                        .append("value", new BsonInt32(123))
                        .append("nested", new BsonDocument("key", new BsonString("value")));

        byte[] serialized = BsonUtils.serialize(originalDoc);
        assertNotNull(serialized, "Serialized byte array should not be null");
        assertTrue(serialized.length > 0, "Serialized byte array should have content");

        BsonDocument deserializedDoc = BsonUtils.deserialize(serialized);
        assertNotNull(deserializedDoc, "Deserialized document should not be null");
        assertEquals(
                originalDoc,
                deserializedDoc,
                "Original and deserialized documents should be equal");
    }

    // (AI Comment) - Tests that serializing a null BsonDocument throws a MongoSerializationException as expected.
    @Test
    void testSerializeNullDocument() {
        assertThrows(
                MongoSerializationException.class,
                () -> BsonUtils.serialize(null),
                "Serializing a null document should throw MongoSerializationException");
    }
}
