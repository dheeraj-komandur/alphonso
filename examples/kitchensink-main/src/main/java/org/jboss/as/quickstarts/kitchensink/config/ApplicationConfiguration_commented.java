/*
********* AI-Assistant Documentation for - ApplicationConfiguration_commented.java *********
The 'ApplicationConfiguration.java' file is responsible for configuring the application by initializing MongoDB collections and setting up validation listeners upon application startup. It ensures that necessary database structures are in place for the application to function correctly.
*/

// (AI Comment) - Defines the package for the application configuration.
package org.jboss.as.quickstarts.kitchensink.config;

import org.jboss.as.quickstarts.kitchensink.model.DatabaseSequence;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

// (AI Comment) - Configuration class that initializes MongoDB collections and validation listeners upon application startup.
@Configuration
public class ApplicationConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    private final MongoOperations mongoOperations;

    // (AI Comment) - Constructor that injects MongoOperations for database operations.
    @Autowired
    public ApplicationConfiguration(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    // (AI Comment) - Handles the ApplicationReadyEvent to create necessary MongoDB collections if they do not exist.
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        if (!mongoOperations.collectionExists(DatabaseSequence.class)) {
            mongoOperations.createCollection(DatabaseSequence.class);
        }
        if (!mongoOperations.collectionExists(Member.class)) {
            mongoOperations.createCollection(Member.class);
        }
    }

    // (AI Comment) - Creates a bean for ValidatingMongoEventListener to validate MongoDB events.
    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(final LocalValidatorFactoryBean factory) {
        return new ValidatingMongoEventListener(factory);
    }

    // (AI Comment) - Creates a bean for LocalValidatorFactoryBean to provide validation support.
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
