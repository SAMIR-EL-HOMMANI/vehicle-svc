package com.demo.vehicle.api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GarageIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_execute_garage_crud_flow_end_to_end() throws Exception {
        String createPayload = """
                {
                  "name": "Garage Renault Sidi Maarouf",
                  "address": "10 Rue Integration Sidi Maarouf Casablanca",
                  "phoneNumber": "0600000000",
                  "email": "garage.renault@test.com",
                  "GarageOpeningHours": {
                    "MONDAY": [
                      {"startTime": "08:00:00", "endTime": "12:00:00"},
                      {"startTime": "14:00:00", "endTime": "18:00:00"}
                    ]
                  }
                }
                """;

        MvcResult creationResult = mockMvc.perform(post("/api/v1/garages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Garage Renault Sidi Maarouf"))
                .andExpect(jsonPath("$.GarageOpeningHours.MONDAY[0].startTime").value("08:00:00"))
                .andReturn();

        JsonNode createdGarage = objectMapper.readTree(creationResult.getResponse().getContentAsString());
        long garageId = createdGarage.get("id").asLong();
        assertThat(garageId).isPositive();

        mockMvc.perform(get("/api/v1/garages/{id}", garageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(garageId))
                .andExpect(jsonPath("$.name").value("Garage Renault Sidi Maarouf"));

        String updatePayload = """
                {
                  "id": %d,
                  "name": "Garage Renault Lissasfa",
                  "address": "10 Rue Integration Lissasfa Casablanca",
                  "phoneNumber": "0600000000",
                  "email": "garage.renault@test.com",
                  "GarageOpeningHours": {
                    "MONDAY": [
                      {"startTime": "09:00:00", "endTime": "12:00:00"}
                    ]
                  }
                }
                """.formatted(garageId);

        mockMvc.perform(put("/api/v1/garages/{id}", garageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Garage Renault Lissasfa"))
                .andExpect(jsonPath("$.GarageOpeningHours.MONDAY[0].startTime").value("09:00:00"));

        mockMvc.perform(delete("/api/v1/garages/{id}", garageId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/garages/{id}", garageId))
                .andExpect(status().isNotFound());
    }
}

