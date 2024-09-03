package com.delsukov.countrycode.util;

import com.delsukov.countrycode.entity.CountryCode;
import com.delsukov.countrycode.service.CountryCodeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CountryCodeLoader {
    private static final String WIKI_URL = "https://en.wikipedia.org/wiki/List_of_country_calling_codes#Alphabetical_order";
    private static final String TABLE_CLASS = "table.wikitable";

    private final CountryCodeService service;

    @PostConstruct
    public void fetchAndSaveCountryCodes() {
        try {
            Document doc = Jsoup.connect(WIKI_URL).get();
            Element table = doc.select(TABLE_CLASS).first();

            if (table != null) {
                parseAndSaveCountryCodes(table);
            } else {
                log.warn("No table found at the provided URL.");
            }
        } catch (IOException e) {
            log.error("Failed to fetch country codes from the URL: {}", WIKI_URL, e);
        }
    }

    private void parseAndSaveCountryCodes(Element table) {
        Elements rows = table.select("tr");

        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() > 1) {
                String country = cells.get(0).text();
                String rawCode = cells.get(1).text();
                processAndSaveCodes(country, rawCode);
            }
        }

        log.info("country codes are saved into the database");
    }

    private void processAndSaveCodes(String country, String rawCode) {
        rawCode = rawCode.replaceAll("[a-zA-Z]", "").trim();

        if (rawCode.contains("(") && rawCode.contains(")")) {
            handleComplexCode(country, rawCode);
        } else if (rawCode.contains(",")) {
            handleMultipleCodes(country, rawCode);
        } else {
            service.writeEntryToDB(new CountryCode(country, rawCode.trim()));
        }
    }

    private void handleComplexCode(String country, String rawCode) {
        String baseCode = rawCode.substring(0, rawCode.indexOf(' '));
        String[] subCodes = rawCode.substring(rawCode.indexOf('(') + 1, rawCode.indexOf(')')).split(", ");

        for (String subCode : subCodes) {
            String fullCode = baseCode + subCode.trim();
            service.writeEntryToDB(new CountryCode(country, fullCode));
        }
    }

    private void handleMultipleCodes(String country, String rawCode) {
        String[] codes = rawCode.split(",");
        for (String code : codes) {
            service.writeEntryToDB(new CountryCode(country, code.trim()));
        }
    }
}
