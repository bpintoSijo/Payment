package com.payments.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Principal DTO for authentication
 * */
public class AuthenticationDTO {

    private AuthenticationDTO() {}

    /**
     * Login request
     * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    /**
     * Signup request
     * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignupRequest {
        @NotBlank
        @Size(min = 3, max = 40)
        private String username;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6, max = 100)
        private String password;

        // Optional : ["ROLE_USER", "ROLE_ADMIN"]
        private Set<String> roles;
    }

    /**
     * Jwt response
     * */
    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private Set<String> roles;

        public JwtResponse(String token, Long id, String username, String email, Set<String> roles) {
            this.token = token;
            this.id = id;
            this.username = username;
            this.email = email;
            this.roles = roles;
        }
    }

    /**
     * Generic response
     * */
    @Data
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }
    }

    /**
     * Information's user response
     * */
    @Data
    public static class UserInformationResponse {
        private Long id;
        private String username;
        private String email;
        private Set<String> roles;

        public UserInformationResponse(Long id, String username, String email, Set<String> roles) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.roles = roles;
        }
    }
}
