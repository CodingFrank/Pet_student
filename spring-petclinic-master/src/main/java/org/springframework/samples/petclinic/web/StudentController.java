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
package org.springframework.samples.petclinic.web;

import java.util.Collection;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Student;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.apache.log4j.Logger;

/**
 * @author Frank Liu
 */
@Controller
public class StudentController {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "students/createOrUpdateStudentForm";
    private final ClinicService clinicService;


    @Autowired
    public StudentController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/students/new", method = RequestMethod.GET)
    public String initCreationForm(Map<String, Object> model) {
        Student student = new Student();
        model.put("student", student);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @RequestMapping(value = "/students/new", method = RequestMethod.POST)
    public String processCreationForm(@Valid Student student, BindingResult result) {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            this.clinicService.saveStudent(student);
            return "redirect:/students/" + student.getId();
        }
    }

    @RequestMapping(value = "/students/find", method = RequestMethod.GET)
    public String initFindForm(Map<String, Object> model) {
        model.put("student", new Student());
        return "students/findStudents";
    }

    @RequestMapping(value = "/students", method = RequestMethod.GET)
    public String processFindForm(Student student, BindingResult result, Map<String, Object> model) {

        // allow parameterless GET request for /students to return all records
        if (student.getLastName() == null) {
            student.setLastName(""); // empty string signifies broadest possible search
        }

        // find students by last name
        Collection<Student> results = this.clinicService.findStudentByLastName(student.getLastName());
        if (results.isEmpty()) {
            // no students found
            result.rejectValue("lastName", "notFound", "not found");
            return "students/findStudents";
        } else if (results.size() == 1) {
            // 1 student found
            student = results.iterator().next();
            return "redirect:/students/" + student.getId();
        } else {
            // multiple students found
            model.put("selections", results);
            return "students/studentsList";
        }
    }

    @RequestMapping(value = "/students/{studentId}/edit", method = RequestMethod.GET)
    public String initUpdateStudentForm(@PathVariable("studentId") int studentId, Model model) {
        Student student = this.clinicService.findStudentById(studentId);
        model.addAttribute(student);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @RequestMapping(value = "/students/{studentId}/edit", method = RequestMethod.POST)
    public String processUpdateStudentForm(@Valid Student student, BindingResult result, @PathVariable("studentId") int studentId) {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            student.setId(studentId);
            this.clinicService.saveStudent(student);
            return "redirect:/students/{studentId}";
        }
    }

    /**
     * Custom handler for displaying an student.
     *
     * @param studentId the ID of the student to display
     * @return a ModelMap with the model attributes for the view
     */
    @RequestMapping("/students/{studentId}")
    public ModelAndView showStudent(@PathVariable("studentId") int studentId) {
        ModelAndView mav = new ModelAndView("students/studentDetails");
        mav.addObject(this.clinicService.findStudentById(studentId));
        return mav;
    }

}
