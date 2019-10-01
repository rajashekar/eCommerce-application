package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void submitFail() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserSuccess() {
        User user = getUser();
        List<UserOrder> userOrders = new ArrayList<UserOrder>();
        userOrders.add(getOrder());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> resOrders = response.getBody();
        assertEquals(userOrders.size(), resOrders.size());
    }

    @Test
    public void getOrdersForUserFail() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
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
}