/*
********* AI-Assistant Documentation for - DatabaseSequence_commented.java *********
This file defines the DatabaseSequence class, which models a MongoDB document for storing and managing database sequence information, including an identifier and the sequence value.
*/

// (AI Comment) - Represents a MongoDB document for storing database sequence information, including an identifier and the sequence value.
package org.jboss.as.quickstarts.kitchensink.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "database_sequences")
public class DatabaseSequence {
    // (AI Comment) - Unique identifier for the database sequence, annotated with @Id for MongoDB.
    @Id
    private String id;

    // (AI Comment) - Holds the current value of the sequence as a BigInteger.
    private BigInteger sequence;

    // (AI Comment) - Retrieves the unique identifier of the database sequence.
    public String getId() {
        return id;
    }

    // (AI Comment) - Sets the unique identifier for the database sequence.
    public void setId(String id) {
        this.id = id;
    }

    // (AI Comment) - Retrieves the current value of the sequence.
    public BigInteger getSequence() {
        return sequence;
    }

    // (AI Comment) - Sets the current value of the sequence.
    public void setSequence(BigInteger sequence) {
        this.sequence = sequence;
    }
}
