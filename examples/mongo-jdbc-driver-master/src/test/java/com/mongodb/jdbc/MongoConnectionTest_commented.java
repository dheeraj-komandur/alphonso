/*
********* AI-Assistant Documentation for - MongoConnectionTest_commented.java *********
This file contains unit tests for the 'MongoConnection' class in the MongoDB JDBC driver, validating its functionality and ensuring correct behavior in various scenarios. It utilizes JUnit and Mockito to create a robust testing environment, focusing on connection management, application name generation, and transaction handling.
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

    // (AI Comment) - Initializes mocks before all tests are run, ensuring that mock objects are ready for use.
    @BeforeAll
    protected void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // Since MongoConnection cannot be created with its constructor, we have to use InjectionMocks Annotation and
    // create it during initiation. In order to reuse the same object for each test, we need to reset it before each test case.
    // (AI Comment) - Resets mock objects before each test to ensure a clean state for testing.
    @BeforeEach
    void setupTest() throws NoSuchFieldException {
        resetMockObjs();
    }

    // (AI Comment) - Sets up mock behaviors for connection properties before each test, defining expected return values.
    @BeforeEach
    void setUp() {
        when(mockConnectionProperties.getConnectionString())
                .thenReturn(new ConnectionString(localhost));
        when(mockConnectionProperties.getDatabase()).thenReturn("test");
    }

    // (AI Comment) - Retrieves the application name from the MongoConnection instance, extracting it from the MongoClient settings.
    private String getApplicationName(MongoConnection connection) {
        MongoClientImpl mongoClientImpl = (MongoClientImpl) connection.getMongoClient();
        MongoClientSettings mcs = mongoClientImpl.getSettings();
        return mcs.getApplicationName();
    }

    // (AI Comment) - Tests the application name generation when no client info is provided, expecting a default format.
    @Test
    void testBuildAppNameWithoutClientInfo() {
        when(mockConnectionProperties.getClientInfo()).thenReturn(null);

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName = MongoDriver.NAME + "+" + MongoDriver.getVersion();
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // (AI Comment) - Tests the application name generation with valid client info, ensuring it is appended correctly.
    @Test
    void testAppNameWithValidClientInfo() {
        String clientInfo = "test-client+1.0.0";
        when(mockConnectionProperties.getClientInfo()).thenReturn(clientInfo);

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName =
                MongoDriver.NAME + "+" + MongoDriver.getVersion() + "|" + clientInfo;
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // (AI Comment) - Tests the application name generation with invalid client info, ensuring it defaults to the expected format.
    @Test
    void testAppNameWithInvalidClientInfo() {
        // Client information has to be in the format 'name+version'
        when(mockConnectionProperties.getClientInfo()).thenReturn("invalid-client-info");

        mongoConnection = new MongoConnection(null, mockConnectionProperties);

        String expectedAppName = MongoDriver.NAME + "+" + MongoDriver.getVersion();
        assertEquals(expectedAppName, getApplicationName(mongoConnection));
    }

    // to replace lambda as input in the testExceptionAfterConnectionClosed
    // (AI Comment) - Defines a functional interface for testing methods that throw SQLException, allowing for flexible testing.
    interface TestInterface {
        void test() throws SQLException;
    }

    // (AI Comment) - Tests that an exception is thrown when attempting to create a statement after the connection is closed.
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

    // (AI Comment) - Tests the connection state before and after closing it, ensuring the isClosed method reflects the correct state.
    @Test
    void testCheckConnection() {
        // When initiated
        assertFalse(mongoConnection.isClosed());

        // after calling close()
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());
    }

    // (AI Comment) - Tests the creation of statements, ensuring multiple statements can be created and that exceptions are handled correctly after closing.
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

    // (AI Comment) - Tests setting auto-commit to true, ensuring no exceptions are thrown during the operation.
    @Test
    void testSetAutoCommitTrue() {
        testNoop(() -> mongoConnection.setAutoCommit(true));
    }

    @Test
    void testSetAutoCommitFalse() {
        testNoop(() -> mongoConnection.setAutoCommit(false));
    }

    @Test
    // (AI Comment) - Tests retrieval of the auto-commit state, ensuring it returns the expected value.
    void testGetAutoCommit() throws SQLException {
        assertTrue(mongoConnection.getAutoCommit());
    }

    @Test
    // (AI Comment) - Tests the commit operation, ensuring it executes without exceptions.
    void testCommit() {
        testNoop(() -> mongoConnection.commit());
    }

    @Test
    // (AI Comment) - Tests the rollback operation, ensuring it executes without exceptions.
    void testRollback() {
        testNoop(() -> mongoConnection.rollback());
    }

    @Test
    // (AI Comment) - Tests the close operation and verifies the connection state, ensuring that multiple close calls do not throw exceptions.
    void testCloseAndIsClosed() {
        assertFalse(mongoConnection.isClosed());
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());

        // noop for second close()
        mongoConnection.close();
        assertTrue(mongoConnection.isClosed());
    }

    @Test
    // (AI Comment) - Tests setting the connection to read-only mode, ensuring no exceptions are thrown.
    void testSetReadOnly() {
        testNoop(() -> mongoConnection.setReadOnly(true));
    }

    @Test
    // (AI Comment) - Tests the read-only state of the connection, ensuring it executes without exceptions.
    void testIsReadOnly() {
        testNoop(() -> mongoConnection.isReadOnly());
    }

    @Test
    // (AI Comment) - Tests setting and getting the catalog, ensuring correct behavior and exception handling after closing the connection.
    void testSetGetCatalog() throws SQLException {
        assertEquals(database, mongoConnection.getCatalog());
        mongoConnection.setCatalog("test1");
        assertEquals("test1", mongoConnection.getCatalog());

        testExceptionAfterConnectionClosed(() -> mongoConnection.setCatalog("test"));
        testExceptionAfterConnectionClosed(() -> mongoConnection.getCatalog());
    }

    @Test
    // (AI Comment) - Tests setting the transaction isolation level, ensuring no exceptions are thrown.
    void tesSetTransactionIsolation() {
        testNoop(
                () ->
                        mongoConnection.setTransactionIsolation(
                                Connection.TRANSACTION_READ_UNCOMMITTED));
    }

    @Test
    // (AI Comment) - Tests getting the transaction isolation level, ensuring it returns the expected value and handles exceptions correctly.
    void tesGetTransactionIsolation() throws SQLException {
        assertEquals(Connection.TRANSACTION_NONE, mongoConnection.getTransactionIsolation());
        testExceptionAfterConnectionClosed(() -> mongoConnection.getTransactionIsolation());
    }

    @Test
    // (AI Comment) - Tests getting warnings from the connection, ensuring it returns null and handles exceptions correctly.
    void testGetWarnings() throws SQLException {
        assertEquals(null, mongoConnection.getWarnings());
        testExceptionAfterConnectionClosed(() -> mongoConnection.getWarnings());
    }

    @Test
    // (AI Comment) - Tests clearing warnings from the connection, ensuring no exceptions are thrown.
    void testClearWarnings() {
        testNoop(() -> mongoConnection.clearWarnings());
    }

    @Test
    // (AI Comment) - Tests the rollback operation with a specific savepoint, ensuring no exceptions are thrown.
    void testRollbackJ3() {
        Savepoint sp = mock(Savepoint.class);
        testNoop(() -> mongoConnection.rollback(sp));
    }
}
