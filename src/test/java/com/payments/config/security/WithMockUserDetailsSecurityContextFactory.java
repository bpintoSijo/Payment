package com.payments.config.security;

import com.payments.domain.User;
import com.payments.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Set;

public class WithMockUserDetailsSecurityContextFactory
        implements WithSecurityContextFactory<WithMockUserDetails> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserDetails annotation) {
        User user = User.builder()
                .id(annotation.id())
                .username(annotation.username())
                .email(annotation.username())
                .password("password")
                .roles(Set.of(annotation.role()))
                .build();

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}