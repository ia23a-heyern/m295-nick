package org.example.m295nick.security;

import org.example.m295nick.controllers.RentalController;
import org.example.m295nick.services.RentalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
class RentalSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @Test
    @DisplayName("USER darf nicht DELETE /api/v1/rentals/{id} aufrufen (403 Forbidden)")
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserRole_deleteRental_forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/rentals/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN darf DELETE /api/v1/rentals/{id} aufrufen (204 No Content)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAdminRole_deleteRental_allowed() throws Exception {
        // rentalService.deleteById(1L) wird vom Controller aufgerufen, deshalb mocken wir es:
        doNothing().when(rentalService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/rentals/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("USER darf GET /api/v1/rentals aufrufen (200 OK)")
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserRole_getAllAllowed() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/rentals"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unauthenticated darf GET /api/v1/rentals nicht aufrufen (401 Unauthorized)")
    void whenNoAuth_getAll_forbidden() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/rentals"))
                .andExpect(status().isUnauthorized());
    }
}
