/*
********* AI-Assistant Documentation for - MongoColumnInfo_commented.java *********
The 'MongoColumnInfo.java' file defines a class that holds metadata for MongoDB columns, including their data source, field names, BSON type information, and nullability. It provides methods to access this information, facilitating the integration of MongoDB with JDBC.
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

// (AI Comment) - Defines the package for MongoDB JDBC integration.
package com.mongodb.jdbc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.BsonType;

// (AI Comment) - Represents metadata for a MongoDB column, including its data source, field name, BSON type information, and nullability.
public class MongoColumnInfo {
    private final String datasource;
    private final String field;
    private final BsonTypeInfo bsonTypeInfo;
    private final boolean isPolymorphic;
    private final int nullable;

    // (AI Comment) - Constructor that initializes the MongoColumnInfo with the specified data source, field name, BSON type information, and nullability.
    MongoColumnInfo(String datasource, String field, BsonTypeInfo bsonTypeInfo, int nullability) {
        this.datasource = datasource;
        this.field = field;
        this.bsonTypeInfo = bsonTypeInfo;
        this.nullable = nullability;
        this.isPolymorphic = bsonTypeInfo == BsonTypeInfo.BSON_BSON;
    }

    // (AI Comment) - Returns a string representation of the MongoColumnInfo object using reflection.
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    // (AI Comment) - Checks if the column is polymorphic based on its BSON type information.
    public boolean isPolymorphic() {
        return isPolymorphic;
    }

    // (AI Comment) - Retrieves the BSON type enumeration for the column.
    public BsonType getBsonTypeEnum() {
        return bsonTypeInfo.getBsonType();
    }

    // (AI Comment) - Gets the BSON type name for the column.
    public String getBsonTypeName() {
        return bsonTypeInfo.getBsonName();
    }

    // (AI Comment) - Retrieves the JDBC type for the column based on its BSON type information.
    public int getJDBCType() {
        return bsonTypeInfo.getJdbcType();
    }

    // (AI Comment) - Gets the nullability of the column.
    public int getNullability() {
        return nullable;
    }

    // (AI Comment) - Returns the name of the column.
    public String getColumnName() {
        return field;
    }

    // (AI Comment) - Returns the alias of the column, which is the same as the column name.
    public String getColumnAlias() {
        return field;
    }

    // (AI Comment) - Returns the name of the table associated with the column.
    public String getTableName() {
        return datasource;
    }

    // (AI Comment) - Returns the alias of the table, which is the same as the data source name.
    public String getTableAlias() {
        return datasource;
    }

    // (AI Comment) - Returns an empty string for the database name, indicating no specific database is associated.
    public String getDatabase() {
        return "";
    }
}
