/*
********* AI-Assistant Documentation for - MongoDBConfig_commented.java *********
The MongoDBConfig class configures a MongoDB container for integration testing using Testcontainers, ensuring a consistent and isolated database environment for tests.
*/

// (AI Comment) - Configuration class for setting up a MongoDB container using Testcontainers for integration testing.
package org.jboss.as.quickstarts.kitchensink.test.config;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
// (AI Comment) - Defines the MongoDBConfig class responsible for initializing the MongoDB container.
public class MongoDBConfig {
    // (AI Comment) - Static MongoDBContainer instance configured to use the latest MongoDB image and expose port 27017.
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    // (AI Comment) - Static initializer block that starts the MongoDB container and sets the mapped port as a system property.
    static {
        // (AI Comment) - Starts the MongoDB container and retrieves the mapped port to set it as a system property.
        mongoDBContainer.start();
        Integer port = mongoDBContainer.getMappedPort(27017);
        System.setProperty("mongodb.container.port", String.valueOf(port));
    }
}
