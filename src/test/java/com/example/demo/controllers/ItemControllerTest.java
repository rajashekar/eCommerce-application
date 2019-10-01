package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems() {
        List<Item> list = new ArrayList<Item>();
        list.add(getItem());
        when(itemRepository.findAll()).thenReturn(list);
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertEquals(1, items.size());
    }

    @Test
    public void getItemByIdSuccess() {
        Item item = getItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(item.getId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item i = response.getBody();
        assertEquals(item.getName(), i.getName());
    }

    @Test
    public void getItemByIdFail() {
        when(itemRepository.findById(1l)).thenReturn(Optional.empty());
        ResponseEntity<Item> response = itemController.getItemById(1l);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameSuccess() {
        List<Item> list = new ArrayList<Item>();
        Item item = getItem();
        list.add(item);
        when(itemRepository.findByName(item.getName())).thenReturn(list);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(item.getName());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertEquals(1, items.size());
    }

    @Test
    public void getItemsByNameFail() {
        List<Item> list = new ArrayList<Item>();
        when(itemRepository.findByName("iPhone")).thenReturn(list);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("iPhone");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    public Item getItem() {
        Item item = new Item();
        item.setId(1l);
        item.setDescription("Phone with i");
        item.setName("iPhone");
        item.setPrice(new BigDecimal(999.99));
        return item;
    }
}