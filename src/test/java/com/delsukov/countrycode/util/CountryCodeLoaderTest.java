package com.delsukov.countrycode.util;

import com.delsukov.countrycode.entity.CountryCode;
import com.delsukov.countrycode.service.CountryCodeService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryCodeLoaderTest {
    @Mock
    private CountryCodeService service;
    @Mock
    Connection connection;
    @InjectMocks
    private CountryCodeLoader countryCodeLoader;

    @BeforeAll
    static void setup() {
        mockStatic(Jsoup.class);
    }

    @Test
    void testFetchAndSaveCountryCodes_SingleCode() throws IOException {
        Document mockDocument = mock(Document.class);
        Element mockTable = mock(Element.class);
        Elements mockRows = new Elements();
        mockRows.add(createMockRow("Bahamas", "1242"));
        when(mockTable.select("tr")).thenReturn(mockRows);
        when(mockDocument.select("table.wikitable")).thenReturn(new Elements(mockTable));
        when(Jsoup.connect(anyString())).thenReturn(connection);
        when(connection.get()).thenReturn(mockDocument);

        countryCodeLoader.fetchAndSaveCountryCodes();

        verify(service, times(1)).writeEntryToDB(new CountryCode("Bahamas", "1242"));
    }

    @Test
    void testFetchAndSaveCountryCodes_MultipleCodes() throws IOException {
        Document mockDocument = mock(Document.class);
        Element mockTable = mock(Element.class);
        Elements mockRows = new Elements();
        mockRows.add(createMockRow("Bahamas", "1242, 1243, 1244"));
        when(mockTable.select("tr")).thenReturn(mockRows);
        when(mockDocument.select("table.wikitable")).thenReturn(new Elements(mockTable));
        when(Jsoup.connect(anyString())).thenReturn(connection);
        when(connection.get()).thenReturn(mockDocument);

        countryCodeLoader.fetchAndSaveCountryCodes();

        verify(service, times(1)).writeEntryToDB(new CountryCode("Bahamas", "1242"));
        verify(service, times(1)).writeEntryToDB(new CountryCode("Bahamas", "1243"));
        verify(service, times(1)).writeEntryToDB(new CountryCode("Bahamas", "1244"));
    }

    @Test
    void testFetchAndSaveCountryCodes_TableNotFound() throws IOException {
        Document mockDocument = mock(Document.class);
        when(mockDocument.select("table.wikitable")).thenReturn(new Elements());
        when(Jsoup.connect(anyString())).thenReturn(connection);
        when(connection.get()).thenReturn(mockDocument);

        countryCodeLoader.fetchAndSaveCountryCodes();

        verify(service, never()).writeEntryToDB(any(CountryCode.class));
    }

    @Test
    void testFetchAndSaveCountryCodes_IOException() throws IOException {
        when(Jsoup.connect(anyString())).thenReturn(connection);
        when(connection.get()).thenThrow(new IOException("Failed to connect"));

        countryCodeLoader.fetchAndSaveCountryCodes();

        verify(service, never()).writeEntryToDB(any(CountryCode.class));
    }

    private Element createMockRow(String country, String code) {
        Element row = mock(Element.class);
        Elements cells = new Elements();

        Element cellCountry = mock(Element.class);
        Element cellCode = mock(Element.class);
        when(cellCountry.text()).thenReturn(country);
        when(cellCode.text()).thenReturn(code);

        cells.add(cellCountry);
        cells.add(cellCode);
        when(row.select("td")).thenReturn(cells);

        return row;
    }
}