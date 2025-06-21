/*
********* AI-Assistant Documentation for - MemberRegistration_commented.java *********
The 'MemberRegistration.java' file defines a service for registering members in a MongoDB database. It handles the generation of unique identifiers for members and manages interactions with the database through the MemberRepository and MongoOperations interfaces.
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
// (AI Comment) - Defines the package for the MemberRegistration service, which handles member registration logic.
package org.jboss.as.quickstarts.kitchensink.service;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.DatabaseSequence;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Objects;
import java.util.logging.Logger;

// (AI Comment) - The MemberRegistration class is responsible for registering new members, managing their unique identifiers, and interacting with the MongoDB database.
@Service
public class MemberRegistration {
    private final Logger log;

    private final MongoOperations mongoOperations;

    private final MemberRepository memberRepository;

    // (AI Comment) - Constructor that initializes the MemberRegistration service with MongoOperations and MemberRepository dependencies.
    @Autowired
    public MemberRegistration(final MongoOperations mongoOperations, final MemberRepository memberRepository, MongoClient mongo) {
        log = Logger.getLogger(getClass().getName());
        this.mongoOperations = mongoOperations;
        this.memberRepository = memberRepository;
    }

    // (AI Comment) - Registers a new member by generating a unique ID and inserting the member into the repository, handling potential write exceptions.
    public void register(Member member) throws Exception {
        member.setId(generateSequence(Member.SEQUENCE_NAME));
        try {
            memberRepository.insert(member);
        } catch (MongoWriteException e) {
            throw new Exception(e.getLocalizedMessage());
        }

    }

    // (AI Comment) - Generates a unique sequence number for a member by incrementing the sequence in the database, returning BigInteger.ONE if no sequence is found.
    private BigInteger generateSequence(String sequenceName) {
        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("sequence", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class
        );
        return !Objects.isNull(counter) ? counter.getSequence() : BigInteger.ONE;
    }
}
