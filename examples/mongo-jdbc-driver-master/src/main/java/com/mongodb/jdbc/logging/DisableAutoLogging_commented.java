/*
********* AI-Assistant Documentation for - DisableAutoLogging_commented.java *********
This file defines the 'DisableAutoLogging' annotation, which is used to mark methods and classes that should be excluded from automatic logging in the MongoDB JDBC driver. It facilitates selective logging by working in conjunction with the LoggingAspect.
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
// (AI Comment) - This section contains the copyright notice and licensing information for the file.

package com.mongodb.jdbc.logging;
// (AI Comment) - Defines the package for logging-related functionality in the MongoDB JDBC driver.

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 // (AI Comment) - Defines an annotation to mark methods or classes that should not be automatically logged by the LoggingAspect.
 * Annotation for identifying all methods which should be excluded from autologging the method
 * entry. Used in conjunction with LoggingAspect to provide auto-logging of public methods entry.
 */
@Retention(RetentionPolicy.CLASS)
// (AI Comment) - Specifies that this annotation is retained in the class file and can be applied to types and methods.
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DisableAutoLogging {}
