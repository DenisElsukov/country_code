package com.delsukov.countrycode.controller;


import com.delsukov.countrycode.service.CountryCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CountryCodeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CountryCodeService service;

    @InjectMocks
    private CountryCodeController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void determineCountry_ReturnsCountry_WhenCountryExists() throws Exception {
        String phoneNumber = "12423222931";
        String country = "Bahamas";

        when(service.getCountryByPhoneNumber(phoneNumber)).thenReturn(Optional.of(country));

        mockMvc.perform(get("/api/phone/country")
                        .param("number", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(content().string(country));
    }

    @Test
    void determineCountry_ReturnsNotFound_WhenCountryDoesNotExist() throws Exception {
        String phoneNumber = "654321";

        when(service.getCountryByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/phone/country")
                        .param("number", phoneNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Country not found"));
    }

}