/*
********* AI-Assistant Documentation for - MongoSQLException_commented.java *********
The 'MongoSQLException.java' file defines a custom exception class for MongoDB SQL operations, allowing for more specific error handling in applications that interact with MongoDB databases.
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

// (AI Comment) - Custom exception class for handling MongoDB-related SQL exceptions, extending the base Exception class.
public class MongoSQLException extends Exception {
    // (AI Comment) - Constructor that accepts a message string to describe the exception.
    public MongoSQLException(String message) {
        super(message);
    }

    // (AI Comment) - Constructor that accepts a message string and a Throwable cause for the exception.
    public MongoSQLException(String message, Throwable cause) {
        super(message, cause);
    }
}
