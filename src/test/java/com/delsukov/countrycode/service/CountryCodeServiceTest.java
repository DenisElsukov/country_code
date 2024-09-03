package com.delsukov.countrycode.service;

import com.delsukov.countrycode.entity.CountryCode;
import com.delsukov.countrycode.repository.CountryCodeRepo;
import com.delsukov.countrycode.validation.InvalidPhoneNumberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CountryCodeServiceTest {
    @Mock
    private CountryCodeRepo countryCodeRepo;

    @InjectMocks
    private CountryCodeService countryCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testWriteEntryToDB() {
        CountryCode code = new CountryCode();
        code.setServing("Bahamas");
        code.setCode("1242");

        when(countryCodeRepo.save(code)).thenReturn(code);

        CountryCode savedCode = countryCodeService.writeEntryToDB(code);

        assertNotNull(savedCode);
        assertEquals("Bahamas", savedCode.getServing());
        verify(countryCodeRepo, times(1)).save(code);
    }

    @Test
    void testGetCountryByPhoneNumber_CountryFound() {
        String phoneNumber = "12423222931";

        CountryCode code = new CountryCode();
        code.setServing("Bahamas");
        code.setCode("1242");

        when(countryCodeRepo.findByCodeStartingWith(anyString())).thenReturn(List.of(code));

        Optional<String> result = countryCodeService.getCountryByPhoneNumber(phoneNumber);

        assertTrue(result.isPresent());
        assertEquals("Bahamas", result.get());
    }

    @Test
    void testGetCountryByPhoneNumber_CountryNotFound() {
        String phoneNumber = "9876543210";

        when(countryCodeRepo.findByCodeStartingWith(anyString())).thenReturn(List.of());

        Optional<String> result = countryCodeService.getCountryByPhoneNumber(phoneNumber);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetCountryByPhoneNumber_InvalidPhoneNumber_Empty() {
        String phoneNumber = "";

        InvalidPhoneNumberException exception = assertThrows(InvalidPhoneNumberException.class, () -> {
            countryCodeService.getCountryByPhoneNumber(phoneNumber);
        });

        assertEquals("Phone number must not be empty", exception.getMessage());
    }

    @Test
    void testGetCountryByPhoneNumber_InvalidPhoneNumber_NonDigits() {
        String phoneNumber = "123abc";

        InvalidPhoneNumberException exception = assertThrows(InvalidPhoneNumberException.class, () -> {
            countryCodeService.getCountryByPhoneNumber(phoneNumber);
        });

        assertEquals("Phone number must contain only digits", exception.getMessage());
    }

    @Test
    void testGetCountryByPhoneNumber_InvalidPhoneNumber_Length() {
        String tooShortNumber = "123";
        String tooLongNumber = "1234567890123456";

        InvalidPhoneNumberException shortException = assertThrows(InvalidPhoneNumberException.class, () -> {
            countryCodeService.getCountryByPhoneNumber(tooShortNumber);
        });

        InvalidPhoneNumberException longException = assertThrows(InvalidPhoneNumberException.class, () -> {
            countryCodeService.getCountryByPhoneNumber(tooLongNumber);
        });

        assertEquals("Phone number must be between 10 and 15 digits long", shortException.getMessage());
        assertEquals("Phone number must be between 10 and 15 digits long", longException.getMessage());
    }

    @Test
    void testCleanup() {
        countryCodeService.cleanup();

        verify(countryCodeRepo, times(1)).deleteAll();
    }
}