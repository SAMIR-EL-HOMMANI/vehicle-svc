package com.demo.vehicle.api.controller;

import com.demo.vehicle.api.MySpringBootAppApplication;
import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.dto.OpeningTimeDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.service.GarageService;
import com.demo.vehicle.api.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GarageController.class)
@ContextConfiguration(classes = MySpringBootAppApplication.class)
class GarageControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    GarageService garageService;
    @MockBean
    VehicleService vehicleService;

    @Test
    void should_return_garage_by_id() throws Exception {
        when(garageService.findGarageByIdWithWorkingHours(1L)).thenReturn(buildGarageDtoObject());
        mockMvc.perform(get("/api/v1/garages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("DACIA CASA"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void should_return_bad_request_when_path_id_and_body_id_do_not_match() throws Exception {
        String payload = "{" +
                "\"id\":2," +
                "\"name\":\"DACIA CASA\"," +
                "\"address\":\"123, Boulevard Ghandi, Casablanca\"," +
                "\"phoneNumber\":\"0522020222\"," +
                "\"email\":\"garage@example.com\"," +
                "\"GarageOpeningHours\":{}" +
                "}";

        mockMvc.perform(put("/api/v1/garages/1")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Path id and body id must be the same"));

        verifyNoInteractions(garageService);
    }

    @Test
    void should_return_vehicles_by_garage_id() throws Exception {
        when(vehicleService.findByGarageId(1L)).thenReturn(List.of(
                new VehicleDto(10L, "Toyota", "Corolla", 2020, "Diesel", null)
        ));

        mockMvc.perform(get("/api/v1/garages/1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].brand").value("Toyota"));
    }

    @Test
    void should_return_vehicles_by_model_and_garage_ids() throws Exception {
        when(vehicleService.findByModelAndGarageIds("Corolla", List.of(1L, 2L))).thenReturn(List.of(
                new VehicleDto(10L, "Toyota", "Corolla", 2020, "Diesel", null),
                new VehicleDto(11L, "Toyota", "Corolla", 2021, "Hybrid", null)
        ));

        mockMvc.perform(get("/api/v1/garages/vehicles")
                        .param("model", "Corolla")
                        .param("garageIds", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[0].model").value("Corolla"));
    }

    GarageDto buildGarageDtoObject(){
        return GarageDto.builder()
                .id(1L)
                .name("DACIA CASA")
                .address("123, Boulevard Ghandi, Casablanca")
                .phoneNumber("0522020222")
                .GarageOpeningHours(buildGarageOpeningHourDto())
                .build();
    }

    Map<DayOfWeek, List<OpeningTimeDto>> buildGarageOpeningHourDto(){
        return Map.of(
                DayOfWeek.MONDAY,
                List.of(OpeningTimeDto.builder()
                        .startTime(LocalTime.of(8, 0, 0))
                        .endTime(LocalTime.of(18, 0, 0))
                        .build())
        );
    }



//
//    void should_return_paged_garages_sorted_by_name() throws Exception {
//        Page<GarageDto> page = new PageImpl<>(
//                List.of(new GarageDto(1L, "Alpha", "Casablanca", 80),
//                        new GarageDto(2L, "Beta", "Rabat", 50)),
//                PageRequest.of(0, 2, Sort.by("name").ascending()),
//                10
//        );
//
//        when(garageService.listGarages(eq(0), eq(2), any())).thenReturn(page);
//
//        mockMvc.perform(get("/api/garages")
//                        .param("page", "0")
//                        .param("size", "2")
//                        .param("sort", "name,asc"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].name").value("Alpha"))
//                .andExpect(jsonPath("$.totalElements").value(10));
//    }
}