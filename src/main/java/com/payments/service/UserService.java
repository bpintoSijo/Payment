package com.payments.service;

import com.payments.domain.User;
import com.payments.dto.authentication.AuthenticationDTO;
import com.payments.repository.UserRepository;
import com.payments.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user from request.
     * @param request user details
     * @return new saved User
     */
    @Transactional
    public User register(AuthenticationDTO.SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already taken.");
        }

        Set<String> roles = (request.getRoles() == null || request.getRoles().isEmpty())
                ? Set.of("ROLE_USER") : request.getRoles();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        return userRepository.save(user);
    }

    /**
     * Get current authenticated user.
     * @param userDetails of the connected user.
     * @return User's information.
     * */
    @Transactional(readOnly = true)
    public AuthenticationDTO.UserInformationResponse getCurrentUserInfo(UserDetailsImpl userDetails) {
        return new AuthenticationDTO.UserInformationResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(java.util.stream.Collectors.toSet())
        );
    }

    /**
     * Find by username
     * @param username used to find user
     * @return user with username
     * */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown user: " + username));
    }
}
