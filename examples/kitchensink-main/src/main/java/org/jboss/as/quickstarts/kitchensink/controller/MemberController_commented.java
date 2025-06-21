/*
********* AI-Assistant Documentation for - MemberController_commented.java *********
The MemberController class is responsible for managing member registrations in a Java EE application. It handles user input, validates member details, interacts with the member registration service, and provides feedback to the user through the UI.
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

@Controller
@ViewScoped
// (AI Comment) - The MemberController class manages member registration and retrieval, handling user input and interactions with the member registration service.
public class MemberController {
    private final MemberRegistration memberRegistration;
    private final MemberListProducer memberListProducer;
    private Member newMember;
    private List<Member> members;

    // (AI Comment) - Constructor that initializes the MemberRegistration and MemberListProducer dependencies via dependency injection.
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

    // (AI Comment) - Registers a new member, validates input, and handles success or error messages using FacesContext.
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

    // (AI Comment) - Retrieves the root error message from an exception, traversing the cause chain if necessary.
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

    // (AI Comment) - Getter for the list of members.
    public List<Member> getMembers() {
        return members;
    }

    // (AI Comment) - Setter for the list of members.
    public void setMembers(List<Member> members) {
        this.members = members;
    }

    // (AI Comment) - Getter for the new member instance.
    public Member getNewMember() {
        return newMember;
    }

    // (AI Comment) - Setter for the new member instance.
    public void setNewMember(Member newMember) {
        this.newMember = newMember;
    }
}
