/*
********* AI-Assistant Documentation for - Main_commented.java *********
The Main class initializes a Spring Boot application, configuring it to use MongoDB for data storage and handling application startup errors.
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
        // (AI Comment) - Try-catch block to manage exceptions that may occur during application startup, logging any errors encountered.
        try {
            SpringApplication.run(ApplicationConfiguration.class, args);
        } catch (Exception e) {
            LoggerFactory.getLogger(Main.class).error(e.getStackTrace().toString(), e);
        }
    }
}
