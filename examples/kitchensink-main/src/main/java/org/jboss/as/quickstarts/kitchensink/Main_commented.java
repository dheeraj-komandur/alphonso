/*
********* AI-Assistant Documentation for - Main_commented.java *********
The 'Main.java' file is the entry point for a Spring Boot application that integrates with MongoDB. It initializes the application context and configures the necessary repositories, ensuring that the application can start and log any errors encountered during the startup process.
*/

// (AI Comment) - Main class serves as the entry point for the Spring Boot application, initializing the application context and configuring MongoDB repositories.
package org.jboss.as.quickstarts.kitchensink;

import org.jboss.as.quickstarts.kitchensink.config.ApplicationConfiguration;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@Import(value = MongoAutoConfiguration.class)
@EnableMongoRepositories(basePackageClasses = MemberRepository.class)
public class Main extends SpringBootServletInitializer {
    // (AI Comment) - Main method that launches the Spring Boot application, handling exceptions during startup and logging errors.
    public static void main(String[] args) {
        // (AI Comment) - Try-catch block to manage exceptions during application startup, ensuring that any errors are logged appropriately.
        try {
            SpringApplication.run(ApplicationConfiguration.class, args);
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error(e.getStackTrace().toString(), e);
        }
    }
}
