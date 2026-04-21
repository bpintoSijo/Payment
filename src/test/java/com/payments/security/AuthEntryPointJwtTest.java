package com.payments.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class AuthEntryPointJwtTest {

    private final AuthEntryPointJwt authEntryPointJwt = new AuthEntryPointJwt();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void commence_setsUnauthorizedStatus() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Unauthorized"));

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_setsJsonContentType() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Unauthorized"));

        assertThat(response.getContentType()).contains("application/json");
    }

    @Test
    void commence_bodyContainsExpectedFields() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Bad credentials"));

        Map<String, Object> body = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(body.keySet()).containsExactlyInAnyOrder("status", "error", "message", "path");
    }

    @Test
    void commence_bodyContainsCorrectStatus() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Unauthorized"));

        Map<String, Object> body = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(body).containsEntry("status", HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_bodyContainsCorrectMessage() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Bad credentials"));

        Map<String, Object> body = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(body).containsEntry("message", "Bad credentials");
    }

    @Test
    void commence_bodyContainsCorrectPath() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/secure");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Unauthorized"));

        Map<String, Object> body = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(body).containsEntry("path", "/api/secure");
    }

    @Test
    void commence_bodyContainsCorrectError() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authEntryPointJwt.commence(request, response, new BadCredentialsException("Unauthorized"));

        Map<String, Object> body = objectMapper.readValue(response.getContentAsString(), Map.class);
        assertThat(body).containsEntry("error", "Unauthorized");
    }
}