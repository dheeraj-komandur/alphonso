/*
********* AI-Assistant Documentation for - BsonExplicitCursor_commented.java *********
The 'BsonExplicitCursor.java' file defines the BsonExplicitCursor class, which implements the MongoCursor interface to facilitate the creation of a cursor from a static list of BSON documents. This is particularly useful for testing purposes or scenarios where static results are needed.
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

import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonDocument;

// (AI Comment) - BsonExplicitCursor allows for creating an instance of MongoCursor from an explicit list of BSON docs. Useful for testing or for any place static results are necessary.
/**
 * BsonExplicitCursor allows for creating an instance of MongoCursor from an explicit list of BSON
 * docs. Useful for testing or for any place static results are necessary.
 */
// (AI Comment) - The constructor initializes the cursor with a list of BSON documents.
public class BsonExplicitCursor implements MongoCursor<BsonDocument> {
    private List<BsonDocument> docs;
    private int rowNum = 0;

    public static final BsonExplicitCursor EMPTY_CURSOR = new BsonExplicitCursor(new ArrayList<>());

    public BsonExplicitCursor(List<BsonDocument> docs) {
        this.docs = docs;
    }

    // (AI Comment) - Closes the cursor. No operation is performed as this is a static cursor.
    @Override
    public void close() {}

    // (AI Comment) - Returns the server address as a static value, indicating a local server.
    @Override
    public ServerAddress getServerAddress() {
        return new ServerAddress("127.0.0.1");
    }

    // (AI Comment) - Returns null for the server cursor as this cursor does not interact with a live server.
    @Override
    public ServerCursor getServerCursor() {
        return null;
    }

    // (AI Comment) - Checks if there are more documents to iterate over in the cursor.
    @Override
    public boolean hasNext() {
        return rowNum < docs.size();
    }

    // (AI Comment) - Returns the next BSON document in the cursor and increments the row number.
    @Override
    public BsonDocument next() {
        return docs.get(rowNum++);
    }

    // (AI Comment) - Returns the number of documents remaining in the cursor.
    @Override
    public int available() {
        return docs.size() - rowNum;
    }

    // (AI Comment) - Attempts to return the next BSON document if available; otherwise, returns null.
    @Override
    public BsonDocument tryNext() {
        if (hasNext()) {
            return next();
        }
        return null;
    }
}
