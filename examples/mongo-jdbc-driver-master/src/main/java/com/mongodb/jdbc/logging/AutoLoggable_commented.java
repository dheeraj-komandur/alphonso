/*
********* AI-Assistant Documentation for - AutoLoggable_commented.java *********
This file defines the AutoLoggable annotation, which is used to mark classes and methods for automatic logging of public method entries in the MongoDB JDBC driver.
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

// (AI Comment) - Package declaration for logging-related classes in the MongoDB JDBC module.
package com.mongodb.jdbc.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// (AI Comment) - Defines an annotation for classes and methods that should log their public method entries, facilitating automatic logging via LoggingAspect.
/**
 * Annotation for identifying all classes which should log their public method entries. Used in
 * conjunction with LoggingAspect to provide auto-logging of public methods entry.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AutoLoggable {}
