/*
********* AI-Assistant Documentation for - Member_commented.java *********
The 'Member.java' file defines a Member class that models a member entity with attributes such as id, email, name, and phone number, incorporating validation constraints to ensure data integrity and proper formatting.
*/

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// (AI Comment) - Defines the package for the Member class, organizing it under the kitchensink model in the JBoss quickstart project.
package org.jboss.as.quickstarts.kitchensink.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;

@Document(collection = "members")
public class Member implements Serializable {
    // (AI Comment) - Represents a Member entity in the application, encapsulating member details such as id, email, name, and phone number, with validation constraints for data integrity.

    // (AI Comment) - Static constant representing the sequence name for member IDs, marked as transient to avoid persistence.
    @Transient
    public static final String SEQUENCE_NAME = "members_sequence";

    // (AI Comment) - Unique identifier for the Member, annotated with @Id for MongoDB document mapping.
    @Id
    private BigInteger id;

    // (AI Comment) - Email address of the member, validated to be non-empty, unique, and in proper email format.
    @NotEmpty
    @Email
    @Indexed(unique = true)
    private String email;

    // (AI Comment) - Name of the member, validated to be non-empty, with a maximum length of 25 characters and must not contain numbers.
    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[^0-9]*", message = "Must not contain numbers")
    private String name;

    // (AI Comment) - Phone number of the member, validated to be non-null, with a length between 10 and 12 digits.
    @NotNull
    @Size(min = 10, max = 12)
    @Digits(fraction = 0, integer = 12)
    private String phoneNumber;

    // (AI Comment) - Getter method for the member's ID, returning the BigInteger id.
    public BigInteger getId() {
        return id;
    }

    // (AI Comment) - Setter method for the member's ID, allowing modification of the BigInteger id.
    public void setId(BigInteger id) {
        this.id = id;
    }

    // (AI Comment) - Getter method for the member's email, returning the String email.
    public String getEmail() {
        return email;
    }

    // (AI Comment) - Setter method for the member's email, allowing modification of the String email.
    public void setEmail(String email) {
        this.email = email;
    }

    // (AI Comment) - Getter method for the member's name, returning the String name.
    public String getName() {
        return name;
    }

    // (AI Comment) - Setter method for the member's name, allowing modification of the String name.
    public void setName(String name) {
        this.name = name;
    }

    // (AI Comment) - Getter method for the member's phone number, returning the String phoneNumber.
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // (AI Comment) - Setter method for the member's phone number, allowing modification of the String phoneNumber.
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
