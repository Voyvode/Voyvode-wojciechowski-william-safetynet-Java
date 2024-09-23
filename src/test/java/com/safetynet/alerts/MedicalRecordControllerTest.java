package com.safetynet.alerts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.safetynet.alerts.config.JsonTestConfig.SAMPLE_PATH;
import static com.safetynet.alerts.config.JsonTestConfig.SAMPLE_ORIG_PATH;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() throws IOException {
        Files.copy(Paths.get(SAMPLE_ORIG_PATH), Paths.get(SAMPLE_PATH), REPLACE_EXISTING);
    }

    @Test
    @Order(1)
    public void testCreateMedicalRecord() throws Exception {
        // add corresponding person required for medical record
        mockMvc.perform(post("/person")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"John", "lastName":"Doe",
                                "address":"123 Test St", "city":"Nowhere", "zip":"12345",
                                "phone":"123-456-7890", "email":"jdoe@mail.com" }
                                """));

        mockMvc.perform(post("/medicalRecord")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"John", "lastName":"Doe",
                                "birthdate":"04/01/1984",
                                "medications":["doexetin:300mg"], "allergies":["doenuts"] }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void testCreateMedicalRecordConflict() throws Exception {
        mockMvc.perform(post("/medicalRecord")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"John", "lastName":"Doe",
                                "birthdate":"12/25/1492",
                                "medications":[], "allergies":[] }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    public void testCreateMedicalWithoutPerson() throws Exception {
        mockMvc.perform(post("/medicalRecord")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"Me", "lastName":"Noexist",
                                "birthdate":"01/01/0001",
                                "medications":["nilapranil:0mg"], "allergies":["being"] }
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Order(4)
    public void testUpdateMedicalRecord() throws Exception {
        mockMvc.perform(put("/medicalRecord/JohnDoe")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"John", "lastName":"Doe",
                                "birthdate":"04/01/1984",
                                "medications":["Doeliprane:1000mg"], "allergies":["doenuts", "Knockandoe"] }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void testUpdateMedicalRecordNotFound() throws Exception {
        mockMvc.perform(put("/medicalRecord/MeNoexist")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"Me", "lastName":"Noexist",
                                "birthdate":"01/01/0001",
                                "medications":["nilapranil:0mg"], "allergies":["being"] }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    public void testDeleteMedicalRecord() throws Exception {
        mockMvc.perform(delete("/medicalRecord/JohnDoe"))
                .andExpect(status().isNoContent());

        // remove corresponding person after medical record deletion
        mockMvc.perform(delete("/person/JohnDoe"));
    }

    @Test
    @Order(7)
    public void testDeleteMedicalRecordNotFound() throws Exception {
        mockMvc.perform(delete("/medicalRecord/MeNoexist"))
                .andExpect(status().isNotFound());
    }

}
