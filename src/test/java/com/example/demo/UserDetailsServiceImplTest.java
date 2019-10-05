package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.security.UserDetailsServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImplTest {
    private UserDetailsServiceImpl userDetailsService;
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    public void loadUserByUsernameSuccess() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameFail() {
        User user = getUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
        userDetailsService.loadUserByUsername(user.getUsername());
    }

    public User getUser() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("password");
        return user;
    }
}