package com.safetynet.alerts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.safetynet.alerts.config.JsonTestConfig.SAMPLE_ORIG_PATH;
import static com.safetynet.alerts.config.JsonTestConfig.SAMPLE_PATH;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setup() throws IOException {
        Files.copy(Paths.get(SAMPLE_ORIG_PATH), Paths.get(SAMPLE_PATH), REPLACE_EXISTING);
    }

    @Test
    public void testFirestationEndpoint() throws Exception {
        mockMvc.perform(get("/firestation").param("stationNumber", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultCount").value(5))
                .andExpect(jsonPath("$.childCount").value(1))
                .andExpect(jsonPath("$.coveredPeopleDataExtract", hasSize(6)));
    }

    @Test
    public void testChildAlertEndpoint() throws Exception {
        mockMvc.perform(get("/childAlert").param("address", "1509 Culver St"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(2)))
                .andExpect(jsonPath("$.otherHouseholders", hasSize(3)));
    }

    @Test
    public void testPhoneAlertEndpoint() throws Exception {
        mockMvc.perform(get("/phoneAlert").param("firestation", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testFireEndpoint() throws Exception {
        mockMvc.perform(get("/fire").param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firestation").value(3))
                .andExpect(jsonPath("$.household", hasSize(5)));
    }

    @Test
    public void testFloodStationEndpoint() throws Exception {
        mockMvc.perform(get("/flood/stations").param("stations", "1,2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.households[*]", hasSize(6)));
    }

    @Test
    public void testPersonInfoEndpoint() throws Exception {
        mockMvc.perform(get("/personInfo").param("lastName", "Boyd"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foundPersons", hasSize(6)));
    }

    @Test
    public void testCommunityEmailEndpoint() throws Exception {
        mockMvc.perform(get("/communityEmail").param("city", "Culver"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(15)));
    }

}
