package org.example.m295nick.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.services.RentalService;
import org.example.m295nick.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle sampleVehicle;
    private Rental sampleRental;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setBrand("VW");
        sampleVehicle.setModel("Golf");
        sampleVehicle.setFirstRegistration(LocalDate.of(2020, 1, 1));
        sampleVehicle.setHasAirConditioning(true);
        sampleVehicle.setPricePerDay(new BigDecimal("100.00"));
        sampleVehicle.setSeats(5);

        sampleRental = new Rental();
        sampleRental.setId(1L);
        sampleRental.setCustomer("Max Mustermann");
        sampleRental.setStartDate(LocalDate.of(2025, 6, 1));
        sampleRental.setEndDate(LocalDate.of(2025, 6, 3));
        sampleRental.setVehicle(sampleVehicle);
        sampleRental.setTotalCost(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("GET /api/v1/rentals/{id} returns 200 and JSON when found")
    void whenGetById_existing_thenReturnJson() throws Exception {
        when(rentalService.getById(1L)).thenReturn(Optional.of(sampleRental));

        mockMvc.perform(get("/api/v1/rentals/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Max Mustermann"))
                .andExpect(jsonPath("$.totalCost").value("300.00"));
    }

    @Test
    @DisplayName("GET /api/v1/rentals/{id} returns 404 when not found")
    void whenGetById_notFound_then404() throws Exception {
        when(rentalService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/rentals/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("HEAD /api/v1/rentals/{id} returns 200 when exists")
    void whenHeadById_exists_then200() throws Exception {
        when(rentalService.existsById(1L)).thenReturn(true);

        mockMvc.perform(head("/api/v1/rentals/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("HEAD /api/v1/rentals/{id} returns 404 when not exists")
    void whenHeadById_notExists_then404() throws Exception {
        when(rentalService.existsById(2L)).thenReturn(false);

        mockMvc.perform(head("/api/v1/rentals/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/rentals returns list of rentals")
    void whenGetAll_thenReturnList() throws Exception {
        when(rentalService.getAll()).thenReturn(List.of(sampleRental));

        mockMvc.perform(get("/api/v1/rentals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customer").value("Max Mustermann"));
    }

    @Test
    @DisplayName("GET /api/v1/rentals/filter/start-after filters correctly")
    void whenFilterByStartDateAfter_thenReturnFiltered() throws Exception {
        when(rentalService.getByStartDateAfter(LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of(sampleRental));

        mockMvc.perform(get("/api/v1/rentals/filter/start-after")
                        .param("after", "2025-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startDate").value("2025-06-01"));
    }

    @Test
    @DisplayName("GET /api/v1/rentals/filter/end-before filters correctly")
    void whenFilterByEndDateBefore_thenReturnFiltered() throws Exception {
        when(rentalService.getByEndDateBefore(LocalDate.of(2026, 1, 1)))
                .thenReturn(List.of(sampleRental));

        mockMvc.perform(get("/api/v1/rentals/filter/end-before")
                        .param("before", "2026-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].endDate").value("2025-06-03"));
    }

    @Test
    @DisplayName("POST /api/v1/rentals returns 201 when body is valid")
    void whenCreate_valid_then201() throws Exception {
        when(rentalService.create(any(Rental.class))).thenReturn(sampleRental);

        Rental toCreate = new Rental();
        toCreate.setCustomer("Anna");
        toCreate.setStartDate(LocalDate.of(2025, 7, 1));
        toCreate.setEndDate(LocalDate.of(2025, 7, 2));
        toCreate.setVehicle(sampleVehicle);

        mockMvc.perform(post("/api/v1/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer").value("Max Mustermann"));
    }

    @Test
    @DisplayName("POST /api/v1/rentals returns 400 when body invalid")
    void whenCreate_invalid_then400() throws Exception {
        // Fehlendes customer-Feld
        String invalidJson = """
            {
              "startDate": "2025-07-01",
              "endDate": "2025-07-02",
              "vehicle": {"id":1}
            }
            """;

        mockMvc.perform(post("/api/v1/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customer").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/rentals/{id} updates and returns 200")
    void whenUpdate_valid_then200() throws Exception {
        when(rentalService.update(eq(1L), any(Rental.class))).thenReturn(sampleRental);

        sampleRental.setCustomer("Erika");
        String updateJson = objectMapper.writeValueAsString(sampleRental);

        mockMvc.perform(put("/api/v1/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Max Mustermann")); // mocked sampleRental
    }

    @Test
    @DisplayName("DELETE /api/v1/rentals/{id} returns 204 on success")
    void whenDelete_valid_then204() throws Exception {
        doNothing().when(rentalService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/rentals/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/rentals/filter/start-after?after=2025-01-01 returns 204")
    void whenDeleteByStartDate_then204() throws Exception {
        doNothing().when(rentalService).deleteByStartDateAfter(LocalDate.of(2025, 1, 1));

        mockMvc.perform(delete("/api/v1/rentals/filter/start-after")
                        .param("after", "2025-01-01"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/rentals returns 204 for deleteAll")
    void whenDeleteAll_then204() throws Exception {
        doNothing().when(rentalService).deleteAll();

        mockMvc.perform(delete("/api/v1/rentals"))
                .andExpect(status().isNoContent());
    }
}
