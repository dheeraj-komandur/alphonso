/*
********* AI-Assistant Documentation for - MemberRepository_commented.java *********
The MemberRepository interface provides a data access layer for Member entities in a Spring application, enabling CRUD operations and custom queries using MongoDB.
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
// (AI Comment) - Defines the package for the MemberRepository interface, which handles data access for Member entities.
package org.jboss.as.quickstarts.kitchensink.data;

import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

// (AI Comment) - Repository interface for Member entities, extending MongoRepository to provide CRUD operations and custom queries.
@Repository
public interface MemberRepository extends MongoRepository<Member, String> {
    // (AI Comment) - Retrieves a Member entity by its unique identifier (BigInteger id).
    Member findById(BigInteger id);

    // (AI Comment) - Retrieves a Member entity by its email address (String email).
    Member findByEmail(String email);

    // (AI Comment) - Retrieves all Member entities sorted in ascending order by their name.
    List<Member> findAllByOrderByNameAsc();

    // (AI Comment) - Deletes a Member entity by its unique identifier (BigInteger id) and returns the deleted entity.
    Member deleteMemberById(BigInteger id);

    // (AI Comment) - Deletes a Member entity by its email address (String email) and returns the deleted entity.
    Member deleteMemberByEmail(String email);
}
