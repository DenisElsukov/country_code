package com.delsukov.countrycode.controller;

import com.delsukov.countrycode.service.CountryCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/phone")
@RequiredArgsConstructor
@Validated
public class CountryCodeController {
    private final CountryCodeService service;

    /**
     * This method is called when a GET request is made
     * URL: localhost:8088/api/phone/country
     * Purpose: Determines country with the given phone
     *
     * @param number - given phone number
     * @return Country corresponding to the given phone number
     */
    @GetMapping("/country")
    public ResponseEntity<String> determineCountry(@RequestParam String number) {
        return service.getCountryByPhoneNumber(number)
                .map(country -> ResponseEntity.ok().body(country))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Country not found"));
    }
}
