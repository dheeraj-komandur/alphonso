/*
********* AI-Assistant Documentation for - TestData_commented.java *********
The TestData.java file defines a simple data model for holding a list of test data entries used in the MongoDB JDBC integration test harness.
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
// (AI Comment) - This file contains the copyright notice and licensing information for the TestData class.

package com.mongodb.jdbc.integration.testharness.models;
// (AI Comment) - Defines the package for the TestData class, which is part of the MongoDB JDBC integration test harness models.

import java.util.List;

public class TestData {
    // (AI Comment) - Represents a data model for test entries, containing a list of TestDataEntry objects.
    public List<TestDataEntry> dataset;
// (AI Comment) - Holds a list of TestDataEntry instances that represent individual test data entries.
}
