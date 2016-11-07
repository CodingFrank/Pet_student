/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.repository.jpa;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;
import org.springframework.samples.petclinic.model.Student;
import org.springframework.samples.petclinic.repository.StudentRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA implementation of the {@link StudentRepository} interface.
 *
 * @author Frank Liu
 */
@Repository
public class JpaStudentRepositoryImpl implements StudentRepository {

    @PersistenceContext
    private EntityManager em;


    /**
     * Important: in the current version of this method, we load Students with all their Pets and Visits while
     * we do not need Visits at all and we only need one property from the Pet objects (the 'name' property).
     * There are some ways to improve it such as:
     * - creating a Ligtweight class (example here: https://community.jboss.org/wiki/LightweightClass)
     * - Turning on lazy-loading and using {@link OpenSessionInViewFilter}
     */
    @SuppressWarnings("unchecked")
    public Collection<Student> findByLastName(String lastName) {
        // using 'join fetch' because a single query should load both students and pets
        // using 'left join fetch' because it might happen that an student does not have pets yet
        Query query = this.em.createQuery("SELECT DISTINCT student FROM Student student WHERE student.lastName LIKE :lastName");
        query.setParameter("lastName", lastName + "%");
        return query.getResultList();
    }

    @Override
    public Student findById(int id) {
        // using 'join fetch' because a single query should load both students and pets
        // using 'left join fetch' because it might happen that an student does not have pets yet
        Query query = this.em.createQuery("SELECT student FROM Student student WHERE student.id =:id");
        query.setParameter("id", id);
        return (Student) query.getSingleResult();
    }


    @Override
    public void save(Student student) {
        if (student.getId() == null) {
            this.em.persist(student);
        } else {
            this.em.merge(student);
        }

    }

}
