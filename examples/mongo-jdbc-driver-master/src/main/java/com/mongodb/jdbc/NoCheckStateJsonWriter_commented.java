/*
********* AI-Assistant Documentation for - NoCheckStateJsonWriter_commented.java *********
This file contains the NoCheckStateJsonWriter class, which extends JsonWriter to allow writing JSON values without validation. It is designed for scenarios where BSON values need to be written directly, bypassing the usual state checks.
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

import java.io.Writer;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

// (AI Comment) - Describes the purpose of NoCheckStateJsonWriter, which allows writing any JSON value without validation, useful for Bson Values.
/**
 * NoCheckStateJsonWriter will allow writing of any Json value. It does not validate it is
 * constructing a valid document. Useful for writing Bson Values such as a BsonArray.
 */
public class NoCheckStateJsonWriter extends JsonWriter {

    // (AI Comment) - Constructor initializes the NoCheckStateJsonWriter with a Writer and JsonWriterSettings, passing them to the superclass.
    public NoCheckStateJsonWriter(Writer writer, JsonWriterSettings settings) {
        super(writer, settings);
    }

    @Override
    // (AI Comment) - Overrides the checkState method to always return true, allowing any state for JSON writing.
    protected boolean checkState(State[] validStates) {
        return true;
    }
}
