package com.delsukov.countrycode.service;

import com.delsukov.countrycode.entity.CountryCode;
import com.delsukov.countrycode.repository.CountryCodeRepo;
import com.delsukov.countrycode.validation.InvalidPhoneNumberException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryCodeService {
    private final CountryCodeRepo countryCodeRepo;

    private static final int MAX_CODE_LENGTH = 7;
    private static final int MIN_CODE_LENGTH = 1;

    public CountryCode writeEntryToDB(CountryCode code) {
        CountryCode savedEmployee = countryCodeRepo.save(code);

        log.debug("{} with code {} and id: {} saved successfully", code.getServing(), code.getCode(), code.getId());
        return savedEmployee;
    }

    public Optional<String> getCountryByPhoneNumber(String number) {
        String normalizedNumber = normalizeNumber(number);
        validatePhoneNumber(normalizedNumber);

        return findCountryByCode(normalizedNumber)
                .or(() -> {
                    log.debug("Country not found for number: {}", normalizedNumber);
                    return Optional.empty();
                });

    }

    private String normalizeNumber(String number) {
        return number.replaceAll("[\\s\\-()+/]", "");
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            throw new InvalidPhoneNumberException("Phone number must not be empty");
        }
        if (!phoneNumber.matches("\\d+")) {
            throw new InvalidPhoneNumberException("Phone number must contain only digits");
        }
        if (phoneNumber.length() < 10 || phoneNumber.length() > 15) {
            throw new InvalidPhoneNumberException("Phone number must be between 10 and 15 digits long");
        }
    }

    private Optional<String> findCountryByCode(String number) {
        for (int i = MAX_CODE_LENGTH; i >= MIN_CODE_LENGTH; i--) {
            if (number.length() >= i) {
                String code = number.substring(0, i);
                log.debug("Checking code: {}", code);

                List<CountryCode> matchingCodes = countryCodeRepo.findByCodeStartingWith(code);

                if (!matchingCodes.isEmpty()) {
                    List<CountryCode> exactMatches = matchingCodes.stream()
                            .filter(c -> number.startsWith(c.getCode()))
                            .sorted(Comparator.comparingInt(c -> -c.getCode().length()))
                            .toList();

                    if (!exactMatches.isEmpty()) {
                        return Optional.of(exactMatches.stream()
                                .map(CountryCode::getServing)
                                .collect(Collectors.joining(", ")));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @PreDestroy
    public void cleanup() {
        countryCodeRepo.deleteAll();
    }
}
