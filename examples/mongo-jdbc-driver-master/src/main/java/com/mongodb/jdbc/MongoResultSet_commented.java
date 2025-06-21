/*
********* AI-Assistant Documentation for - MongoResultSet_commented.java *********
This file implements the MongoResultSet class, which provides a JDBC-compliant interface for accessing MongoDB data. It handles data retrieval, conversion, and error management, ensuring that MongoDB data can be accessed in a manner consistent with standard SQL operations.
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

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCursor;
import com.mongodb.jdbc.logging.AutoLoggable;
import com.mongodb.jdbc.logging.MongoLogger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.UuidRepresentation;
import org.bson.internal.UuidHelper;
import org.bson.types.Decimal128;

// (AI Comment) - Defines the MongoResultSet class which implements the ResultSet interface for MongoDB data retrieval.
@AutoLoggable
public class MongoResultSet implements ResultSet {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    // dateFormat cannot be static due to a threading bug in the library.
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    // The current row
    protected BsonDocument current;
    // cursor over all rows
    protected MongoCursor<BsonDocument> cursor;

    // The one-indexed number of the current row. Will be zero until
    // next() is called for the first time.
    protected int rowNum = 0;

    protected boolean closed = false;
    protected MongoStatement statement;
    protected boolean wasNull = false;
    protected MongoResultSetMetaData rsMetaData;
    protected MongoLogger logger;
    // Boolean marker true if the JSON representation is Extended JSON. False otherwise.
    protected boolean extJsonMode;
    protected UuidRepresentation uuidRepresentation;

    private MongoJsonSchema jsonSchema;

    // (AI Comment) - Constructor for a MongoResultSet tied to a connection and statement, initializing the result set with the provided cursor and schema.
    /**
     * Constructor for a MongoResultset tied to a connection and statement.
     *
     * @param statement The statement this resultset is related to.
     * @param cursor The resultset cursor.
     * @param resultSetchema The resultset schema.
     * @param selectOrder The select list order.
     * @param extJsonMode The JSON mode.
     * @throws SQLException
     */
    public MongoResultSet(
            MongoStatement statement,
            MongoCursor<BsonDocument> cursor,
            MongoJsonSchema resultSetchema,
            List<List<String>> selectOrder,
            boolean extJsonMode,
            UuidRepresentation uuidRepresentation)
            throws SQLException {
        Preconditions.checkNotNull(statement);
        this.statement = statement;
        this.logger =
                new MongoLogger(
                        this.getClass().getCanonicalName(),
                        statement.getParentLogger(),
                        statement.getStatementId());
        logger.setQueryDiagnostics(statement.getQueryDiagnostics());
        this.extJsonMode = extJsonMode;
        this.uuidRepresentation = uuidRepresentation;
        setUpResultset(
                cursor,
                resultSetchema,
                selectOrder,
                true,
                statement.getParentLogger(),
                statement.getStatementId());
    }

    // (AI Comment) - Constructor for a MongoResultSet not tied to a statement, used for MongoDatabaseMetaData.
    /**
     * Constructor for a MongoResultSet not tied to a statement used for MongoDatabaseMetaData.
     *
     * @param parentLogger The parent connection logger.
     * @param cursor The resultset cursor.
     * @param schema The resultset schema.
     * @throws SQLException
     */
    public MongoResultSet(
            MongoLogger parentLogger, MongoCursor<BsonDocument> cursor, MongoJsonSchema schema)
            throws SQLException {
        this.logger = new MongoLogger(this.getClass().getCanonicalName(), parentLogger);
        setUpResultset(cursor, schema, null, false, parentLogger, null);
    }

    // (AI Comment) - Sets up the result set with the provided cursor, schema, and select order, ensuring proper initialization.
    private void setUpResultset(
            MongoCursor<BsonDocument> cursor,
            MongoJsonSchema schema,
            List<List<String>> selectOrder,
            boolean sortFieldsAlphabetically,
            MongoLogger parentLogger,
            Integer statementId)
            throws SQLException {
        Preconditions.checkNotNull(cursor);
        this.jsonSchema = schema;
        // dateFormat is not thread safe, so we do not want to make it a static field.
        dateFormat.setTimeZone(UTC);
        // Only sort the columns alphabetically for SQL statement result sets and not for database metadata result sets.
        // The JDBC specification provides the order for each database metadata result set.
        // Because a lot BI tools will access database metadata columns by index, the specification order must be respected.
        this.cursor = cursor;

        this.rsMetaData =
                new MongoResultSetMetaData(
                        schema,
                        selectOrder,
                        sortFieldsAlphabetically,
                        parentLogger,
                        statementId,
                        logger.getQueryDiagnostics());
    }

    // (AI Comment) - Package-private method to retrieve the current BsonDocument for testing purposes.
    // This is only used for testing, and that is why it has package level access, and the
    // tests have been moved into this package.
    BsonDocument getCurrent() {
        return current;
    }

    // (AI Comment) - Checks the bounds of the current row index and throws SQLException if out of bounds or if no current row exists.
    private void checkBounds(int i) throws SQLException {
        checkClosed();
        if (current == null) {
            throw new SQLException("No current row in the result set. Make sure to call next().");
        }
        if (i > rsMetaData.getColumnCount()) {
            throw new SQLException("Index out of bounds: '" + i + "'.");
        }
    }

    // (AI Comment) - Advances the cursor to the next row in the result set, logging the operation and handling exceptions.
    @Override
    public boolean next() throws SQLException {
        checkClosed();
        try {
            boolean result;
            result = cursor.hasNext();
            logger.log(Level.FINER, "cursor.hasNext()? " + String.valueOf(result));
            if (result) {
                logger.log(Level.FINEST, "Getting row " + (rowNum + 1));
                long startTime = System.nanoTime();
                current = cursor.next();
                long endTime = System.nanoTime();
                logger.log(
                        Level.FINER,
                        "Moved to next row in "
                                + ((endTime - startTime) / 1000000d)
                                + " milliseconds");
                ++rowNum;
            }
            return result;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    // (AI Comment) - Closes the result set and releases any resources associated with it, ensuring proper cleanup.
    @Override
    public void close() throws SQLException {
        if (closed) {
            return;
        }
        cursor.close();
        closed = true;
        if (statement != null && !statement.isClosed && statement.isCloseOnCompletion()) {
            statement.close();
        }
    }

    // (AI Comment) - Checks if the cursor is at the last row of the result set.
    @Override
    public boolean isLast() throws SQLException {
        checkClosed();
        return !cursor.hasNext();
    }

    // (AI Comment) - Retrieves the BsonValue for the specified column index from the current row.
    private BsonValue getBsonValue(int columnIndex) throws SQLException {
        checkBounds(columnIndex);
        MongoColumnInfo columnInfo = rsMetaData.getColumnInfo(columnIndex);
        BsonDocument datasource = this.current.get(columnInfo.getTableName()).asDocument();
        return datasource.get(columnInfo.getColumnName());
    }

    // (AI Comment) - Retrieves the BsonValue for the specified column label from the current row.
    private BsonValue getBsonValue(String columnLabel) throws SQLException {
        int columnIndex;
        if (rsMetaData.hasColumnWithLabel(columnLabel)) {
            try {
                columnIndex = rsMetaData.getColumnPositionFromLabel(columnLabel);
                return getBsonValue(columnIndex + 1);
            } catch (Exception e) {
                throw new SQLException(e.getMessage());
            }
        } else {
            throw new SQLException(String.format("column label '%s' not found", columnLabel));
        }
    }

    // (AI Comment) - Checks if the result set is closed and throws SQLException if it is.
    private void checkClosed() throws SQLException {
        if (closed) throw new SQLException("MongoResultSet is closed.");
    }

    // (AI Comment) - Returns true if the last retrieved value was null, adhering to JDBC API requirements.
    @Override
    public boolean wasNull() throws SQLException {
        checkClosed();
        return wasNull;
    }

    // (AI Comment) - Checks if the provided BsonValue is null or undefined, updating the wasNull flag accordingly.
    /**
     * Returns true if the Object obj is null. Crucially, it also must set the value of `wasNull`,
     * since that is part of the JDBC API.
     *
     * @param obj the object to check.
     * @return true if the object is Null, False otherwise.
     */
    private boolean checkNull(BsonValue obj) throws SQLException {
        // reset wasNull from previous check.
        wasNull = false;
        if (obj == null) {
            wasNull = true;
            return true;
        }
        switch (BsonTypeInfo.getBsonTypeInfoFromBsonValue(obj).getBsonType()) {
            case NULL:
            case UNDEFINED:
                wasNull = true;
                return true;
        }
        return false;
    }

    // (AI Comment) - Deprecated method for retrieving BigDecimal, not supported in this implementation.
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Handles conversion failures for byte arrays, throwing SQLException.
    private byte[] handleBytesConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to blob.");
    }

    private byte[] getBytes(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        // we only allow getting Strings and Binaries as Bytes so that
        // we can conveniently ignore Endianess issues. Null and undefined
        // are still supported because Bytes's can be null.
        BsonTypeInfo bsonType = BsonTypeInfo.getBsonTypeInfoFromBsonValue(o);
        switch (bsonType.getBsonType()) {
            case BINARY:
                return o.asBinary().getData();
            case NULL:
                return null;
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
                return null;
            default:
                return handleBytesConversionFailure(bsonType.getBsonName());
        }
    }
    // (AI Comment) - Retrieves byte array representation of the specified column index.

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getBytes(out);
    }

    // (AI Comment) - Retrieves byte array representation of the specified column label.
    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getBytes(out);
    }

    // (AI Comment) - Creates a new ByteArrayInputStream from the provided byte array.
    protected static ByteArrayInputStream getNewByteArrayInputStream(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    // (AI Comment) - Retrieves an ASCII stream for the specified column index, converting the string to bytes.
    @Override
    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        final String encoding = "ASCII";
        try {
            return getNewByteArrayInputStream(getString(columnIndex).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    @Override
    // (AI Comment) - Retrieves an ASCII stream for the specified column label, converting the string to bytes.
    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        final String encoding = "ASCII";
        try {
            return getNewByteArrayInputStream(getString(columnLabel).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    // (AI Comment) - Deprecated method for retrieving Unicode stream, not supported in this implementation.
    @Deprecated
    @Override
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        final String encoding = "UTF-8";
        try {
            return getNewByteArrayInputStream(getString(columnIndex).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    @Deprecated
    @Override
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        final String encoding = "UTF-8";
        try {
            return getNewByteArrayInputStream(getString(columnLabel).getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("The JVM claims not to support the encoding: " + encoding + ".");
        }
    }

    // (AI Comment) - Retrieves a binary stream for the specified column index.
    @Override
    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        return getNewByteArrayInputStream(getBytes(columnIndex));
    }

    @Override
    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getNewByteArrayInputStream(getBytes(columnLabel));
    }

    // (AI Comment) - Retrieves a binary stream for the specified column label.
    private String handleStringConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to string.");
    }

    private String getString(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        return new MongoBsonValue(o, extJsonMode, uuidRepresentation).toString();
    }

    // (AI Comment) - Retrieves string representation of the specified column label.
    @Override
    public String getString(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getString(out);
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getString(out);
    }

    // (AI Comment) - Handles conversion failures for boolean values, throwing SQLException.
    private boolean handleBooleanConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to boolean.");
    }

    // (AI Comment) - Retrieves boolean value from the specified BsonValue, handling various BSON types.
    private boolean getBoolean(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return false;
        }
        BsonTypeInfo bsonType = BsonTypeInfo.getBsonTypeInfoFromBsonValue(o);
        switch (bsonType.getBsonType()) {
            case BOOLEAN:
                return o.asBoolean().getValue();
            case DECIMAL128:
                {
                    Decimal128 v = o.asDecimal128().getValue();
                    return !Objects.equals(v, Decimal128.POSITIVE_ZERO)
                            && !Objects.equals(v, Decimal128.NEGATIVE_ZERO);
                }
            case DOUBLE:
                return o.asDouble().getValue() != 0.0;
            case INT32:
                return o.asInt32().getValue() != 0;
            case INT64:
                return o.asInt64().getValue() != 0;
            case NULL:
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBoolean
                // returns false for null values.
                return false;
            case STRING:
                // mongodb $convert converts all strings to true, even the empty string.
                return true;
            default:
                return handleBooleanConversionFailure(bsonType.getBsonName());
        }
    }

    // (AI Comment) - Retrieves boolean value for the specified column label.
    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getBoolean(out);
    }

    // (AI Comment) - Retrieves boolean value for the specified column index.
    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getBoolean(out);
    }

    // (AI Comment) - Retrieves byte value from the specified BsonValue.
    protected byte getByte(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (byte) getLong(o);
    }

    // (AI Comment) - Retrieves byte value for the specified column label.
    @Override
    public byte getByte(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getByte(out);
    }

    // (AI Comment) - Retrieves byte value for the specified column index.
    @Override
    public byte getByte(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getByte(out);
    }

    // (AI Comment) - Retrieves short value from the specified BsonValue.
    private short getShort(BsonValue o) throws SQLException {
        // Just be lazy, I doubt this will be called often.
        // HotSpot should inline these, anyway.
        return (short) getLong(o);
    }

    // (AI Comment) - Retrieves short value for the specified column label.
    @Override
    public short getShort(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getShort(out);
    }

    // (AI Comment) - Retrieves short value for the specified column index.
    @Override
    public short getShort(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getShort(out);
    }

    // (AI Comment) - Retrieves int value from the specified BsonValue.
    private int getInt(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0;
        }
        return (int) getLong(o);
    }

    // (AI Comment) - Retrieves int value for the specified column label.
    @Override
    public int getInt(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getInt(out);
    }

    // (AI Comment) - Retrieves int value for the specified column index.
    @Override
    public int getInt(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getInt(out);
    }

    // (AI Comment) - Handles conversion failures for long values, throwing SQLException.
    private long handleLongConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to integral type.");
    }

    // (AI Comment) - Retrieves long value from the specified BsonValue, handling various BSON types.
    private long getLong(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0L;
        }
        BsonTypeInfo bsonType = BsonTypeInfo.getBsonTypeInfoFromBsonValue(o);
        switch (o.getBsonType()) {
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1 : 0;
            case DATE_TIME:
                // This is what $convert does.
                return o.asDateTime().getValue();
            case DECIMAL128:
                return o.asDecimal128().longValue();
            case DOUBLE:
                return (long) o.asDouble().getValue();
            case INT32:
                return o.asInt32().getValue();
            case INT64:
                return o.asInt64().getValue();
            case NULL:
                return 0L;
            case STRING:
                try {
                    return Long.parseLong(o.asString().getValue());
                } catch (NumberFormatException e) {
                    throw new SQLException(e);
                }
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getLong
                // returns 0.0 for null values.
                return 0L;
            default:
                return handleLongConversionFailure(bsonType.getBsonName());
        }
    }

    // (AI Comment) - Retrieves long value for the specified column label.
    @Override
    public long getLong(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getLong(out);
    }

    @Override
    // (AI Comment) - Retrieves long value for the specified column index.
    public long getLong(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getLong(out);
    }

    private float getFloat(BsonValue o) throws SQLException {
        // (AI Comment) - Retrieves float value from the specified BsonValue.
        return (float) getDouble(o);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getFloat(out);
    // (AI Comment) - Retrieves float value for the specified column label.
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getFloat(out);
    }
    // (AI Comment) - Retrieves float value for the specified column index.

    private double handleDoubleConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to double.");
    }

    // (AI Comment) - Handles conversion failures for double values, throwing SQLException.
    private double getDouble(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return 0.0;
        }
        // (AI Comment) - Retrieves double value from the specified BsonValue, handling various BSON types.
        BsonTypeInfo bsonType = BsonTypeInfo.getBsonTypeInfoFromBsonValue(o);
        switch (o.getBsonType()) {
            case BOOLEAN:
                return o.asBoolean().getValue() ? 1.0 : 0.0;
            case DATE_TIME:
                // This is what $convert does.
                return (double) o.asDateTime().getValue();
            case DECIMAL128:
                return o.asDecimal128().doubleValue();
            case DOUBLE:
                return o.asDouble().getValue();
            case INT32:
                return o.asInt32().getValue();
            case INT64:
                return (double) o.asInt64().getValue();
            case STRING:
                try {
                    return Double.parseDouble(o.asString().getValue());
                } catch (NumberFormatException e) {
                    throw new SQLException(e);
                }
            case NULL:
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getDouble
                // returns 0.0 for null values.
                return 0.0;
            default:
                return handleDoubleConversionFailure(bsonType.getBsonName());
        }
    }

    @Override
    // (AI Comment) - Retrieves double value for the specified column label.
    public double getDouble(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getDouble(out);
    }

    @Override
    // (AI Comment) - Retrieves double value for the specified column index.
    public double getDouble(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getDouble(out);
    }

    // (AI Comment) - Deprecated method for retrieving BigDecimal, not supported in this implementation.
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves SQL warnings for the current result set.
    // Advanced features:

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        return null;
    }

    // (AI Comment) - Clears any warnings associated with the current result set.
    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
    }

    // (AI Comment) - Retrieves the cursor name associated with the current statement or cursor.
    @Override
    public String getCursorName() throws SQLException {
        if (this.statement.cursorName != null) {
            return this.statement.cursorName;
        }
        if (this.cursor != null) {
            return String.valueOf(this.cursor.getServerCursor().getId());
        }
        return "";
    }

    // (AI Comment) - Retrieves metadata for the current result set.
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkClosed();
        return rsMetaData;
    }

    // (AI Comment) - Retrieves an object from the result set based on the specified column type, handling various BSON types.
    private Object getObject(BsonValue o, int columnType) throws SQLException {
        // If the value is an SQL NULL, the driver returns a Java null.
        if (checkNull(o)) {
            return null;
        }
        switch (columnType) {
            case Types.BIGINT:
                return getLong(o);
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return getInt(o);
            case Types.BINARY:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
                if (o.getBsonType() == BsonType.BINARY) {
                    BsonBinary binary = o.asBinary();
                    if (binary.getType() == BsonBinarySubType.UUID_STANDARD.getValue()
                            || binary.getType() == BsonBinarySubType.UUID_LEGACY.getValue()) {
                        // Handle UUID
                        return UuidHelper.decodeBinaryToUuid(
                                binary.getData(), binary.getType(), uuidRepresentation);
                    }
                }
                return o.asBinary().getData();
            case Types.BIT:
            case Types.BOOLEAN:
                return getBoolean(o);
            case Types.DOUBLE:
            case Types.FLOAT:
                return getDouble(o);
            case Types.DECIMAL:
            case Types.NUMERIC:
                return o.asDecimal128().decimal128Value().bigDecimalValue();
            case Types.CHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.VARCHAR:
                return getString(o);
            case Types.REAL:
                return getFloat(o);
            case Types.TIMESTAMP:
                return new Timestamp(o.asDateTime().getValue());
            case Types.NULL:
                return null;

            case Types.OTHER:
                if (o.getBsonType() == BsonType.NULL) {
                    return null;
                }
                // These types are wrapped in MongoBsonValue so that
                // if they are stringified via toString() they will be
                // represented as extended JSON.
                return new MongoBsonValue(o, extJsonMode, uuidRepresentation);

            case Types.ARRAY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.DATALINK:
            case Types.DATE:
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
            case Types.NCLOB:
            case Types.REF:
            case Types.REF_CURSOR:
            case Types.ROWID:
            case Types.SQLXML:
            case Types.STRUCT:
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // not supported
                break;
        }
        throw new SQLException("getObject not supported for column type " + columnType);
    }

    // (AI Comment) - Retrieves an object based on the specified column index.
    @Override
    public Object getObject(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        int columnType = rsMetaData.getColumnType(columnIndex);
        return getObject(out, columnType);
    }

    @Override
    // (AI Comment) - Retrieves an object based on the specified column label.
    public Object getObject(String columnLabel) throws SQLException {
        int columnIndex = findColumn(columnLabel);
        return getObject(columnIndex);
    }

    @Override
    // (AI Comment) - Retrieves an object based on the specified column index and a mapping of SQL types.
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map)
            throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        if (checkNull(out)) {
            return null;
        }
        String columnTypeName = rsMetaData.getColumnTypeName(columnIndex);
        Class<?> type = map.get(columnTypeName);
        if (type == null) {
            return null;
        }
        return type.cast(out);
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map)
            throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    // (AI Comment) - Retrieves an object based on the specified column label and a mapping of SQL types.
    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        if (checkNull(out)) {
            return null;
        }
        return type.cast(out);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }

    // (AI Comment) - Retrieves an object based on the specified column index and type.
    // ----------------------------------------------------------------

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        // (AI Comment) - Retrieves an object based on the specified column label and type.
        checkClosed();
        if (!rsMetaData.hasColumnWithLabel(columnLabel)) {
            throw new SQLException("No such column: '" + columnLabel + "'.");
        }
        // (AI Comment) - Finds the column index based on the specified column label.
        try {
            return rsMetaData.getColumnPositionFromLabel(columnLabel) + 1;
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    // (AI Comment) - Retrieves the character stream for the specified column index, not supported in this implementation.
    // --------------------------JDBC 2.0-----------------------------------

    // ---------------------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------------------

    @Override
    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves the character stream for the specified column label, not supported in this implementation.
    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Handles conversion failures for BigDecimal, throwing SQLException.
    private BigDecimal handleBigDecimalConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to BigDecimal.");
    }

    private BigDecimal getBigDecimal(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return BigDecimal.ZERO;
        }
        BsonTypeInfo bsonType = BsonTypeInfo.getBsonTypeInfoFromBsonValue(o);
        switch (o.getBsonType()) {
            case BOOLEAN:
                return o.asBoolean().getValue() ? BigDecimal.ONE : BigDecimal.ZERO;
            case DATE_TIME:
                // This is what $convert does.
                return new BigDecimal(o.asDateTime().getValue());
            case DECIMAL128:
                return o.asDecimal128().decimal128Value().bigDecimalValue();
            case DOUBLE:
                return new BigDecimal(o.asDouble().getValue());
            case INT32:
                return new BigDecimal(o.asInt32().getValue());
            case INT64:
                return new BigDecimal(o.asInt64().getValue());
            case NULL:
            case UNDEFINED:
                // this is consistent with $convert in mongodb insofar as getBigDecimal
                // returns 0.0 for null values.
                return BigDecimal.ZERO;
            case STRING:
                try {
                    return new BigDecimal(o.asString().getValue());
                } catch (NumberFormatException | ArithmeticException e) {
                    throw new SQLException(e);
                }
            default:
                return handleBigDecimalConversionFailure(bsonType.getBsonName());
        }
    }
    // (AI Comment) - Retrieves BigDecimal value from the specified column index.

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getBigDecimal(out);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getBigDecimal(out);
    }

    // (AI Comment) - Retrieves BigDecimal value from the specified column label.
    // ---------------------------------------------------------------------
    // (AI Comment) - Checks if the cursor is before the first row, not supported in this implementation.
    // Traversal/Positioning
    // ---------------------------------------------------------------------

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Checks if the cursor is after the last row, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Checks if the cursor is at the first row.
    public boolean isFirst() throws SQLException {
        checkClosed();
        return rowNum == 1;
    }

    // (AI Comment) - Moves the cursor before the first row, not supported in this implementation.
    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Moves the cursor after the last row, not supported in this implementation.
    }

    @Override
    public void afterLast() throws SQLException {
        // (AI Comment) - Moves the cursor to the first row, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Moves the cursor to the last row, not supported in this implementation.
    @Override
    public boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves the current row number.
    @Override
    public int getRow() throws SQLException {
        checkClosed();
        return rowNum;
    }

    // (AI Comment) - Moves the cursor to the specified row, not supported in this implementation.
    @Override
    public boolean absolute(int row) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Moves the cursor relative to the current position, not supported in this implementation.
    @Override
    public boolean relative(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Moves the cursor to the previous row, not supported in this implementation.
    @Override
    public boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Sets the fetch direction for the result set, not supported in this implementation.
    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves the fetch direction for the result set.
    @Override
    public int getFetchDirection() throws SQLException {
        checkClosed();
        return ResultSet.FETCH_FORWARD;
    }

    // (AI Comment) - Sets the fetch size for the result set, not supported in this implementation.
    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves the fetch size for the result set, not supported in this implementation.
    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves the type of the result set, indicating it is forward-only.
    @Override
    public int getType() throws SQLException {
        checkClosed();
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    // (AI Comment) - Retrieves the concurrency type of the result set, indicating it is read-only.
    public int getConcurrency() throws SQLException {
        checkClosed();
        return ResultSet.CONCUR_READ_ONLY;
    }

    // (AI Comment) - Checks if the current row has been updated, not supported in this implementation.
    // ---------------------------------------------------------------------
    // Updates
    // ---------------------------------------------------------------------

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Checks if the current row has been inserted, not supported in this implementation.
    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Checks if the current row has been deleted, not supported in this implementation.
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        // (AI Comment) - Updates the current row to null, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a boolean value, not supported in this implementation.
    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a byte value, not supported in this implementation.
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        // (AI Comment) - Updates the current row to a short value, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to an int value, not supported in this implementation.
    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a long value, not supported in this implementation.
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        // (AI Comment) - Updates the current row to a float value, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a double value, not supported in this implementation.
    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a BigDecimal value, not supported in this implementation.
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        // (AI Comment) - Updates the current row to a string value, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a byte array, not supported in this implementation.
    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a date value, not supported in this implementation.
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        // (AI Comment) - Updates the current row to a time value, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a timestamp value, not supported in this implementation.
    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to an ASCII stream, not supported in this implementation.
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        // (AI Comment) - Updates the current row to a binary stream, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a character stream, not supported in this implementation.
    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to an object, not supported in this implementation.
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        // (AI Comment) - Updates the current row to a null value, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Inserts a new row into the result set, not supported in this implementation.
    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Updates the current row, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Deletes the current row, not supported in this implementation.
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Refreshes the current row, not supported in this implementation.
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length)
            // (AI Comment) - Cancels any updates to the current row, not supported in this implementation.
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }
    // (AI Comment) - Moves to the insert row, not supported in this implementation.

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Moves to the current row, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }
    // (AI Comment) - Retrieves the statement associated with this result set.

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public Statement getStatement() throws SQLException {
        checkClosed();
        return statement;
    }

    // (AI Comment) - Creates a new Blob from the provided byte array.
    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    protected Blob getNewBlob(byte[] bytes) throws SQLException {
        if (bytes == null) {
            return null;
        }
        try {
            return new SerialBlob(bytes);
        } catch (SerialException e) {
            throw new SQLException(e);
        }
    }

    // (AI Comment) - Retrieves a Blob based on the specified column label.
    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getNewBlob(getBytes(out));
    }

    // (AI Comment) - Retrieves a Blob based on the specified column index.
    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getNewBlob(getBytes(out));
    }

    // (AI Comment) - Creates a new Clob from the provided BsonValue.
    protected Clob getClob(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        return new SerialClob(getString(o).toCharArray());
    }

    // (AI Comment) - Retrieves a Clob based on the specified column label.
    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getClob(out);
    }

    @Override
    // (AI Comment) - Retrieves a Clob based on the specified column index.
    public Clob getClob(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getClob(out);
    }

    // (AI Comment) - Retrieves an Array based on the specified column index, not supported in this implementation.
    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves a Ref based on the specified column label, not supported in this implementation.
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves an Array based on the specified column label, not supported in this implementation.
    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Handles conversion failures for java.util.Date, throwing SQLException.
    private java.util.Date handleUtilDateConversionFailure(String from) throws SQLException {
        throw new SQLException("The " + from + " type cannot be converted to java.util.Date");
    }

    // (AI Comment) - Retrieves java.util.Date from the specified BsonValue, handling various BSON types.
    private java.util.Date getUtilDate(BsonValue o) throws SQLException {
        if (checkNull(o)) {
            return null;
        }
        BsonTypeInfo bsonType = BsonTypeInfo.getBsonTypeInfoFromBsonValue(o);
        switch (o.getBsonType()) {
            case DATE_TIME:
                return new java.util.Date(o.asDateTime().getValue());
            case DECIMAL128:
                return new Date(o.asDecimal128().longValue());
            case DOUBLE:
                return new Date((long) o.asDouble().getValue());
            case INT32:
                return new Date(o.asInt32().getValue());
            case INT64:
                return new Date(o.asInt64().getValue());
            case UNDEFINED:
                // this is consistent with $convert in mongodb.
            case NULL:
                return null;
            case STRING:
                try {
                    return dateFormat.parse(o.asString().getValue());
                } catch (ParseException e) {
                    throw new SQLException(e);
                }
            default:
                return handleUtilDateConversionFailure(bsonType.getBsonName());
        }
    }

    private Date getDate(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Date(utilDate.getTime());
    }

    // (AI Comment) - Retrieves Date based on the specified column label.
    @Override
    public Date getDate(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getDate(out);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getDate(out);
    }

    @Override
    // (AI Comment) - Retrieves Date based on the specified column index.
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date d = getDate(columnIndex);
        if (d == null) {
            return null;
        // (AI Comment) - Retrieves Date based on the specified column index and Calendar.
        }
        cal.setTime(d);
        return new Date(cal.getTime().getTime());
    }

    // (AI Comment) - Retrieves Date based on the specified column label and Calendar.
    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        Date d = getDate(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Date(cal.getTime().getTime());
    }

    // (AI Comment) - Retrieves Time from the specified BsonValue.
    protected Time getTime(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Time(utilDate.getTime());
    }

    // (AI Comment) - Retrieves Time based on the specified column label.
    @Override
    public Time getTime(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getTime(out);
    }

    // (AI Comment) - Retrieves Time based on the specified column index.
    @Override
    public Time getTime(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getTime(out);
    }

    @Override
    // (AI Comment) - Retrieves Time based on the specified column index and Calendar.
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        Time d = getTime(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Time(cal.getTime().getTime());
    // (AI Comment) - Retrieves Time based on the specified column label and Calendar.
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        Time d = getTime(columnLabel);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Time(cal.getTime().getTime());
    }

    // (AI Comment) - Retrieves Timestamp from the specified BsonValue.
    protected Timestamp getTimestamp(BsonValue o) throws SQLException {
        java.util.Date utilDate = getUtilDate(o);
        return (utilDate == null) ? null : new Timestamp(utilDate.getTime());
    }

    // (AI Comment) - Retrieves Timestamp based on the specified column label.
    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        BsonValue out = getBsonValue(columnLabel);
        return getTimestamp(out);
    }

    // (AI Comment) - Retrieves Timestamp based on the specified column index.
    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        BsonValue out = getBsonValue(columnIndex);
        return getTimestamp(out);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        // (AI Comment) - Retrieves Timestamp based on the specified column index and Calendar.
        Timestamp d = getTimestamp(columnIndex);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        return new Timestamp(cal.getTime().getTime());
    }
    // (AI Comment) - Retrieves Timestamp based on the specified column label and Calendar.

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        Timestamp d = getTimestamp(columnLabel);
        if (d == null) {
            // (AI Comment) - Retrieves URL based on the specified column index, not supported in this implementation.
            return null;
        }
        cal.setTime(d);
        return new Timestamp(cal.getTime().getTime());
    }

    // -------------------------- JDBC 3.0 ----------------------------------------

    @Override
    public java.net.URL getURL(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves URL based on the specified column label, not supported in this implementation.
    @Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Ref value, not supported in this implementation.
    @Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Ref value based on the specified column label, not supported in this implementation.
    @Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Blob value, not supported in this implementation.
    @Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Blob value based on the specified column label, not supported in this implementation.
    @Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Clob value, not supported in this implementation.
    @Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Clob value based on the specified column label, not supported in this implementation.
    @Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to an Array value, not supported in this implementation.
    @Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a Ref value based on the specified column label, not supported in this implementation.
    @Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to an Array value based on the specified column label, not supported in this implementation.
    // ------------------------- JDBC 4.0 -----------------------------------

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Retrieves RowId based on the specified column index, not supported in this implementation.
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Retrieves RowId based on the specified column label, not supported in this implementation.
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a RowId value, not supported in this implementation.
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a RowId value based on the specified column label, not supported in this implementation.
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Retrieves the holdability of the result set, not supported in this implementation.
    }

    @Override
    public boolean isClosed() throws SQLException {
        // (AI Comment) - Checks if the result set is closed.
        return closed;
    }

    @Override
    // (AI Comment) - Updates the current row to an NString value, not supported in this implementation.
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NString value based on the specified column label, not supported in this implementation.
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NClob value, not supported in this implementation.
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NClob value based on the specified column label, not supported in this implementation.
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves an NClob based on the specified column index, not supported in this implementation.
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves an NClob based on the specified column label, not supported in this implementation.
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves SQLXML based on the specified column index, not supported in this implementation.
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves SQLXML based on the specified column label, not supported in this implementation.
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an SQLXML value, not supported in this implementation.
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an SQLXML value based on the specified column label, not supported in this implementation.
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves NString based on the specified column index.
    public String getNString(int columnIndex) throws SQLException {
        return getString(columnIndex);
    }

    // (AI Comment) - Retrieves NString based on the specified column label.
    @Override
    public String getNString(String columnLabel) throws SQLException {
        return getString(columnLabel);
    }

    // (AI Comment) - Retrieves NCharacterStream based on the specified column index.
    @Override
    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
        return new java.io.StringReader(getString(columnIndex));
    }

    // (AI Comment) - Retrieves NCharacterStream based on the specified column label.
    @Override
    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
        return new java.io.StringReader(getString(columnLabel));
    }

    // (AI Comment) - Updates the current row to an NCharacterStream value, not supported in this implementation.
    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to an NCharacterStream value based on the specified column label, not supported in this implementation.
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Updates the current row to an ASCII stream value, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to a binary stream value, not supported in this implementation.
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a character stream value, not supported in this implementation.
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length)
            throws SQLException {
        // (AI Comment) - Updates the current row to an ASCII stream value, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to a binary stream value, not supported in this implementation.
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }
    // (AI Comment) - Updates the current row to a character stream value, not supported in this implementation.

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Updates the current row to an object value, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length)
            // (AI Comment) - Updates the current row to a null value, not supported in this implementation.
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Inserts a new row into the result set, not supported in this implementation.
    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row, not supported in this implementation.
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length)
            throws SQLException {
        // (AI Comment) - Deletes the current row, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Refreshes the current row, not supported in this implementation.
    public void updateBlob(String columnLabel, InputStream inputStream, long length)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }
    // (AI Comment) - Cancels any updates to the current row, not supported in this implementation.

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Moves to the insert row, not supported in this implementation.
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Moves to the current row, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        // (AI Comment) - Retrieves the statement associated with this result set.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves a Ref based on the specified column index, not supported in this implementation.
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves a Blob based on the specified column label, not supported in this implementation.
    // ---

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Retrieves a Blob based on the specified column index, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader)
            // (AI Comment) - Retrieves a Clob based on the specified column label, not supported in this implementation.
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Retrieves a Clob based on the specified column index, not supported in this implementation.
    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }
    // (AI Comment) - Retrieves an Array based on the specified column index, not supported in this implementation.

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Retrieves a Ref based on the specified column label, not supported in this implementation.
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Retrieves an Array based on the specified column label, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
        // (AI Comment) - Retrieves a RowId based on the specified column index, not supported in this implementation.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Retrieves a RowId based on the specified column label, not supported in this implementation.
    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // (AI Comment) - Updates the current row to a RowId value, not supported in this implementation.
    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to a RowId value based on the specified column label, not supported in this implementation.
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Retrieves the holdability of the result set, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        // (AI Comment) - Checks if the result set is closed.
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NString value, not supported in this implementation.
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NString value based on the specified column label, not supported in this implementation.
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NClob value, not supported in this implementation.
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an NClob value based on the specified column label, not supported in this implementation.
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    }

    // ------------------------- JDBC 4.2 -----------------------------------
    // (AI Comment) - Retrieves an NClob based on the specified column index, not supported in this implementation.

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Retrieves an NClob based on the specified column label, not supported in this implementation.
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Retrieves SQLXML based on the specified column index, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException(
                // (AI Comment) - Retrieves SQLXML based on the specified column label, not supported in this implementation.
                Thread.currentThread().getStackTrace()[1].toString());
    }

    @Override
    // (AI Comment) - Updates the current row to an SQLXML value, not supported in this implementation.
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType)
            throws SQLException {
        throw new SQLFeatureNotSupportedException(
                Thread.currentThread().getStackTrace()[1].toString());
    // (AI Comment) - Updates the current row to an SQLXML value based on the specified column label, not supported in this implementation.
    }

    // java.sql.Wrapper impl
    @Override
    // (AI Comment) - Checks if the current object is an instance of the specified interface.
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    // (AI Comment) - Unwraps the current object as the specified interface type.
    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) this;
    }
}
