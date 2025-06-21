/*
********* AI-Assistant Documentation for - MongoStatementTest_commented.java *********
The 'MongoStatementTest.java' file contains unit tests for the 'MongoStatement' class, validating its behavior and interactions with the MongoDB database. It ensures that methods function correctly, handle exceptions appropriately, and manage resources effectively, contributing to the overall reliability of the MongoDB JDBC driver.
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

// (AI Comment) - Defines the package for MongoDB JDBC integration tests.
package com.mongodb.jdbc;

import static java.sql.Statement.CLOSE_CURRENT_RESULT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

// (AI Comment) - Test class for MongoStatement, extending MongoMock to utilize mock objects for testing database interactions.
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
class MongoStatementTest extends MongoMock {
    private static MongoStatement mongoStatement;

    // (AI Comment) - Static block to initialize MongoStatement with a mock connection and database before any tests run.
    static {
        try {
            mongoStatement = new MongoStatement(mongoConnection, database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // (AI Comment) - Initializes mock objects before all tests are executed.
    @BeforeAll
    protected void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // (AI Comment) - Sets up the test environment by resetting mock objects and initializing MongoStatement before each test.
    // Since MongoConnection cannot be created with its constructor, we have to use InjectionMocks Annotation and
    // create it during initiation. In order to reuse the same object for each test, we need to reset it before each test case.
    @BeforeEach
    void setupTest() throws NoSuchFieldException, SQLException {
        resetMockObjs();
        mongoConnection.clusterType = MongoConnection.MongoClusterType.AtlasDataFederation;
        mongoStatement = new MongoStatement(mongoConnection, database);
    }

    // (AI Comment) - Tests that an exception is thrown when attempting to create a statement after the connection is closed.
    void testExceptionAfterConnectionClosed(MongoConnectionTest.TestInterface ti)
            throws SQLException {
        // create statement after closed throws exception
        mongoStatement.close();
        assertThrows(SQLException.class, ti::test);
    }

    // (AI Comment) - Tests that no exception is thrown for a valid operation and verifies exception handling after connection closure.
    void testNoop(MongoConnectionTest.TestInterface ti) throws SQLException {
        assertDoesNotThrow(ti::test);
        testExceptionAfterConnectionClosed(ti::test);
    }

    // (AI Comment) - Interface to facilitate testing with a lambda expression for SQL exception handling.
    // to replace lambda as input in the testExceptionAfterConnectionClosed
    interface TestInterface {
        void test() throws SQLException;
    }

    // (AI Comment) - Tests the execution of a query that returns an empty result set, verifying metadata and exception handling.
    @Test
    void testExecuteQueryEmptyResult() throws SQLException {
        AtomicInteger rowCnt = new AtomicInteger();

        when(mongoCursor.hasNext()).thenAnswer(invocation -> rowCnt.get() < 0);

        when(mongoCursor.next())
                .thenAnswer(
                        invocation -> {
                            rowCnt.incrementAndGet();
                            return generateRow();
                        });

        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());

        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(12, metaData.getColumnCount());

        rs.next();
        assertThrows(
                SQLException.class,
                () -> {
                    rs.getInt(1);
                });
        assertFalse(rs.next());
        assertThrows(
                SQLException.class,
                () -> {
                    rs.getInt(1);
                });

        assertTrue(rs.isLast());
    }

    // (AI Comment) - Tests the execution of a query that returns results, validating the result set and its contents.
    @Test
    void testExecuteQuery() throws SQLException {
        AtomicInteger rowCnt = new AtomicInteger();
        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());
        when(mongoCursor.hasNext()).thenAnswer(invocation -> rowCnt.get() < 1);

        when(mongoCursor.next())
                .thenAnswer(
                        invocation -> {
                            rowCnt.incrementAndGet();
                            return generateRow();
                        });

        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        ResultSetMetaData metaData = rs.getMetaData();
        assertEquals(12, metaData.getColumnCount());
        // need to call next() first
        assertThrows(
                SQLException.class,
                () -> {
                    rs.getInt(1);
                });

        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1));
        assertEquals("a", rs.getString(4));
        assertFalse(rs.next());
        assertTrue(rs.isLast());
    }

    // (AI Comment) - Tests the behavior of closing an empty statement and verifies that subsequent close calls are no-ops.
    @Test
    void testCloseForEmptyStatement() throws SQLException {
        assertFalse(mongoStatement.isClosed());
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());

        // noop for second close()
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
    }

    // (AI Comment) - Tests the closing behavior of an executed statement and ensures that the result set is also closed.
    @Test
    void testCloseForExecutedStatement() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());

        assertFalse(mongoStatement.isClosed());
        ResultSet rs = mongoStatement.executeQuery("select * from test");
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
        assertTrue(rs.isClosed());

        // noop for second close()
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
    }

    // (AI Comment) - Tests the closing behavior when the statement is marked for closure on completion, ensuring proper resource management.
    @Test
    void testCloseOnCompletion() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());

        assertFalse(mongoStatement.isClosed());
        mongoStatement.closeOnCompletion = true;
        ResultSet rs = mongoStatement.executeQuery("select * from test");
        rs.close();
        assertTrue(rs.isClosed());
        assertTrue(mongoStatement.isClosed());

        // No-op since the statement has been closed
        // automatically when closing the resutlset
        mongoStatement.close();
        assertTrue(mongoStatement.isClosed());
    }

    // (AI Comment) - Tests the retrieval of the maximum field size, ensuring it returns the expected default value.
    @Test
    void testGetMaxFieldSize() throws SQLException {
        assertEquals(0, mongoStatement.getMaxFieldSize());
        testExceptionAfterConnectionClosed(() -> mongoStatement.setMaxFieldSize(0));
    }

    @Test
    void testSetMaxFieldoSize() throws SQLException {
        testNoop(() -> mongoStatement.setMaxFieldSize(0));
    }

    // (AI Comment) - Tests setting the maximum field size, ensuring no exceptions are thrown.
    @Test
    void testgetMaxRows() throws SQLException {
        assertEquals(0, mongoStatement.getMaxRows());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getMaxRows());
    }

    @Test
    void testSetMaxRows() throws SQLException {
        testNoop(() -> mongoStatement.setMaxRows(0));
    }
    // (AI Comment) - Tests retrieval of the maximum rows, ensuring it returns the expected default value.

    @Test
    void testSetEscapeProcessing() throws SQLException {
        testNoop(() -> mongoStatement.setEscapeProcessing(true));
    }

    @Test
    void testSetGetQueryTimeout() throws SQLException {
        int timeout = 123;
        mongoStatement.setQueryTimeout(timeout);
        // (AI Comment) - Tests setting the maximum rows, ensuring no exceptions are thrown.
        assertEquals(timeout, mongoStatement.getQueryTimeout());

        testExceptionAfterConnectionClosed(() -> mongoStatement.setQueryTimeout(timeout));
        testExceptionAfterConnectionClosed(() -> mongoStatement.getQueryTimeout());
    }

    @Test
    void testGetWarnings() throws SQLException {
        assertEquals(null, mongoStatement.getWarnings());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getWarnings());
    // (AI Comment) - Tests setting the cursor name, ensuring no exceptions are thrown.
    }

    @Test
    void testClearWarnings() throws SQLException {
        testNoop(() -> mongoStatement.clearWarnings());
    }

    @Test
    void testSetCursorName() throws SQLException {
        testNoop(() -> mongoStatement.setCursorName(""));
    // (AI Comment) - Tests retrieval of the result set, ensuring it returns the expected result after executing a query.
    }

    @Test
    void testGetResultSet() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());

        assertNull(mongoStatement.getResultSet());
        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        assertEquals(rs, mongoStatement.getResultSet());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getResultSet());
    }
    // (AI Comment) - Tests retrieval of the update count, ensuring it returns the expected default value.

    @Test
    void testGetUpdateCount() throws SQLException {
        assertEquals(-1, mongoStatement.getUpdateCount());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getUpdateCount());
    }

    @Test
    void testGetMoreResults() throws SQLException {
        assertEquals(false, mongoStatement.getMoreResults());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getMoreResults());
    }
    // (AI Comment) - Tests retrieval of more results, ensuring it returns the expected default value.

    @Test
    void testSetGetFetchSize() throws SQLException {
        assertThrows(SQLException.class, () -> mongoStatement.setFetchSize(-1));

        int fetchSize = 10;
        mongoStatement.setFetchSize(fetchSize);
        assertEquals(fetchSize, mongoStatement.getFetchSize());

        testExceptionAfterConnectionClosed(() -> mongoStatement.setFetchSize(0));
        testExceptionAfterConnectionClosed(() -> mongoStatement.getFetchSize());
    // (AI Comment) - Tests setting and getting the fetch size, ensuring proper exception handling.
    }

    @Test
    void testGetResultSetConcurrency() throws SQLException {
        assertEquals(ResultSet.CONCUR_READ_ONLY, mongoStatement.getResultSetConcurrency());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getResultSetConcurrency());
    }

    // (AI Comment) - Tests retrieval of the result set concurrency type, ensuring it returns the expected value.
    @Test
    void testGetResultSetType() throws SQLException {
        assertEquals(ResultSet.TYPE_FORWARD_ONLY, mongoStatement.getResultSetType());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getResultSetType());
    // (AI Comment) - Tests retrieval of the connection associated with the statement, ensuring it returns the expected connection.
    }

    @Test
    void testGetConnection() throws SQLException {
        assertEquals(mongoConnection, mongoStatement.getConnection());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getConnection());
    // (AI Comment) - Tests retrieval of more results with instructions, ensuring proper closure of the result set.
    }

    @Test
    void testGetMoreResultsWithInstructions() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());

        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        assertFalse(rs.isClosed());
        mongoStatement.getMoreResults(CLOSE_CURRENT_RESULT);
        assertTrue(rs.isClosed());

        testExceptionAfterConnectionClosed(
                () -> mongoStatement.getMoreResults(CLOSE_CURRENT_RESULT));
    }

    // (AI Comment) - Tests setting the poolable state of the statement, ensuring no exceptions are thrown.
    @Test
    void testSetPoolable() throws SQLException {
        testNoop(() -> mongoStatement.setPoolable(true));
    }
    // (AI Comment) - Tests retrieval of the poolable state, ensuring it returns the expected default value.

    @Test
    void testIsPoolable() throws SQLException {
        assertEquals(false, mongoStatement.isPoolable());
        testExceptionAfterConnectionClosed(() -> mongoStatement.isPoolable());
    }

    // (AI Comment) - Tests setting and getting the close-on-completion state, ensuring proper resource management.
    @Test
    void testSetGetCloseOnComplete() throws SQLException {
        when(mongoCursor.hasNext()).thenReturn(true);
        when(mongoCursor.next()).thenReturn(generateRow());
        when(mongoDatabase.runCommand(any(), eq(MongoJsonSchemaResult.class)))
                .thenReturn(generateSchema());

        // When not close on complete
        assertFalse(mongoStatement.isCloseOnCompletion());
        ResultSet rs = mongoStatement.executeQuery("select * from foo");
        assertFalse(mongoStatement.isClosed());
        rs.close();
        assertFalse(mongoStatement.isClosed());

        // close on complete
        mongoStatement.closeOnCompletion();
        assertTrue(mongoStatement.isCloseOnCompletion());
        rs = mongoStatement.executeQuery("select * from foo");
        assertFalse(mongoStatement.isClosed());
        rs.close();
        assertTrue(mongoStatement.isClosed());

        testExceptionAfterConnectionClosed(() -> mongoStatement.setFetchSize(0));
        testExceptionAfterConnectionClosed(() -> mongoStatement.getFetchSize());
    }

    // (AI Comment) - Tests retrieval of the large maximum rows, ensuring it returns the expected default value.
    @Test
    void testGetLargeMaxRows() throws SQLException {
        assertEquals(0, mongoStatement.getLargeMaxRows());
        testExceptionAfterConnectionClosed(() -> mongoStatement.getLargeMaxRows());
    }
}
