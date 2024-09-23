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
public class FirestationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() throws IOException {
        Files.copy(Paths.get(SAMPLE_ORIG_PATH), Paths.get(SAMPLE_PATH), REPLACE_EXISTING);
    }

    @Test
    @Order(1)
    public void testCreateFirestation() throws Exception {
        mockMvc.perform(post("/firestation")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "address":"123 Test St", "station":"5" }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void testCreateFirestationConflict() throws Exception {
        mockMvc.perform(post("/firestation")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "address":"123 Test St", "station":"3" }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    public void testUpdateFirestation() throws Exception {
        mockMvc.perform(put("/firestation/123 Test St")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "address":"123 Test St", "station":"1" }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void testUpdateFirestationNotFound() throws Exception {
        mockMvc.perform(put("/firestation/0000 Untold Rd")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "address":"0000 Untold Rd", "station":"1" }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void testDeleteFirestation() throws Exception {
        mockMvc.perform(delete("/firestation/123 Test St"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    public void testDeleteFirestationNotFound() throws Exception {
        mockMvc.perform(delete("/firestation/0000 Untold Rd"))
                .andExpect(status().isNotFound());
    }

}
