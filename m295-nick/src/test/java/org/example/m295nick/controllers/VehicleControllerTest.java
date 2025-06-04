package org.example.m295nick.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setBrand("VW");
        sampleVehicle.setModel("Golf");
        sampleVehicle.setFirstRegistration(LocalDate.of(2020, 1, 1));
        sampleVehicle.setHasAirConditioning(true);
        sampleVehicle.setPricePerDay(new BigDecimal("50.00"));
        sampleVehicle.setSeats(5);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/vehicles/{id} returns 200 and JSON when found")
    void whenGetById_existing_thenReturnJson() throws Exception {
        when(vehicleService.getById(1L)).thenReturn(Optional.of(sampleVehicle));

        mockMvc.perform(get("/api/v1/vehicles/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("VW"))
                .andExpect(jsonPath("$.model").value("Golf"))
                .andExpect(jsonPath("$.pricePerDay").value(50.0));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/vehicles/{id} returns 404 when not found")
    void whenGetById_notFound_then404() throws Exception {
        when(vehicleService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/vehicles/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("HEAD /api/v1/vehicles/{id} returns 200 when exists")
    void whenHeadById_exists_then200() throws Exception {
        when(vehicleService.existsById(1L)).thenReturn(true);

        mockMvc.perform(head("/api/v1/vehicles/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("HEAD /api/v1/vehicles/{id} returns 404 when not exists")
    void whenHeadById_notExists_then404() throws Exception {
        when(vehicleService.existsById(2L)).thenReturn(false);

        mockMvc.perform(head("/api/v1/vehicles/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/vehicles returns list of vehicles")
    void whenGetAll_thenReturnList() throws Exception {
        when(vehicleService.getAll()).thenReturn(List.of(sampleVehicle));

        mockMvc.perform(get("/api/v1/vehicles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("VW"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/vehicles/filter/air-conditioning?enabled=true filters correctly")
    void whenFilterByAirConditioning_thenReturnFiltered() throws Exception {
        when(vehicleService.getByAirConditioning(true)).thenReturn(List.of(sampleVehicle));

        mockMvc.perform(get("/api/v1/vehicles/filter/air-conditioning")
                        .param("enabled", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hasAirConditioning").value(true));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/vehicles/filter/brand?brand=vw filters correctly")
    void whenFilterByBrand_thenReturnFiltered() throws Exception {
        when(vehicleService.getByBrand("vw")).thenReturn(List.of(sampleVehicle));

        mockMvc.perform(get("/api/v1/vehicles/filter/brand")
                        .param("brand", "vw")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("VW"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("POST /api/v1/vehicles returns 201 when body is valid")
    void whenCreate_valid_then201() throws Exception {
        Vehicle toCreate = new Vehicle();
        toCreate.setBrand("Seat");
        toCreate.setModel("Ibiza");
        toCreate.setFirstRegistration(LocalDate.of(2021, 1, 1));
        toCreate.setHasAirConditioning(false);
        toCreate.setPricePerDay(new BigDecimal("40.00"));
        toCreate.setSeats(4);

        when(vehicleService.create(any(Vehicle.class))).thenReturn(sampleVehicle);

        mockMvc.perform(post("/api/v1/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("VW"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("POST /api/v1/vehicles returns 400 when body invalid")
    void whenCreate_invalid_then400() throws Exception {
        String invalidJson = """
            {
              "model": "Ibiza",
              "firstRegistration": "2021-01-01",
              "hasAirConditioning": true,
              "pricePerDay": 40.00,
              "seats": 4
            }
            """;

        mockMvc.perform(post("/api/v1/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.brand").exists());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("PUT /api/v1/vehicles/{id} updates and returns 200")
    void whenUpdate_valid_then200() throws Exception {
        Vehicle updated = new Vehicle();
        updated.setId(1L);
        updated.setBrand("Skoda");
        updated.setModel("Octavia");
        updated.setFirstRegistration(LocalDate.of(2020, 1, 1));
        updated.setHasAirConditioning(true);
        updated.setPricePerDay(new BigDecimal("50.00"));
        updated.setSeats(5);

        when(vehicleService.update(eq(1L), any(Vehicle.class))).thenReturn(updated);

        String updateJson = objectMapper.writeValueAsString(updated);

        mockMvc.perform(put("/api/v1/vehicles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Skoda"))
                .andExpect(jsonPath("$.model").value("Octavia"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("DELETE /api/v1/vehicles/{id} returns 204 on success")
    void whenDelete_valid_then204() throws Exception {
        doNothing().when(vehicleService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/vehicles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("DELETE /api/v1/vehicles/filter/first-registration?before=2020-01-01 returns 204")
    void whenDeleteByDate_then204() throws Exception {
        doNothing().when(vehicleService).deleteByFirstRegistrationBefore(LocalDate.of(2020, 1, 1));

        mockMvc.perform(delete("/api/v1/vehicles/filter/first-registration")
                        .with(csrf())
                        .param("before", "2020-01-01"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("DELETE /api/v1/vehicles returns 204 for deleteAll")
    void whenDeleteAll_then204() throws Exception {
        doNothing().when(vehicleService).deleteAll();

        mockMvc.perform(delete("/api/v1/vehicles")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
