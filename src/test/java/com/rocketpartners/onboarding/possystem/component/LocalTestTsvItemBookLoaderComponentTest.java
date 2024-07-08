package com.rocketpartners.onboarding.possystem.component;

import com.rocketpartners.onboarding.commons.utils.FileLineReader;
import com.rocketpartners.onboarding.possystem.ApplicationProperties;
import com.rocketpartners.onboarding.possystem.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LocalTestTsvItemBookLoaderComponentTest {

    private ItemService itemService;
    private LocalTestTsvItemBookLoaderComponent itemBookLoaderComponent;
    private FileLineReader mockFileLineReader;

    @BeforeEach
    void setUp() {
        itemService = Mockito.mock(ItemService.class);
        ApplicationProperties mockProps = Mockito.mock(ApplicationProperties.class);
        mockFileLineReader = Mockito.mock(FileLineReader.class);
        when(mockProps.getProperty("test.item.book.tsv.file.path")).thenReturn("path/to/test.tsv");
        itemBookLoaderComponent = Mockito.spy(new LocalTestTsvItemBookLoaderComponent());
        when(itemBookLoaderComponent.getProps()).thenReturn(mockProps);
        when(itemBookLoaderComponent.getFileReader()).thenReturn(mockFileLineReader);
    }

    @Test
    void testLoadItemBook() {
        List<String[]> tsvLines = Arrays.asList(
                new String[]{"123456", "Test Item 1", "9.99"},
                new String[]{"789012", "Test Item 2", "19.99"}
        );

        when(mockFileLineReader.read("path/to/test.tsv", "\t")).thenReturn(tsvLines);

        itemBookLoaderComponent.loadItemBook(itemService);

        ArgumentCaptor<String> upcCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<BigDecimal> priceCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        verify(itemService, times(2)).createAndPersist(
                upcCaptor.capture(),
                nameCaptor.capture(),
                priceCaptor.capture(),
                isNull(),
                isNull()
        );

        List<String> capturedUpcs = upcCaptor.getAllValues();
        List<String> capturedNames = nameCaptor.getAllValues();
        List<BigDecimal> capturedPrices = priceCaptor.getAllValues();

        assertEquals(Arrays.asList("123456", "789012"), capturedUpcs);
        assertEquals(Arrays.asList("Test Item 1", "Test Item 2"), capturedNames);
        assertEquals(Arrays.asList(BigDecimal.valueOf(9.99), BigDecimal.valueOf(19.99)), capturedPrices);
    }

    @Test
    void testLoadItemBook_InvalidLine() {
        List<String[]> tsvLines = Arrays.asList(
                new String[]{"123456", "Test Item 1", "9.99"},
                new String[]{"Invalid line"}
        );

        when(mockFileLineReader.read("path/to/test.tsv", "\t")).thenReturn(tsvLines);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> itemBookLoaderComponent.loadItemBook(itemService));

        assertEquals("Invalid TSV file format. Expected 3 fields per line. Invalid line: [Invalid line]",
                exception.getMessage());

        verify(itemService, times(1)).createAndPersist(
                eq("123456"),
                eq("Test Item 1"),
                eq(BigDecimal.valueOf(9.99)),
                isNull(),
                isNull()
        );
    }
}
