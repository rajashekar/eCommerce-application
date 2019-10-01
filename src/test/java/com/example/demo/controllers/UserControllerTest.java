package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptEncoder);
    }

    @Test
    public void createUserSuccess() {
        when(bCryptEncoder.encode("password")).thenReturn("hashedpassword");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Test");
        r.setPassword("password");
        r.setConfirmPassword("password");

        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(u.getUsername(), r.getUsername());
        assertEquals("hashedpassword", u.getPassword());
    }

    @Test
    public void createUserFail() {
        when(bCryptEncoder.encode("password")).thenReturn("hashedpassword");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Test");
        r.setPassword("password");
        r.setConfirmPassword("passwor");

        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findByUsernameSuccess() {
        String username = "Test";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(u.getUsername(), username);
    }

    @Test
    public void findByUsernameFail() {
        String username = "Test";
        when(userRepository.findByUsername(username)).thenReturn(null);
        ResponseEntity<User> response = userController.findByUserName(username);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findByIdSuccess() {
        User user = new User();
        user.setId(1l);
        when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(1l);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(u.getId(), 1l);
    }

    @Test
    public void findByIdFail() {
        when(userRepository.findById(1l)).thenReturn(Optional.empty());
        ResponseEntity<User> response = userController.findById(1l);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}