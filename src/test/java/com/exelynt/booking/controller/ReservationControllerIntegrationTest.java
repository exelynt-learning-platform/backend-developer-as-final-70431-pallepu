package com.exelynt.booking.controller;

import com.exelynt.booking.domain.ReservationStatus;
import com.exelynt.booking.dto.ReservationRequest;
import com.exelynt.booking.dto.ReservationStatusUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /reservations - Success for authenticated user (Identity taken from Security Context)")
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void createReservation_Success() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(10).withHour(10).withMinute(0);
        LocalDateTime end = LocalDateTime.now().plusDays(10).withHour(14).withMinute(0);

        ReservationRequest request = ReservationRequest.builder()
                .resourceId(1L)
                .startTime(start)
                .endTime(end)
                .build();

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    @DisplayName("GET /reservations - Pagination, Filtering, and Sorting as ADMIN")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void getReservations_FilteredAndPaginated_AsAdmin() throws Exception {
        mockMvc.perform(get("/reservations")
                        .param("status", "CONFIRMED")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "600.00")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "totalPrice")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("PUT /reservations/{id}/status - USER can cancel own reservation")
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void updateReservationStatus_UserCancel_Success() throws Exception {
        ReservationStatusUpdateRequest request = ReservationStatusUpdateRequest.builder()
                .status(ReservationStatus.CANCELLED)
                .build();

        mockMvc.perform(put("/reservations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("GET /reservations - Unauthenticated request returns 401 Unauthorized")
    void getReservations_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isUnauthorized());
    }
}
