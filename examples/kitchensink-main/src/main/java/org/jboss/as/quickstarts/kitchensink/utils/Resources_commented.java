/*
********* AI-Assistant Documentation for - Resources_commented.java *********
The 'Resources.java' file is responsible for configuring and providing a prototype-scoped Logger bean for use in dependency injection within a Spring application, facilitating class-specific logging.
*/

// (AI Comment) - Defines a Spring configuration class that provides a prototype-scoped Logger bean for dependency injection.
package org.jboss.as.quickstarts.kitchensink.utils;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.logging.Logger;

@Configuration
public class Resources {

    // (AI Comment) - Produces a Logger instance based on the class where it is injected, allowing for class-specific logging.
    @Bean
    @Scope("prototype")
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return Logger.getLogger(classOnWired.getName());
    }
}
