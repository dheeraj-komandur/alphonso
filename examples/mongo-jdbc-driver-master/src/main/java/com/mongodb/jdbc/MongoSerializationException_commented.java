/*
********* AI-Assistant Documentation for - MongoSerializationException_commented.java *********
The 'MongoSerializationException.java' file defines a custom exception for handling serialization errors in MongoDB operations, providing constructors for detailed error reporting.
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

// (AI Comment) - Custom exception class for handling serialization errors specific to MongoDB operations, extending the base Exception class.
package com.mongodb.jdbc;

public class MongoSerializationException extends Exception {
    // (AI Comment) - Constructor that accepts a message string to describe the exception.
    public MongoSerializationException(String message) {
        super(message);
    }

    // (AI Comment) - Constructor that accepts a message string and a Throwable cause for more detailed exception chaining.
    public MongoSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
