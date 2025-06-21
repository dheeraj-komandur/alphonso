/*
********* AI-Assistant Documentation for - MongoResultSetMetaDataTest_commented.java *********
This file contains unit tests for the 'MongoResultSetMetaData' class, ensuring its methods function correctly and return expected results. It validates various properties of the result set metadata, including column counts, names, types, and nullability, using the JUnit testing framework.
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

// (AI Comment) - Defines the package for MongoDB JDBC related classes.
package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.BsonValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

// (AI Comment) - Test class for MongoResultSetMetaData, extending MongoMock to utilize mock functionalities.
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoResultSetMetaDataTest extends MongoMock {
    private static MongoResultSetMetaData resultSetMetaData;

    // (AI Comment) - Static block to initialize MongoResultSetMetaData with a generated schema and other parameters.
    static {
        try {
            resultSetMetaData =
                    new MongoResultSetMetaData(
                            generateMongoJsonSchema(),
                            null,
                            true,
                            mongoConnection.getLogger(),
                            0,
                            null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // (AI Comment) - Initializes mocks before all tests are run.
    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // (AI Comment) - Tests the getColumnCount method to ensure it returns the correct number of columns.
    @Test
    void testGetColumnCount() throws SQLException {
        assertEquals(12, MongoResultSetMetaDataTest.resultSetMetaData.getColumnCount());
    }

    // (AI Comment) - Tests the order of metadata fields based on sorting options, verifying expected column names.
    @Test
    void testMetadataFieldsOrder() throws SQLException {

        // Verify that the columns are sorted alphabetically when the sortFieldsAlphabetically is true and that the original order is kept when it's false.
        String[] expected_sorted_columns =
                new String[] {
                    "a",
                    "binary",
                    "dup",
                    "str",
                    "anyOfStrOrInt",
                    "b",
                    "c",
                    "d",
                    "doc",
                    "dup",
                    "null",
                    "vec"
                };
        String[] expected_original_columns =
                new String[] {
                    "a",
                    "binary",
                    "str",
                    "dup",
                    "c",
                    "anyOfStrOrInt",
                    "d",
                    "b",
                    "vec",
                    "null",
                    "doc",
                    "dup"
                };
        String[] expected_select_order_columns =
                new String[] {
                    "binary",
                    "anyOfStrOrInt",
                    "b",
                    "dup",
                    "c",
                    "d",
                    "doc",
                    "null",
                    "vec",
                    "a",
                    "dup",
                    "str"
                };
        List<List<String>> selectOrder =
                Arrays.asList(
                        Arrays.asList("", "binary"),
                        Arrays.asList("foo", "anyOfStrOrInt"),
                        Arrays.asList("foo", "b"),
                        Arrays.asList("foo", "dup"),
                        Arrays.asList("foo", "c"),
                        Arrays.asList("foo", "d"),
                        Arrays.asList("foo", "doc"),
                        Arrays.asList("foo", "null"),
                        Arrays.asList("foo", "vec"),
                        Arrays.asList("", "a"),
                        Arrays.asList("", "dup"),
                        Arrays.asList("", "str"));

        MongoJsonSchema schema = generateMongoJsonSchema();
        MongoResultSetMetaData unsortedMedata =
                new MongoResultSetMetaData(
                        schema, null, false, mongoConnection.getLogger(), 0, null);
        MongoResultSetMetaData sortedMedata =
                new MongoResultSetMetaData(
                        schema, null, true, mongoConnection.getLogger(), 0, null);
        MongoResultSetMetaData selectOrderedMetadata =
                new MongoResultSetMetaData(
                        schema, selectOrder, false, mongoConnection.getLogger(), 0, null);

        assertEquals(
                expected_original_columns.length,
                unsortedMedata.getColumnCount(),
                "The number of expected columns doesn't match the actual number of columns");
        for (int i = 0; i < unsortedMedata.getColumnCount(); i++) {
            assertEquals(expected_original_columns[i], unsortedMedata.getColumnName(i + 1));
        }

        assertEquals(
                expected_sorted_columns.length,
                sortedMedata.getColumnCount(),
                "The number of expected columns doesn't match the actual number of columns");
        for (int i = 0; i < sortedMedata.getColumnCount(); i++) {
            assertEquals(expected_sorted_columns[i], sortedMedata.getColumnName(i + 1));
        }

        assertEquals(
                expected_select_order_columns.length,
                selectOrderedMetadata.getColumnCount(),
                "The number of expected columns doesn't match the actual number of columns");
        for (int i = 0; i < selectOrderedMetadata.getColumnCount(); i++) {
            assertEquals(
                    expected_select_order_columns[i], selectOrderedMetadata.getColumnName(i + 1));
        }
    }

    // (AI Comment) - Tests retrieval of catalog and schema names, expecting empty strings.
    @Test
    void testGetCatalogAndSchemaName() throws SQLException {
        assertEquals("", resultSetMetaData.getCatalogName(DOUBLE_COL));
        assertEquals("", resultSetMetaData.getSchemaName(DOUBLE_COL));
    }

    // (AI Comment) - Tests retrieval of column names for various columns, ensuring correct mappings.
    @Test
    void testGetColumnName() throws SQLException {
        assertEquals(DOUBLE_COL_LABEL, resultSetMetaData.getColumnName(DOUBLE_COL));
        assertEquals(STRING_COL_LABEL, resultSetMetaData.getColumnName(STRING_COL));
        assertEquals(
                ANY_OF_INT_STRING_COL_LABEL,
                resultSetMetaData.getColumnName(ANY_OF_INT_STRING_COL));
        assertEquals(INT_NULLABLE_COL_LABEL, resultSetMetaData.getColumnName(INT_OR_NULL_COL));
        assertEquals(INT_COL_LABEL, resultSetMetaData.getColumnName(INT_COL));
        assertEquals(ANY_COL_LABEL, resultSetMetaData.getColumnName(ANY_COL));
        assertEquals(NULL_COL_LABEL, resultSetMetaData.getColumnName(NULL_COL));
        assertEquals(ARRAY_COL_LABEL, resultSetMetaData.getColumnName(ARRAY_COL));
        assertEquals(DOC_COL_LABEL, resultSetMetaData.getColumnName(DOC_COL));
        assertEquals(BOT_DUP_COL_LABEL, resultSetMetaData.getColumnName(BOT_DUP_COL));
        assertEquals(FOO_DUP_COL_LABEL, resultSetMetaData.getColumnName(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column labels, ensuring they match expected values.
    @Test
    void testGetColumnLabel() throws SQLException {
        assertEquals(DOUBLE_COL_LABEL, resultSetMetaData.getColumnLabel(DOUBLE_COL));
        assertEquals(STRING_COL_LABEL, resultSetMetaData.getColumnLabel(STRING_COL));
        assertEquals(
                ANY_OF_INT_STRING_COL_LABEL,
                resultSetMetaData.getColumnLabel(ANY_OF_INT_STRING_COL));
        assertEquals(INT_NULLABLE_COL_LABEL, resultSetMetaData.getColumnLabel(INT_OR_NULL_COL));
        assertEquals(INT_COL_LABEL, resultSetMetaData.getColumnLabel(INT_COL));
        assertEquals(ANY_COL_LABEL, resultSetMetaData.getColumnLabel(ANY_COL));
        assertEquals(NULL_COL_LABEL, resultSetMetaData.getColumnName(NULL_COL));
        assertEquals(ARRAY_COL_LABEL, resultSetMetaData.getColumnLabel(ARRAY_COL));
        assertEquals(DOC_COL_LABEL, resultSetMetaData.getColumnLabel(DOC_COL));
        assertEquals(BOT_DUP_COL_LABEL, resultSetMetaData.getColumnName(BOT_DUP_COL));
        assertEquals(FOO_DUP_COL_LABEL, resultSetMetaData.getColumnName(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of table names for various columns, expecting specific values.
    @Test
    void testGetTableName() throws SQLException {
        assertEquals("", resultSetMetaData.getTableName(DOUBLE_COL));
        assertEquals("", resultSetMetaData.getTableName(STRING_COL));
        assertEquals("", resultSetMetaData.getTableName(BOT_DUP_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ANY_OF_INT_STRING_COL));
        assertEquals("foo", resultSetMetaData.getTableName(INT_OR_NULL_COL));
        assertEquals("foo", resultSetMetaData.getTableName(INT_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ANY_COL));
        assertEquals("foo", resultSetMetaData.getTableName(NULL_COL));
        assertEquals("foo", resultSetMetaData.getTableName(ARRAY_COL));
        assertEquals("foo", resultSetMetaData.getTableName(DOC_COL));
        assertEquals("foo", resultSetMetaData.getTableName(FOO_DUP_COL));
    }

    // (AI Comment) - Tests case sensitivity of columns, verifying expected boolean results.
    @Test
    void testIsCaseSensitive() throws SQLException {
        assertEquals(false, resultSetMetaData.isCaseSensitive(DOUBLE_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(STRING_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_OF_INT_STRING_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_OR_NULL_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(INT_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(ANY_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(NULL_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(ARRAY_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(DOC_COL));
        assertEquals(true, resultSetMetaData.isCaseSensitive(BOT_DUP_COL));
        assertEquals(false, resultSetMetaData.isCaseSensitive(FOO_DUP_COL));
    }

    // (AI Comment) - Tests nullability of columns, ensuring correct nullable states are returned.
    @Test
    void testIsNullable() throws SQLException {
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(DOUBLE_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(STRING_COL));
        assertEquals(
                ResultSetMetaData.columnNoNulls,
                resultSetMetaData.isNullable(ANY_OF_INT_STRING_COL));
        assertEquals(
                ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(INT_OR_NULL_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(INT_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(ANY_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(NULL_COL));
        assertEquals(ResultSetMetaData.columnNoNulls, resultSetMetaData.isNullable(ARRAY_COL));
        assertEquals(ResultSetMetaData.columnNoNulls, resultSetMetaData.isNullable(DOC_COL));
        assertEquals(ResultSetMetaData.columnNullable, resultSetMetaData.isNullable(BOT_DUP_COL));
        assertEquals(ResultSetMetaData.columnNoNulls, resultSetMetaData.isNullable(FOO_DUP_COL));
    }

    // (AI Comment) - Tests signed status of columns, verifying expected boolean results.
    @Test
    void testIsSigned() throws SQLException {
        assertEquals(true, resultSetMetaData.isSigned(DOUBLE_COL));
        assertEquals(false, resultSetMetaData.isSigned(STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_OF_INT_STRING_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_OR_NULL_COL));
        assertEquals(true, resultSetMetaData.isSigned(INT_COL));
        assertEquals(true, resultSetMetaData.isSigned(ANY_COL));
        assertEquals(false, resultSetMetaData.isSigned(NULL_COL));
        assertEquals(false, resultSetMetaData.isSigned(ARRAY_COL));
        assertEquals(false, resultSetMetaData.isSigned(DOC_COL));
        assertEquals(false, resultSetMetaData.isSigned(BOT_DUP_COL));
        assertEquals(true, resultSetMetaData.isSigned(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column display sizes, ensuring correct values are returned.
    @Test
    void testGetColumnDisplaySize() throws SQLException {
        assertEquals(15, resultSetMetaData.getColumnDisplaySize(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(STRING_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ANY_OF_INT_STRING_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INT_OR_NULL_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(INT_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ANY_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(NULL_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(ARRAY_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(DOC_COL));
        assertEquals(0, resultSetMetaData.getColumnDisplaySize(BOT_DUP_COL));
        assertEquals(10, resultSetMetaData.getColumnDisplaySize(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column precision, ensuring correct values are returned.
    @Test
    void testGetPrecision() throws SQLException {
        assertEquals(15, resultSetMetaData.getPrecision(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getPrecision(STRING_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ANY_OF_INT_STRING_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INT_OR_NULL_COL));
        assertEquals(10, resultSetMetaData.getPrecision(INT_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ANY_COL));
        assertEquals(0, resultSetMetaData.getPrecision(NULL_COL));
        assertEquals(0, resultSetMetaData.getPrecision(ARRAY_COL));
        assertEquals(0, resultSetMetaData.getPrecision(DOC_COL));
        assertEquals(0, resultSetMetaData.getPrecision(BOT_DUP_COL));
        assertEquals(10, resultSetMetaData.getPrecision(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column scale, ensuring correct values are returned.
    @Test
    void testGetScale() throws SQLException {
        assertEquals(15, resultSetMetaData.getScale(DOUBLE_COL));
        assertEquals(0, resultSetMetaData.getScale(STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(ANY_OF_INT_STRING_COL));
        assertEquals(0, resultSetMetaData.getScale(INT_OR_NULL_COL));
        assertEquals(0, resultSetMetaData.getScale(INT_COL));
        assertEquals(0, resultSetMetaData.getScale(ANY_COL));
        assertEquals(0, resultSetMetaData.getScale(NULL_COL));
        assertEquals(0, resultSetMetaData.getScale(ARRAY_COL));
        assertEquals(0, resultSetMetaData.getScale(DOC_COL));
        assertEquals(0, resultSetMetaData.getScale(BOT_DUP_COL));
        assertEquals(0, resultSetMetaData.getScale(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column types, ensuring correct SQL types are returned.
    @Test
    void testGetColumnType() throws SQLException {
        assertEquals(Types.DOUBLE, resultSetMetaData.getColumnType(DOUBLE_COL));
        assertEquals(Types.LONGVARCHAR, resultSetMetaData.getColumnType(STRING_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ANY_OF_INT_STRING_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INT_OR_NULL_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(INT_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ANY_COL));
        assertEquals(Types.NULL, resultSetMetaData.getColumnType(NULL_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(ARRAY_COL));
        assertEquals(Types.OTHER, resultSetMetaData.getColumnType(DOC_COL));
        assertEquals(Types.LONGVARCHAR, resultSetMetaData.getColumnType(BOT_DUP_COL));
        assertEquals(Types.INTEGER, resultSetMetaData.getColumnType(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column class names, ensuring correct class names are returned.
    @Test
    void testGetColumnTypeClassName() throws SQLException {
        assertEquals(double.class.getName(), resultSetMetaData.getColumnClassName(DOUBLE_COL));
        assertEquals(String.class.getName(), resultSetMetaData.getColumnClassName(STRING_COL));
        assertEquals(
                BsonValue.class.getName(),
                resultSetMetaData.getColumnClassName(ANY_OF_INT_STRING_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INT_OR_NULL_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(INT_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(ANY_COL));
        assertEquals(null, resultSetMetaData.getColumnClassName(NULL_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(ARRAY_COL));
        assertEquals(BsonValue.class.getName(), resultSetMetaData.getColumnClassName(DOC_COL));
        assertEquals(String.class.getName(), resultSetMetaData.getColumnClassName(BOT_DUP_COL));
        assertEquals(int.class.getName(), resultSetMetaData.getColumnClassName(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of column type names, ensuring correct type names are returned.
    @Test
    void testGetColumnTypeName() throws SQLException {
        assertEquals("double", resultSetMetaData.getColumnTypeName(DOUBLE_COL));
        assertEquals("string", resultSetMetaData.getColumnTypeName(STRING_COL));
        assertEquals("bson", resultSetMetaData.getColumnTypeName(ANY_OF_INT_STRING_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INT_OR_NULL_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(INT_COL));
        assertEquals("bson", resultSetMetaData.getColumnTypeName(ANY_COL));
        assertEquals("null", resultSetMetaData.getColumnTypeName(NULL_COL));
        assertEquals("array", resultSetMetaData.getColumnTypeName(ARRAY_COL));
        assertEquals("object", resultSetMetaData.getColumnTypeName(DOC_COL));
        assertEquals("string", resultSetMetaData.getColumnTypeName(BOT_DUP_COL));
        assertEquals("int", resultSetMetaData.getColumnTypeName(FOO_DUP_COL));
    }

    // (AI Comment) - Tests retrieval of data source names, ensuring correct values are returned and handles duplicates.
    @Test
    void testGetDatasource() throws Exception {
        assertEquals("", resultSetMetaData.getDatasource(DOUBLE_COL_LABEL));
        assertEquals("", resultSetMetaData.getDatasource(STRING_COL_LABEL));
        assertEquals("foo", resultSetMetaData.getDatasource(INT_NULLABLE_COL_LABEL));
        assertEquals("foo", resultSetMetaData.getDatasource(INT_COL_LABEL));
        assertEquals("foo", resultSetMetaData.getDatasource(ANY_COL_LABEL));
        assertEquals("foo", resultSetMetaData.getDatasource(NULL_COL_LABEL));
        assertEquals("foo", resultSetMetaData.getDatasource(ARRAY_COL_LABEL));
        assertEquals("foo", resultSetMetaData.getDatasource(DOC_COL_LABEL));
        // Duplicated column names fail
        assertThrows(Exception.class, () -> resultSetMetaData.getDatasource(FOO_DUP_COL_LABEL));
        assertThrows(Exception.class, () -> resultSetMetaData.getDatasource(BOT_DUP_COL_LABEL));
    }

    // (AI Comment) - Tests behavior when select order is empty, ensuring correct column counts are returned.
    @Test
    void testEmptySelectOrder() throws SQLException {
        MongoJsonSchema schema = generateMongoJsonSchema();

        MongoResultSetMetaData nullSelectOrderMetadata =
                new MongoResultSetMetaData(
                        schema, null, true, mongoConnection.getLogger(), 0, null);

        List<List<String>> emptySelectOrder = new ArrayList<>();
        MongoResultSetMetaData emptySelectOrderMetadata =
                new MongoResultSetMetaData(
                        schema, emptySelectOrder, true, mongoConnection.getLogger(), 0, null);

        assertEquals(12, nullSelectOrderMetadata.getColumnCount());
        assertEquals(12, emptySelectOrderMetadata.getColumnCount());
    }
}
