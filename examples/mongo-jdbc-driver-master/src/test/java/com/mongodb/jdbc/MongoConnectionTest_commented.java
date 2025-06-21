/*
********* AI-Assistant Documentation for - MongoConnectionTest_commented.java *********
This file contains unit tests for the MongoConnection class, ensuring its correct behavior in various scenarios related to MongoDB connections. It utilizes JUnit for testing and Mockito for mocking dependencies, focusing on aspects such as connection state, application name generation, and exception handling.
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.internal.MongoClientImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.WARN)
// (AI Comment) - Test class for MongoConnection, verifying its behavior and interactions with MongoDB. It uses Mockito for mocking dependencies and JUnit for testing.
class MongoConnectionTest extends MongoMock {
    static final String localhost = "mongodb://localhost";
    @Mock private MongoConnectionProperties mockConnectionProperties;

    // (AI Comment) - Initializes mock objects before all tests are run.
    @BeforeAll
    protected void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // Since MongoConnection cannot be created with its constructor, we have to use InjectionMocks Annotation and
    // create it during initiation. In order to reuse the same object for each test, we need to reset it before each test case.
    // (AI Comment) - Resets mock objects before each test to ensure a clean state.
    @BeforeEach
    void setupTest() throws NoSuchFieldException {
        resetMockObjs();
    }

    // (AI Comment) - Sets up mock behavior for connection properties before each test.
    @BeforeEach
    void setUp() {
        when(mockConnectionProperties.getConnectionString())
                .thenReturn(new ConnectionString(localhost));
        when(mockConnectionProperties.getDatabase()).thenReturn("test");
    }

    // (AI Comment) - Retrieves the application name from the MongoConnection instance.
    private String getApplicationName(MongoConnection connection) {
        MongoClientImpl mongoClientImpl = (MongoClientImpl) connection.getMongoClient();
        MongoClientSettings mcs = mongoClientImpl.getSettings();
        return mcs.getApplicationName();
    }

    // (AI Comment) - Tests application name generation when client info is not provided.
    @Test
    void testBuildAppNameWithoutClientInfo() {
        when(mockConnectionProperties.getClientInfo()).thenReturn(null);

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName = MongoDriver.NAME + "+" + MongoDriver.getVersion();
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // (AI Comment) - Tests application name generation with valid client info.
    @Test
    void testAppNameWithValidClientInfo() {
        String clientInfo = "test-client+1.0.0";
        when(mockConnectionProperties.getClientInfo()).thenReturn(clientInfo);

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName =
                MongoDriver.NAME + "+" + MongoDriver.getVersion() + "|" + clientInfo;
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // (AI Comment) - Tests application name generation with invalid client info format.
    @Test
    void testAppNameWithInvalidClientInfo() {
        // Client information has to be in the format 'name+version'
        when(mockConnectionProperties.getClientInfo()).thenReturn("invalid-client-info");

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName = MongoDriver.NAME + "+" + MongoDriver.getVersion();
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // to replace lambda as input in the testExceptionAfterConnectionClosed
    // (AI Comment) - Defines a functional interface for testing SQL exceptions.
    interface TestInterface {
        void test() throws SQLException;
    }

    // (AI Comment) - Tests that an exception is thrown when trying to create a statement after the connection is closed.
    void testExceptionAfterConnectionClosed(TestInterface ti) {
        // create statement after closed throws exception
        mongoConnection.close();
        assertThrows(SQLException.class, ti::test);
    }

    // (AI Comment) - Tests that no exception is thrown for valid operations and checks exception handling after closing the connection.
    void testNoop(TestInterface ti) {
        assertDoesNotThrow(ti::test);
        testExceptionAfterConnectionClosed(ti::test);
    }

    // (AI Comment) - Tests the connection state before and after closing the connection.
    @Test
    void testCheckConnection() {
        // When initiated
        assertFalse(mongoConnection.isClosed());

        // after calling close()
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());
    }

    // (AI Comment) - Tests statement creation and verifies that multiple statements can be created.
    @Test
    void testCreateStatement() throws SQLException {
        Statement statement = mongoConnection.createStatement();

        // Should be able to create multiple statements.
        Statement statement2 = mongoConnection.createStatement();
        assertNotEquals(statement, statement2);

        // create statement after closed throws exception
        mongoConnection.close();
        testExceptionAfterConnectionClosed(() -> mongoConnection.createStatement());
    }

    @Test
    // (AI Comment) - Tests setting auto-commit to true.
    void testSetAutoCommitTrue() {
        testNoop(() -> mongoConnection.setAutoCommit(true));
    }

    // (AI Comment) - Tests setting auto-commit to false.
    @Test
    void testSetAutoCommitFalse() {
        testNoop(() -> mongoConnection.setAutoCommit(false));
    }

    @Test
    // (AI Comment) - Tests retrieval of the auto-commit state.
    void testGetAutoCommit() throws SQLException {
        assertTrue(mongoConnection.getAutoCommit());
    }

    @Test
    // (AI Comment) - Tests the commit operation.
    void testCommit() {
        testNoop(() -> mongoConnection.commit());
    }

    @Test
    // (AI Comment) - Tests the rollback operation.
    void testRollback() {
        testNoop(() -> mongoConnection.rollback());
    }

    @Test
    // (AI Comment) - Tests the connection close operation and verifies the closed state.
    void testCloseAndIsClosed() {
        assertFalse(mongoConnection.isClosed());
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());

        // noop for second close()
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());
    }

    @Test
    // (AI Comment) - Tests setting the connection to read-only mode.
    void testSetReadOnly() {
        testNoop(() -> mongoConnection.setReadOnly(true));
    }

    @Test
    // (AI Comment) - Tests checking if the connection is in read-only mode.
    void testIsReadOnly() {
        testNoop(() -> mongoConnection.isReadOnly());
    }

    @Test
    // (AI Comment) - Tests setting and getting the catalog name, including exception handling after closing the connection.
    void testSetGetCatalog() throws SQLException {
        assertEquals(database, mongoConnection.getCatalog());
        mongoConnection.setCatalog("test1");
        assertEquals("test1", mongoConnection.getCatalog());

        testExceptionAfterConnectionClosed(() -> mongoConnection.setCatalog("test"));
        testExceptionAfterConnectionClosed(() -> mongoConnection.getCatalog());
    }

    @Test
    // (AI Comment) - Tests setting the transaction isolation level.
    void tesSetTransactionIsolation() {
        testNoop(
                () ->
                        mongoConnection.setTransactionIsolation(
                                Connection.TRANSACTION_READ_UNCOMMITTED));
    }

    @Test
    // (AI Comment) - Tests getting the transaction isolation level, including exception handling after closing the connection.
    void tesGetTransactionIsolation() throws SQLException {
        assertEquals(Connection.TRANSACTION_NONE, mongoConnection.getTransactionIsolation());
        testExceptionAfterConnectionClosed(() -> mongoConnection.getTransactionIsolation());
    }

    @Test
    // (AI Comment) - Tests getting warnings from the connection, including exception handling after closing the connection.
    void testGetWarnings() throws SQLException {
        assertEquals(null, mongoConnection.getWarnings());
        testExceptionAfterConnectionClosed(() -> mongoConnection.getWarnings());
    }

    @Test
    // (AI Comment) - Tests clearing warnings from the connection.
    void testClearWarnings() {
        testNoop(() -> mongoConnection.clearWarnings());
    }

    @Test
    // (AI Comment) - Tests rollback operation with a savepoint.
    void testRollbackJ3() {
        Savepoint sp = mock(Savepoint.class);
        testNoop(() -> mongoConnection.rollback(sp));
    }
}
