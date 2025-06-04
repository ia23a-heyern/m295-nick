package org.example.m295nick.security;

import org.example.m295nick.controllers.VehicleController;
import org.example.m295nick.services.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
class VehicleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Test
    @DisplayName("USER darf nicht DELETE /api/v1/vehicles/{id} aufrufen → 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserRole_deleteVehicle_forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN darf DELETE /api/v1/vehicles/{id} aufrufen → 204 No Content")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAdminRole_deleteVehicle_allowed() throws Exception {
        // Wir mocken vehicleService.deleteById(1L), damit der Controller ohne Fehler durchläuft:
        doNothing().when(vehicleService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("USER darf GET /api/v1/vehicles aufrufen → 200 OK")
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserRole_getAllAllowed() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/vehicles"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unauthenticated darf GET /api/v1/vehicles nicht aufrufen → 401 Unauthorized")
    void whenNoAuth_getAll_forbidden() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/vehicles"))
                .andExpect(status().isUnauthorized());
    }
}
