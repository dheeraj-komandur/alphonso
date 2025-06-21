/*
********* AI-Assistant Documentation for - MemberRegistrationIT_commented.java *********
This file contains integration tests for the MemberRegistration service in the kitchensink application, ensuring that new members can be registered and persisted correctly.
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
// (AI Comment) - Defines the package for the integration tests related to the kitchensink application.
package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.Main;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensink.test.config.MongoDBConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@Testcontainers
@SpringBootTest(classes = {Main.class, MongoDBConfig.class})
// (AI Comment) - Integration test class for MemberRegistration, verifying member registration functionality.
public class MemberRegistrationIT {
    @Autowired
    MemberRegistration memberRegistration;

    @Autowired
    Logger log;

    // (AI Comment) - Tests the registration of a new member, ensuring that the member is persisted and has a generated ID.
    @Test
    public void testRegister() {
        Member newMember = new Member();
        newMember.setName("Jane Doe");
        newMember.setEmail("jane@mailinator.com");
        newMember.setPhoneNumber("2125551234");
        // (AI Comment) - Handles exceptions during member registration and fails the test if an exception occurs.
        try {
            memberRegistration.register(newMember);
            assertNotNull(newMember.getId());
            log.info(newMember.getName() + " was persisted with id " + newMember.getId());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
