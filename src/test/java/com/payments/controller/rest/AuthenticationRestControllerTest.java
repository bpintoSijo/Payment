package com.payments.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.config.BaseSecurityTest;
import com.payments.config.TestConfig;
import com.payments.domain.User;
import com.payments.dto.authentication.AuthenticationDTO;
import com.payments.security.JwtUtils;
import com.payments.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@ActiveProfiles("test")
class AuthenticationRestControllerTest extends BaseSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserService userService;

    // -------------------------------------------------------------------------
    // POST /api/auth/login
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/auth/login — Try to login with valid data")
    void login_withValidCredentials_returnsJwtResponse() throws Exception {
        AuthenticationDTO.LoginRequest loginRequest =
                new AuthenticationDTO.LoginRequest("john", "secret");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@mail.com"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    @DisplayName("POST /api/auth/login — Try to login with invalid credentials")
    void login_withInvalidCredentials_returns401() throws Exception {
        AuthenticationDTO.LoginRequest loginRequest =
                new AuthenticationDTO.LoginRequest("john", "wrongPassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login — Try to login with empty data")
    void login_withMissingFields_returns400() throws Exception {
        String emptyBody = "{}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login — Try to login with empty username")
    void login_withMissingUsername_returns400() throws Exception {
        String emptyBody = "{username:\"\", password:\"secret\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login — Try to login with empty password")
    void login_withMissingPassword_returns400() throws Exception {
        String emptyBody = "{username:\"john\", password:\"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyBody))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/signup
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/auth/signup — call UserService.register() with valid data")
    void signup_withValidData_returnsSuccessMessage() throws Exception {
        AuthenticationDTO.SignupRequest signupRequest =
                new AuthenticationDTO.SignupRequest("john", "john@mail.com", "secret", Set.of("ROLE_USER"));

        User createdUser = new User();
        createdUser.setUsername("john");

        when(userService.register(any())).thenReturn(createdUser);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User created: john"));
    }

    @Test
    @DisplayName("POST /api/auth/signup — call UserService.register() with a duplicate username")
    void signup_withDuplicateUsername_returns400() throws Exception {
        AuthenticationDTO.SignupRequest signupRequest =
                new AuthenticationDTO.SignupRequest("john", "john@mail.com", "secret", Set.of("ROLE_USER"));

        when(userService.register(any()))
                .thenThrow(new IllegalArgumentException("Username already taken"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already taken"));
    }

    @Test
    @DisplayName("POST /api/auth/signup — call UserService.register() with a duplicate email")
    void signup_withDuplicateEmail_returns400() throws Exception {
        AuthenticationDTO.SignupRequest signupRequest =
                new AuthenticationDTO.SignupRequest("john", "john@mail.com", "secret", Set.of("ROLE_USER"));

        when(userService.register(any()))
                .thenThrow(new IllegalArgumentException("Email already taken"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already taken"));
    }

    @Test
    @DisplayName("POST /api/auth/signup — call UserService.register() with a low length username")
    void signup_withUsernameLengthToLow_returns400() throws Exception {
        AuthenticationDTO.SignupRequest signupRequest =
                new AuthenticationDTO.SignupRequest("jo", "john@mail.com", "secret", Set.of("ROLE_USER"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/signup — call UserService.register() with a low length password")
    void signup_withPasswordLengthToLow_returns400() throws Exception {
        AuthenticationDTO.SignupRequest signupRequest =
                new AuthenticationDTO.SignupRequest("john", "john@mail.com", "secre", Set.of("ROLE_USER"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/logout
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/auth/logout — call UserService.logout() while authenticated")
    void logout_whenAuthenticated_returnsSuccessMessage() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "Logout successfull. Remove token client side."));
    }

    @Test
    @DisplayName("POST /api/auth/logout — call UserService.logout() while unauthenticated")
    void logout_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /api/auth/me
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/auth/me — call UserService.getCurrentUserInfo() while authenticated")
    void getCurrentUser_whenAuthenticated_returnsUserInfo() throws Exception {
        AuthenticationDTO.UserInformationResponse userInfo =
                new AuthenticationDTO.UserInformationResponse(1L, "john", "john@mail.com", Set.of("ROLE_USER"));

        when(userService.getCurrentUserInfo(any())).thenReturn(userInfo);

        mockMvc.perform(get("/api/auth/me")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    @DisplayName("POST /api/auth/me — call UserService.getCurrentUserInfo() while unauthenticated")
    void getCurrentUser_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}