package com.delsukov.countrycode.config;

import com.delsukov.countrycode.util.CountryCodeLoader;
import com.delsukov.countrycode.service.CountryCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up country code related beans.
 */
@Configuration
@RequiredArgsConstructor
public class CountryCodeConfig {
    private final CountryCodeService countryCodeService;
    @Bean
    public CountryCodeLoader countryCodeLoader() {
        return new CountryCodeLoader(countryCodeService);
    }
}
