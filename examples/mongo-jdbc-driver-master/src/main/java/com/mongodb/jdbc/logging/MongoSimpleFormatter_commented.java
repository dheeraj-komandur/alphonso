/*
********* AI-Assistant Documentation for - MongoSimpleFormatter_commented.java *********
This file provides a custom log formatter for MongoDB JDBC, allowing for structured and readable log output that includes timestamps, log levels, and exception details.
*/

/*
 * Copyright 2023-present MongoDB, Inc.
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

// (AI Comment) - Defines the package for logging utilities specific to MongoDB JDBC.
package com.mongodb.jdbc.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

// (AI Comment) - Implements a custom log formatter for MongoDB JDBC, extending the Formatter class to format log messages with a specific pattern.
public class MongoSimpleFormatter extends Formatter {
    private final String format = "[%1$tF %1$tT.%1$tL] [%4$s] %2$s: %5$s %6$s %n";
    private final Date date = new Date();

    // (AI Comment) - Overrides the format method to customize the log message format, including timestamp, source, and exception stack trace if present.
    @Override
    public String format(LogRecord record) {
        // (AI Comment) - Sets the date based on the log record's timestamp and constructs the source string from the class and method names if available.
        date.setTime(record.getMillis());
        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }
        String message = formatMessage(record);
        String throwable = "";
        // (AI Comment) - Checks if the log record contains a throwable and captures its stack trace for inclusion in the formatted log message.
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        // (AI Comment) - Formats the final log message string using the specified format pattern and the extracted components.
        return String.format(
                format,
                date,
                source,
                record.getLoggerName(),
                record.getLevel().getLocalizedName(),
                message,
                throwable);
    }
}
