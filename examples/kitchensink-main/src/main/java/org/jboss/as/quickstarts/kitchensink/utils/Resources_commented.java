/*
********* AI-Assistant Documentation for - Resources_commented.java *********
The 'Resources.java' file provides a Spring configuration class that defines a prototype-scoped Logger bean for dependency injection, enabling class-specific logging throughout the application.
*/

package org.jboss.as.quickstarts.kitchensink.utils;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.logging.Logger;

@Configuration
// (AI Comment) - Defines a Spring configuration class that provides a prototype-scoped Logger bean for dependency injection.
public class Resources {

    @Bean
    @Scope("prototype")
    // (AI Comment) - Produces a Logger instance that is scoped to the lifecycle of the injection point, allowing for class-specific logging.
    public Logger produceLogger(InjectionPoint injectionPoint) {
        Class<?> classOnWired = injectionPoint.getMember().getDeclaringClass();
        return Logger.getLogger(classOnWired.getName());
    }
}
