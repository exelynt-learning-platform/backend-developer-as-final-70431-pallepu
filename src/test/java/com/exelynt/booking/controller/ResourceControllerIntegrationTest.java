package com.exelynt.booking.controller;

import com.exelynt.booking.domain.ResourceType;
import com.exelynt.booking.dto.ResourceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResourceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /resources - Accessible by authenticated USER")
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void getAllResources_AsUser_Success() throws Exception {
        mockMvc.perform(get("/resources"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /resources - Forbidden (403) for regular USER role")
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void createResource_AsUser_Forbidden() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("New Room")
                .type(ResourceType.ROOM)
                .pricePerHour(new BigDecimal("100.00"))
                .build();

        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /resources - Allowed (201 Created) for ADMIN role")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void createResource_AsAdmin_Success() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("VIP Executive Lounge")
                .type(ResourceType.ROOM)
                .description("Luxury meeting space")
                .capacity(10)
                .location("Building A, Floor 5")
                .pricePerHour(new BigDecimal("150.00"))
                .build();

        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("VIP Executive Lounge"))
                .andExpect(jsonPath("$.pricePerHour").value(150.00));
    }

    @Test
    @DisplayName("POST /resources - Returns 400 Bad Request when validation fails")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void createResource_ValidationFailure() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("") // Blank name
                .type(null) // Null type
                .pricePerHour(new BigDecimal("-10.00")) // Invalid negative price
                .build();

        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.type").exists())
                .andExpect(jsonPath("$.fieldErrors.pricePerHour").exists());
    }
}
