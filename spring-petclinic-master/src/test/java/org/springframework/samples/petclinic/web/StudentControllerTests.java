package org.springframework.samples.petclinic.web;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Student;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link StudentController}
 *
 * @author Frank Liu
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
@WebAppConfiguration
public class StudentControllerTests {

    private static final int TEST_STUDENT_ID = 1;

    @Autowired
    private StudentController studentController;

    @Autowired
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private Student dave;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();

        dave = new Student();
        dave.setId(TEST_STUDENT_ID);
        dave.setFirstName("Dave");
        dave.setLastName("Ward");
        dave.setAddress("110 W. Liberty St.");
        dave.setCity("Troy");
        dave.setTelephone("6085551023");
        dave.setDepartment("ITWS");
        given(this.clinicService.findStudentById(TEST_STUDENT_ID)).willReturn(dave);

    }

    @Test
    public void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/students/new"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("student"))
            .andExpect(view().name("students/createOrUpdateStudentForm"));
    }

    @Test
    public void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/students/new")
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("address", "123 Caramel Street")
            .param("city", "London")
            .param("telephone", "01316761638")
            .param("department", "ITWS")
        )
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/students/new")
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("city", "London")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("student"))
            .andExpect(model().attributeHasFieldErrors("student", "address"))
            .andExpect(model().attributeHasFieldErrors("student", "telephone"))
            .andExpect(model().attributeHasFieldErrors("student", "department"))
            .andExpect(view().name("students/createOrUpdateStudentForm"));
    }

    @Test
    public void testInitFindForm() throws Exception {
        mockMvc.perform(get("/students/find"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("student"))
            .andExpect(view().name("students/findStudents"));
    }

    @Test
    public void testProcessFindFormSuccess() throws Exception {
        given(this.clinicService.findStudentByLastName("")).willReturn(Lists.newArrayList(dave, new Student()));

        mockMvc.perform(get("/students"))
            .andExpect(status().isOk())
            .andExpect(view().name("students/studentsList"));
    }

    @Test
    public void testProcessFindFormByLastName() throws Exception {
        given(this.clinicService.findStudentByLastName(dave.getLastName())).willReturn(Lists.newArrayList(dave));

        mockMvc.perform(get("/students")
            .param("lastName", "Ward")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/students/" + TEST_STUDENT_ID));
    }

    @Test
    public void testProcessFindFormNoStudentsFound() throws Exception {
        mockMvc.perform(get("/students")
            .param("lastName", "Unknown Surname")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("student", "lastName"))
            .andExpect(model().attributeHasFieldErrorCode("student", "lastName", "notFound"))
            .andExpect(view().name("students/findStudents"));
    }

    @Test
    public void testInitUpdateStudentForm() throws Exception {
        mockMvc.perform(get("/students/{studentId}/edit", TEST_STUDENT_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("student"))
            .andExpect(model().attribute("student", hasProperty("lastName", is("Ward"))))
            .andExpect(model().attribute("student", hasProperty("firstName", is("Dave"))))
            .andExpect(model().attribute("student", hasProperty("address", is("110 W. Liberty St."))))
            .andExpect(model().attribute("student", hasProperty("city", is("Troy"))))
            .andExpect(model().attribute("student", hasProperty("telephone", is("6085551023"))))
            .andExpect(model().attribute("student", hasProperty("department", is("ITWS"))))
            .andExpect(view().name("students/createOrUpdateStudentForm"));
    }

    @Test
    public void testProcessUpdateStudentFormSuccess() throws Exception {
        mockMvc.perform(post("/students/{studentId}/edit", TEST_STUDENT_ID)
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("address", "123 Caramel Street")
            .param("city", "London")
            .param("telephone", "01616291589")
            .param("department", "ITWS")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/students/{studentId}"));
    }

    @Test
    public void testProcessUpdateStudentFormHasErrors() throws Exception {
        mockMvc.perform(post("/students/{studentId}/edit", TEST_STUDENT_ID)
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("city", "London")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("student"))
            .andExpect(model().attributeHasFieldErrors("student", "address"))
            .andExpect(model().attributeHasFieldErrors("student", "telephone"))
            .andExpect(model().attributeHasFieldErrors("student", "department"))
            .andExpect(view().name("students/createOrUpdateStudentForm"));
    }

    @Test
    public void testShowStudent() throws Exception {
        mockMvc.perform(get("/students/{studentId}", TEST_STUDENT_ID))
            .andExpect(status().isOk())
            .andExpect(model().attribute("student", hasProperty("lastName", is("Ward"))))
            .andExpect(model().attribute("student", hasProperty("firstName", is("Dave"))))
            .andExpect(model().attribute("student", hasProperty("address", is("110 W. Liberty St."))))
            .andExpect(model().attribute("student", hasProperty("city", is("Troy"))))
            .andExpect(model().attribute("student", hasProperty("telephone", is("6085551023"))))
            .andExpect(model().attribute("student", hasProperty("department", is("ITWS"))))
            .andExpect(view().name("students/studentDetails"));
    }

}
