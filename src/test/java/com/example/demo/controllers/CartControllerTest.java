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
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Item item = getItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest());
        Cart cart = response.getBody();
        assertEquals(3, cart.getItems().size());
    }

    @Test
    public void addToCartUserFail() {
        when(userRepository.findByUsername(getUser().getUsername())).thenReturn(null);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartItemFail() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Item item = getItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Item item = getItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ModifyCartRequest mcr = modifyCartRequest();
        mcr.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest());
        Cart cart = response.getBody();
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void removeFromCartUserFail() {
        when(userRepository.findByUsername(getUser().getUsername())).thenReturn(null);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartItemFail() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        Item item = getItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    public UserOrder getOrder() {
        UserOrder order = new UserOrder();
        order.setId(1l);
        order.setTotal(new BigDecimal(999.99));
        List<Item> list = new ArrayList<Item>();
        list.add(getItem());
        order.setItems(list);
        order.setUser(getUser());
        return order;
    }

    public Item getItem() {
        Item item = new Item();
        item.setId(1l);
        item.setDescription("Phone with i");
        item.setName("iPhone");
        item.setPrice(new BigDecimal(999.99));
        return item;
    }

    public User getUser() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("password");
        user.setCart(getCart());
        return user;
    }

    public Cart getCart() {
        Cart cart = new Cart();
        cart.setId(1l);
        cart.setTotal(new BigDecimal(999.99));
        List<Item> list = new ArrayList<Item>();
        list.add(getItem());
        cart.setItems(list);
        return cart;
    }

    public ModifyCartRequest modifyCartRequest() {
        ModifyCartRequest mcr = new ModifyCartRequest();
        mcr.setItemId(1l);
        mcr.setQuantity(2);
        mcr.setUsername("Test");
        return mcr;
    }

}