package com.payments.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final String SECRET = "mysecretkeyformytestsissuperlongenoughtobe256bits!!";
    private static final int EXPIRATION_MS = 3600000;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION_MS);
        ReflectionTestUtils.setField(jwtUtils, "cookieName", "jwt-cookie");
    }

    @Test
    void generateTokenFromUsername_returnsNonNullToken() {
        assertThat(jwtUtils.generateTokenFromUsername("john")).isNotNull();
    }

    @Test
    void getUsernameFromJwtToken_returnsCorrectUsername() {
        String token = jwtUtils.generateTokenFromUsername("john");
        assertThat(jwtUtils.getUsernameFromJwtToken(token)).isEqualTo("john");
    }

    @Test
    void validateJwtToken_validToken_returnsTrue() {
        String token = jwtUtils.generateTokenFromUsername("john");
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    void validateJwtToken_malformedToken_returnsFalse() {
        assertThat(jwtUtils.validateJwtToken("malformed.token")).isFalse();
    }

    @Test
    void validateJwtToken_emptyString_returnsFalse() {
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    @Test
    void validateJwtToken_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        String token = jwtUtils.generateTokenFromUsername("john");
        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }

    @Test
    void generateJwtToken_fromAuthentication_returnsValidToken() {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getUsername()).thenReturn("john");
        when(userDetails.getAuthorities()).thenReturn(List.of());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUsernameFromJwtToken(token)).isEqualTo("john");
    }

    @Test
    void generateTokenFromUsername_differentUsers_returnsDifferentTokens() {
        String token1 = jwtUtils.generateTokenFromUsername("john");
        String token2 = jwtUtils.generateTokenFromUsername("jane");
        assertThat(token1).isNotEqualTo(token2);
    }
}