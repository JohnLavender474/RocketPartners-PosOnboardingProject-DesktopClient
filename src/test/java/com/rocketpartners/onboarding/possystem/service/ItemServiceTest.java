package com.rocketpartners.onboarding.possystem.service;

import com.rocketpartners.onboarding.commons.model.Item;
import com.rocketpartners.onboarding.possystem.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository itemRepository;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemService = new ItemService(itemRepository);
    }

    @Test
    void testCreateAndPersist_Success() {
        String upc = "1234567890";
        String name = "Test Item";
        BigDecimal unitPrice = BigDecimal.valueOf(10.00);
        String category = "Category";
        String description = "Description";

        when(itemRepository.itemExists(upc)).thenReturn(false);

        Item item = itemService.createAndPersist(upc, name, unitPrice, category, description);

        assertNotNull(item);
        assertEquals(upc, item.getUpc());
        assertEquals(name, item.getName());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(category, item.getCategory());
        assertEquals(description, item.getDescription());

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).saveItem(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();
        assertEquals(upc, capturedItem.getUpc());
        assertEquals(name, capturedItem.getName());
        assertEquals(unitPrice, capturedItem.getUnitPrice());
        assertEquals(category, capturedItem.getCategory());
        assertEquals(description, capturedItem.getDescription());
    }

    @Test
    void testCreateAndPersist_UpcExists() {
        String upc = "1234567890";
        String name = "Test Item";
        BigDecimal unitPrice = BigDecimal.valueOf(10.00);

        when(itemRepository.itemExists(upc)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                itemService.createAndPersist(upc, name, unitPrice, null, null));
        assertEquals("Item with UPC " + upc + " already exists", exception.getMessage());
    }

    @Test
    void testCreateAndPersist_InvalidInputs() {
        String validUpc = "1234567890";
        String validName = "Test Item";
        BigDecimal validPrice = BigDecimal.valueOf(10.00);

        assertThrows(IllegalArgumentException.class, () -> itemService.createAndPersist("", validName, validPrice,
                null, null));
        assertThrows(IllegalArgumentException.class, () -> itemService.createAndPersist(validUpc, "", validPrice,
                null, null));
        assertThrows(IllegalArgumentException.class, () -> itemService.createAndPersist(validUpc, validName,
                BigDecimal.valueOf(-1), null, null));
    }

    @Test
    void testSaveItem() {
        Item item = new Item();
        item.setUpc("1234567890");
        item.setName("Test Item");
        item.setUnitPrice(BigDecimal.valueOf(10.00));

        itemService.saveItem(item);

        verify(itemRepository).saveItem(item);
    }

    @Test
    void testGetItemByUpc() {
        String upc = "1234567890";
        Item expectedItem = new Item();
        expectedItem.setUpc(upc);

        when(itemRepository.getItemByUpc(upc)).thenReturn(expectedItem);

        Item item = itemService.getItemByUpc(upc);
        assertNotNull(item);
        assertEquals(upc, item.getUpc());
    }

    @Test
    void testGetAllItems() {
        List<Item> expectedItems = Arrays.asList(new Item(), new Item());

        when(itemRepository.getAllItems()).thenReturn(expectedItems);

        List<Item> items = itemService.getAllItems();
        assertEquals(expectedItems.size(), items.size());
    }

    @Test
    void testDeleteItemByUpc() {
        String upc = "1234567890";

        itemService.deleteItemByUpc(upc);

        verify(itemRepository).deleteItemByUpc(upc);
    }

    @Test
    void testGetItemsByName() {
        String name = "Test Item";
        List<Item> expectedItems = Arrays.asList(new Item(), new Item());

        when(itemRepository.getItemsByName(name)).thenReturn(expectedItems);

        List<Item> items = itemService.getItemsByName(name);
        assertEquals(expectedItems.size(), items.size());
    }

    @Test
    void testGetItemsByCategory() {
        String category = "Category";
        List<Item> expectedItems = Arrays.asList(new Item(), new Item());

        when(itemRepository.getItemsByCategory(category)).thenReturn(expectedItems);

        List<Item> items = itemService.getItemsByCategory(category);
        assertEquals(expectedItems.size(), items.size());
    }

    @Test
    void testItemExists() {
        String upc = "1234567890";

        when(itemRepository.itemExists(upc)).thenReturn(true);

        assertTrue(itemService.itemExists(upc));
    }

    @Test
    void testGetRandomItems1() {
        List<Item> allItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Item item = new Item();
            item.setUpc(String.valueOf(i));
            allItems.add(item);
        }

        when(itemRepository.getAllItems()).thenReturn(allItems);

        List<Item> randomItems = itemService.getRandomItems(2);
        assertEquals(2, randomItems.size());
        assertNotEquals(randomItems.get(0), randomItems.get(1));
    }

    @Test
    void testGetRandomItems2() {
        List<Item> allItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Item item = new Item();
            item.setUpc(String.valueOf(i));
            allItems.add(item);
        }

        when(itemRepository.getAllItems()).thenReturn(allItems);

        List<Item> randomItems = itemService.getRandomItems(4);
        assertEquals(3, randomItems.size());
        assertTrue(randomItems.containsAll(allItems));
    }

    @Test
    void testGetRandomItemsNotIn() {
        Set<String> excludedUpcs = Set.of("0", "2", "3");
        List<Item> allItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Item item = new Item();
            item.setUpc(String.valueOf(i));
            allItems.add(item);
        }

        when(itemRepository.getAllItems()).thenReturn(allItems);

        List<Item> randomItems = itemService.getRandomItemsNotIn(excludedUpcs, 2);
        assertEquals(1, randomItems.size());
        assertEquals(allItems.get(1), randomItems.get(0));
    }
}

