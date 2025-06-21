/*
********* AI-Assistant Documentation for - TestData_commented.java *********
The 'TestData.java' file defines a model class for holding a collection of test data entries, which are utilized in integration testing scenarios for MongoDB JDBC interactions.
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

// (AI Comment) - Represents a collection of test data entries used for integration testing.
public class TestData {
    // (AI Comment) - Holds a list of TestDataEntry objects representing individual entries in the dataset.
    public List<TestDataEntry> dataset;
}
