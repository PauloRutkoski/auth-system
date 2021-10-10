package com.rutkoski.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutkoski.auth.domain.User;
import com.rutkoski.auth.services.AuthService;
import com.rutkoski.auth.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private AuthController controller;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService service;

    private User example;

    @BeforeEach
    public void setup() {
        standaloneSetup(this.controller, this.jwtUtils, this.service);
        this.example = new User(null, "UserTest", "123456");
    }

    //==================================================================================================================
    //Authentication
    //==================================================================================================================
    @Test
    void successAuthenticate() throws Exception {
        when(service.validateAuth(example)).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(example));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getHeader("token"));
        assertNotNull(result.getResponse().getHeader("refresh_token"));
    }

    @Test
    void failAuthenticate() throws Exception {
        when(service.validateAuth(example)).thenReturn(false);

        RequestBuilder request = MockMvcRequestBuilders.post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(example));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), result.getResponse().getStatus());
        assertNull(result.getResponse().getHeader("token"));
        assertNull(result.getResponse().getHeader("refresh_token"));
    }

    //==================================================================================================================
    //Registration
    //==================================================================================================================
    @Test
    void successRegister() throws Exception {
        when(service.validatePersist(example)).thenCallRealMethod();
        when(service.alreadyExists(example.getUsername())).thenReturn(false);
        when(service.persist(example)).thenReturn(new User(1L, example.getUsername(), example.getPassword()));

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(example));
        mockMvc.perform(request).andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    void noPasswordRegister() throws Exception {
        example.setPassword(null);
        RequestBuilder request = MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(example));
        mockMvc.perform(request).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void noUsernameRegister() throws Exception {
        example.setUsername(null);
        RequestBuilder request = MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(example));
        mockMvc.perform(request).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void userAlreadyExistsRegister() throws Exception {
        when(service.validatePersist(example)).thenCallRealMethod();
        when(service.alreadyExists(example.getUsername())).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(example));
        mockMvc.perform(request).andExpect(status().is(HttpStatus.ALREADY_REPORTED.value()));
    }

    //==================================================================================================================
    //Refresh
    //==================================================================================================================
    @Test
    void successRefresh() throws Exception {
        String token = jwtUtils.generateToken(example.getUsername(), 1);
        Map<String, String> map = new HashMap<>() {{
            put("refresh_token", token);
        }};

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(map));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertNotNull(result.getResponse().getHeader("token"));
        assertNotNull(result.getResponse().getHeader("refresh_token"));
    }

    @Test
    void nullTokenRefresh() throws Exception {
        Map<String, String> map = new HashMap<>() {{
            put("refresh_token", null);
        }};

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(map));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertNull(result.getResponse().getHeader("token"));
        assertNull(result.getResponse().getHeader("refresh_token"));
    }

    @Test
    void expiredTokenRefresh() throws Exception {
        //Generate a token that will expire soon, 1 milisecond
        ReflectionTestUtils.setField(jwtUtils, "JWT_REFRESH_VALIDITY", 1L);
        String token = jwtUtils.generateToken(example.getUsername(), 1);
        Map<String, String> map = new HashMap<>() {{
            put("refresh_token", token);
        }};

        Thread.sleep(10);

        RequestBuilder request = MockMvcRequestBuilders.post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(map));
        MvcResult result = mockMvc.perform(request).andReturn();

        assertEquals(HttpStatus.FORBIDDEN.value(), result.getResponse().getStatus());
        assertNull(result.getResponse().getHeader("token"));
        assertNull(result.getResponse().getHeader("refresh_token"));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}