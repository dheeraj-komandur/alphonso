/*
********* AI-Assistant Documentation for - MemberController_commented.java *********
The 'MemberController.java' file is responsible for managing member registrations in a Java EE application. It handles the registration process, validates user input, and interacts with services to retrieve and manage member data. The controller ensures that user feedback is provided through messages, maintaining a clear separation of concerns in the MVC architecture.
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
// (AI Comment) - Defines the package for the MemberController class, which handles member registration and management.
package org.jboss.as.quickstarts.kitchensink.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.jboss.as.quickstarts.kitchensink.data.MemberListProducer;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

// (AI Comment) - The MemberController class is responsible for managing member registration and retrieval, utilizing services for member data.
@Controller
@ViewScoped
public class MemberController {
    private final MemberRegistration memberRegistration;
    private final MemberListProducer memberListProducer;
    private Member newMember;
    private List<Member> members;

    // (AI Comment) - Constructor that initializes the MemberRegistration and MemberListProducer services used for member operations.
    @Autowired
    public MemberController(MemberRegistration memberRegistration, MemberListProducer memberListProducer) {
        this.memberRegistration = memberRegistration;
        this.memberListProducer = memberListProducer;
    }

    // (AI Comment) - PostConstruct method that initializes a new member and retrieves the list of members ordered by name.
    @PostConstruct
    public void refresh() {
        newMember = new Member();
        memberListProducer.retrieveAllMembersOrderedByName();
        members = memberListProducer.getMembers();
    }

    // (AI Comment) - Registers a new member, validates input, and handles success or error messages based on the registration outcome.
    public void register() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (newMember.getName().isEmpty() || newMember.getEmail().isEmpty() || newMember.getPhoneNumber().isEmpty()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid member details", "One or more member details is blank"));
        }
        try {
            memberRegistration.register(newMember);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
            facesContext.addMessage(null, msg);
            refresh();
        } catch (Exception e) {
            String errorMessage = getRootErrorMessage(e);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
            facesContext.addMessage(null, msg);
        }
    }

    // (AI Comment) - Retrieves the root error message from an exception, traversing the cause chain to find the most specific message.
    private String getRootErrorMessage(Exception e) {
        String errorMessage = "Registration failed";
        if (e == null) {
            return errorMessage;
        }

        Throwable cause = e;
        while (cause != null) {
            errorMessage = cause.getLocalizedMessage();
            cause = cause.getCause();
        }

        return errorMessage;
    }

    // (AI Comment) - Getter method for the list of members.
    public List<Member> getMembers() {
        return members;
    }

    // (AI Comment) - Setter method for the list of members.
    public void setMembers(List<Member> members) {
        this.members = members;
    }

    // (AI Comment) - Getter method for the new member being registered.
    public Member getNewMember() {
        return newMember;
    }

    // (AI Comment) - Setter method for the new member being registered.
    public void setNewMember(Member newMember) {
        this.newMember = newMember;
    }
}
