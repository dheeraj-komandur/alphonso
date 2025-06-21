/*
********* AI-Assistant Documentation for - TestEntry_commented.java *********
The 'TestEntry.java' file defines a data model for integration tests in the MongoDB JDBC framework, encapsulating attributes related to test cases, including SQL queries, expected results, and metadata for validation.
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

package com.mongodb.jdbc.integration.testharness.models;

import java.util.List;
import java.util.Map;

// (AI Comment) - Represents a test entry for database integration tests, encapsulating various attributes related to the test case.
public class TestEntry {
    // (AI Comment) - A description of the test case.
    public String description;
    // (AI Comment) - The database name associated with the test case.
    public String db;
    // (AI Comment) - The SQL query to be executed in the test.
    public String sql;
    // (AI Comment) - A list of meta functions related to the test case.
    public List<Object> meta_function;
    // (AI Comment) - Reason for skipping the test, if applicable.
    public String skip_reason;
    // (AI Comment) - Expected number of rows returned by the query.
    public Integer row_count;
    // (AI Comment) - Indicates if the row count should be greater than or equal to the expected value.
    public Boolean row_count_gte;
    // (AI Comment) - Indicates if the test should be executed in an ordered manner.
    public Boolean ordered;
    // (AI Comment) - Names of columns that are expected to have duplicate values.
    public List<String> duplicated_columns_names;
    // (AI Comment) - Expected results of the test case.
    public List<Object> expected_result;
    // (AI Comment) - Expected results in extended JSON format.
    public List<Map<String, Object>> expected_result_extended_json;
    // (AI Comment) - Expected SQL types for the columns.
    public List<String> expected_sql_type;
    // (AI Comment) - Expected BSON types for the columns.
    public List<String> expected_bson_type;
    // (AI Comment) - Expected catalog names for the columns.
    public List<String> expected_catalog_name;
    // (AI Comment) - Expected class names for the columns.
    public List<String> expected_column_class_name;
    // (AI Comment) - Expected labels for the columns.
    public List<String> expected_column_label;
    // (AI Comment) - Expected display sizes for the columns.
    public List<Integer> expected_column_display_size;
    // (AI Comment) - Expected precision for the columns.
    public List<Integer> expected_precision;
    // (AI Comment) - Expected scale for the columns.
    public List<Integer> expected_scale;
    // (AI Comment) - Expected schema names for the columns.
    public List<String> expected_schema_name;
    // (AI Comment) - Indicates if the column is expected to be auto-incremented.
    public List<Boolean> expected_is_auto_increment;
    // (AI Comment) - Indicates if the column is expected to be case-sensitive.
    public List<Boolean> expected_is_case_sensitive;
    // (AI Comment) - Indicates if the column is expected to represent currency.
    public List<Boolean> expected_is_currency;
    // (AI Comment) - Indicates if the column is expected to be definitely writable.
    public List<Boolean> expected_is_definitely_writable;
    // (AI Comment) - Indicates the expected nullability of the column.
    public List<String> expected_is_nullable;
    // (AI Comment) - Indicates if the column is expected to be read-only.
    public List<Boolean> expected_is_read_only;
    // (AI Comment) - Indicates if the column is expected to be searchable.
    public List<Boolean> expected_is_searchable;
    // (AI Comment) - Indicates if the column is expected to be signed.
    public List<Boolean> expected_is_signed;
    // (AI Comment) - Indicates if the column is expected to be writable.
    public List<Boolean> expected_is_writable;
}
