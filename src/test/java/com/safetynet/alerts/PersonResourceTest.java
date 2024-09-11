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
public class PersonResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() throws IOException {
        Files.copy(Paths.get(SAMPLE_ORIG_PATH), Paths.get(SAMPLE_PATH), REPLACE_EXISTING);
    }

    @Test
    @Order(1)
    public void testCreatePerson() throws Exception {
        mockMvc.perform(post("/person")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"Jane", "lastName":"Doe",
                                "address":"123 Test St", "city":"Nowhere", "zip":"12345",
                                "phone":"123-456-7890", "email":"jdoe@mail.com" }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void testCreatePersonConflict() throws Exception {
        mockMvc.perform(post("/person")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"Jane", "lastName":"Doe",
                                "address":"456 Anyway Rd", "city":"Anywhere",
                                "zip":"98765", "phone":"987-654-3210", "email":"jpeter@impostor.com" }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    public void testUpdatePerson() throws Exception {
        mockMvc.perform(put("/person/JaneDoe")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"Jane", "lastName":"Smith",
                                "address":"1600 Pennsylvania Av", "city":"Washington DC",
                                "zip":"20500", "phone":"999-999-9999", "email":"jdoe@whitehouse.com" }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void testUpdatePersonNotFound() throws Exception {
        mockMvc.perform(put("/person/MeNoexist")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "firstName":"Me", "lastName":"Noexist",
                                "address":"000 Staya Way", "city":"Netherworld",
                                "zip":"00000", "phone":"000-000-0000", "email":"noreply@server.nil" }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void testDeletePerson() throws Exception {
        mockMvc.perform(delete("/person/JaneSmith"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    public void testDeletePersonNotFound() throws Exception {
        mockMvc.perform(delete("/person/MeNoexist"))
                .andExpect(status().isNotFound());
    }

}
