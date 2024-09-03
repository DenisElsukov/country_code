package com.delsukov.countrycode.repository;

import com.delsukov.countrycode.entity.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryCodeRepo extends JpaRepository<CountryCode, Integer> {
    List<CountryCode> findByCodeStartingWith(String code);
}
