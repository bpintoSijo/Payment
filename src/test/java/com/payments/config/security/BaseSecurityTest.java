package com.payments.config.security;

import com.payments.domain.User;
import com.payments.security.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

public abstract class BaseSecurityTest {

    protected UserDetailsImpl userDetails;
    protected Authentication authentication;

    protected final User defaultTestUser = User.builder()
            .id(1L).username("John").email("John@test.com").password("password").roles(Set.of("ROLE_USER"))
            .build();

    private void setUpAuthentication() {
        userDetails = UserDetailsImpl.build(defaultTestUser);

        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @BeforeEach
    protected void setUp() {
        setUpAuthentication();
    }

    @AfterEach
    protected void tearDown() {
        SecurityContextHolder.clearContext();
    }
}