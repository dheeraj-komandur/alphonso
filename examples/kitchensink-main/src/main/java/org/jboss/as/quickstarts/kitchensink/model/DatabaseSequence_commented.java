/*
********* AI-Assistant Documentation for - DatabaseSequence_commented.java *********
The DatabaseSequence class serves as a model for storing and managing unique identifier sequences in a MongoDB collection, facilitating the generation of unique IDs for application entities.
*/

// (AI Comment) - Defines the DatabaseSequence class, which represents a sequence in a MongoDB collection for generating unique identifiers.
package org.jboss.as.quickstarts.kitchensink.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document(collection = "database_sequences")
public class DatabaseSequence {
    // (AI Comment) - Holds the unique identifier for the sequence, annotated with @Id for MongoDB.
    @Id
    private String id;

    // (AI Comment) - Stores the current value of the sequence as a BigInteger.
    private BigInteger sequence;

    // (AI Comment) - Retrieves the unique identifier of the sequence.
    public String getId() {
        return id;
    }

    // (AI Comment) - Sets the unique identifier for the sequence.
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
