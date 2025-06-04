package org.example.m295nick.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.services.RentalService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    }

    // --- GET /api/v1/rentals/{id} (happy path) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/rentals/{id} returns 200 + JSON, wenn vorhanden")
    void whenGetById_existing_thenReturnJson() throws Exception {
        Rental sampleRental = new Rental();
        sampleRental.setId(1L);
        sampleRental.setCustomer("Max Mustermann");
        sampleRental.setStartDate(LocalDate.of(2025, 6, 10));
        sampleRental.setEndDate  (LocalDate.of(2025, 6, 12));
        sampleRental.setVehicle(sampleVehicle);
        sampleRental.setTotalCost(new BigDecimal("300.00"));

        when(rentalService.getById(1L)).thenReturn(Optional.of(sampleRental));

        mockMvc.perform(get("/api/v1/rentals/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Max Mustermann"))
                .andExpect(jsonPath("$.startDate").value("2025-06-10"))
                .andExpect(jsonPath("$.endDate").value("2025-06-12"))
                .andExpect(jsonPath("$.totalCost").value(300.0));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/rentals/{id} returns 404, wenn nicht vorhanden")
    void whenGetById_notFound_then404() throws Exception {
        when(rentalService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/rentals/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- HEAD /api/v1/rentals/{id} (happy path vs. not found) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("HEAD /api/v1/rentals/{id} returns 200, wenn vorhanden")
    void whenHeadById_exists_then200() throws Exception {
        when(rentalService.existsById(1L)).thenReturn(true);
        mockMvc.perform(head("/api/v1/rentals/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("HEAD /api/v1/rentals/{id} returns 404, wenn nicht vorhanden")
    void whenHeadById_notExists_then404() throws Exception {
        when(rentalService.existsById(2L)).thenReturn(false);
        mockMvc.perform(head("/api/v1/rentals/2"))
                .andExpect(status().isNotFound());
    }

    // --- GET /api/v1/rentals (Liste) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/rentals returns Liste von Rentals")
    void whenGetAll_thenReturnList() throws Exception {
        Rental r = new Rental();
        r.setId(1L);
        r.setCustomer("Max Mustermann");
        r.setStartDate(LocalDate.of(2025, 6, 10));
        r.setEndDate  (LocalDate.of(2025, 6, 12));
        r.setVehicle(sampleVehicle);
        r.setTotalCost(new BigDecimal("300.00"));

        when(rentalService.getAll()).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/rentals")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customer").value("Max Mustermann"))
                .andExpect(jsonPath("$[0].totalCost").value(300.0));
    }

    // --- GET /api/v1/rentals/filter/start-after?after=... ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/rentals/filter/start-after filtert korrekt")
    void whenFilterByStartDateAfter_thenReturnFiltered() throws Exception {
        Rental r = new Rental();
        r.setId(2L);
        r.setCustomer("Max Mustermann");
        r.setStartDate(LocalDate.of(2025, 7, 5));
        r.setEndDate  (LocalDate.of(2025, 7, 7));
        r.setVehicle(sampleVehicle);
        r.setTotalCost(new BigDecimal("400.00"));

        when(rentalService.getByStartDateAfter(LocalDate.of(2025, 1, 1)))
                .thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/rentals/filter/start-after")
                        .param("after", "2025-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startDate").value("2025-07-05"));
    }

    // --- GET /api/v1/rentals/filter/end-before?before=... ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /api/v1/rentals/filter/end-before filtert korrekt")
    void whenFilterByEndDateBefore_thenReturnFiltered() throws Exception {
        Rental r = new Rental();
        r.setId(3L);
        r.setCustomer("Erika Muster");
        r.setStartDate(LocalDate.of(2025, 5, 10));
        r.setEndDate  (LocalDate.of(2025, 5, 12));
        r.setVehicle(sampleVehicle);
        r.setTotalCost(new BigDecimal("200.00"));

        when(rentalService.getByEndDateBefore(LocalDate.of(2026, 1, 1)))
                .thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/rentals/filter/end-before")
                        .param("before", "2026-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].endDate").value("2025-05-12"));
    }

    // --- POST /api/v1/rentals (happy path) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("POST /api/v1/rentals liefert 201, wenn Body gültig")
    void whenCreate_valid_then201() throws Exception {
        Rental toCreate = new Rental();
        toCreate.setCustomer("Anna");
        toCreate.setVehicle(sampleVehicle);
        toCreate.setStartDate(LocalDate.of(2025, 7, 1));
        toCreate.setEndDate  (LocalDate.of(2025, 7, 2));
        toCreate.setTotalCost(new BigDecimal("100.00"));

        Rental saved = new Rental();
        saved.setId(42L);
        saved.setCustomer("Anna");
        saved.setVehicle(sampleVehicle);
        saved.setStartDate(toCreate.getStartDate());
        saved.setEndDate(toCreate.getEndDate());
        saved.setTotalCost(new BigDecimal("100.00"));

        when(rentalService.create(any(Rental.class))).thenReturn(saved);

        String validJson = objectMapper.writeValueAsString(toCreate);

        mockMvc.perform(post("/api/v1/rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.customer").value("Anna"))
                .andExpect(jsonPath("$.totalCost").value(100.0));
    }

    // --- POST /api/v1/rentals (invalid = fehlende totalCost) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("POST /api/v1/rentals liefert 400, wenn totalCost fehlt")
    void whenCreate_invalid_then400() throws Exception {
        Rental invalid = new Rental();
        invalid.setCustomer("Lisa");
        invalid.setVehicle(sampleVehicle);
        invalid.setStartDate(LocalDate.of(2025, 7, 1));
        invalid.setEndDate  (LocalDate.of(2025, 7, 2));
        // kein totalCost gesetzt

        String invalidJson = objectMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/api/v1/rentals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.totalCost").value("Gesamtkosten sind Pflicht"));
    }

    // --- PUT /api/v1/rentals/{id} (happy path) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("PUT /api/v1/rentals/{id} updated und gibt 200 zurück")
    void whenUpdate_valid_then200() throws Exception {
        Rental validUpdate = new Rental();
        validUpdate.setCustomer("Erika");
        validUpdate.setVehicle(sampleVehicle);
        validUpdate.setStartDate(LocalDate.of(2025, 6, 10));
        validUpdate.setEndDate  (LocalDate.of(2025, 6, 12));
        validUpdate.setTotalCost(new BigDecimal("300.00"));

        when(rentalService.update(eq(1L), any(Rental.class))).thenReturn(validUpdate);

        String updateJson = objectMapper.writeValueAsString(validUpdate);

        mockMvc.perform(put("/api/v1/rentals/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Erika"))
                .andExpect(jsonPath("$.startDate").value("2025-06-10"))
                .andExpect(jsonPath("$.endDate").value("2025-06-12"))
                .andExpect(jsonPath("$.totalCost").value(300.0));
    }

    // --- DELETE /api/v1/rentals/{id} (happy path) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("DELETE /api/v1/rentals/{id} liefert 204 bei Erfolg")
    void whenDelete_valid_then204() throws Exception {
        doNothing().when(rentalService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/rentals/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // --- DELETE /api/v1/rentals/filter/start-after?after=... ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("DELETE /api/v1/rentals/filter/start-after liefert 204")
    void whenDeleteByStartDate_then204() throws Exception {
        doNothing().when(rentalService).deleteByStartDateAfter(LocalDate.of(2025, 1, 1));

        mockMvc.perform(delete("/api/v1/rentals/filter/start-after")
                        .with(csrf())
                        .param("after", "2025-01-01"))
                .andExpect(status().isNoContent());
    }

    // --- DELETE /api/v1/rentals (deleteAll) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("DELETE /api/v1/rentals liefert 204 für deleteAll")
    void whenDeleteAll_then204() throws Exception {
        doNothing().when(rentalService).deleteAll();

        mockMvc.perform(delete("/api/v1/rentals")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
