package com.demo.vehicle.api.controller;

import com.demo.vehicle.api.MySpringBootAppApplication;
import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.service.AccessoryService;
import com.demo.vehicle.api.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
@ContextConfiguration(classes = MySpringBootAppApplication.class)
class VehicleControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    VehicleService vehicleService;
    @MockBean
    AccessoryService accessoryService;

    @Test
    void should_return_accessories_by_vehicle_id() throws Exception {
        when(vehicleService.getByIdOrThrow(10L)).thenReturn(new VehicleDto(10L, "Toyota", "Corolla", 2020, "Diesel", null));
        when(accessoryService.findByVehicleId(10L)).thenReturn(List.of(
                new AccessoryDto(1L, "GPS", "Systeme navigation", 1500L, "Electronique", 10L),
                new AccessoryDto(2L, "Camera", "Camera de recul", 900L, "Securite", 10L)
        ));

        mockMvc.perform(get("/api/v1/vehicles/10/accessories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].vehicleId").value(10))
                .andExpect(jsonPath("$[1].name").value("Camera"));

        verify(vehicleService).getByIdOrThrow(10L);
        verify(accessoryService).findByVehicleId(10L);
    }
}

