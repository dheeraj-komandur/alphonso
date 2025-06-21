/*
********* AI-Assistant Documentation for - MemberListProducer_commented.java *********
The MemberListProducer class is responsible for managing and providing access to a list of members in the kitchensink application. It retrieves members from a repository and updates the list in response to events, ensuring that the data remains current and accessible.
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
// (AI Comment) - Defines the package for the MemberListProducer class, which is part of the kitchensink data module.
package org.jboss.as.quickstarts.kitchensink.data;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


// (AI Comment) - The MemberListProducer class is responsible for producing a list of members from the MemberRepository and notifying observers when the member list changes.
@Component
public class MemberListProducer {
    private final MemberRepository memberRepository;
    private List<Member> members;

    // (AI Comment) - Constructor that initializes the MemberListProducer with a MemberRepository instance, ensuring dependency injection.
    @Autowired
    public MemberListProducer(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // (AI Comment) - Returns the current list of members managed by this producer.
    public List<Member> getMembers() {
        return members;
    }

    // (AI Comment) - Handles events indicating that the member list has changed, triggering a refresh of the member list.
    public void onMemberListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Member member) {
        retrieveAllMembersOrderedByName();
    }

    // (AI Comment) - Retrieves all members from the repository, ordered by name, and updates the internal members list.
    @PostConstruct
    public void retrieveAllMembersOrderedByName() {
        members = memberRepository.findAllByOrderByNameAsc();
    }
}
