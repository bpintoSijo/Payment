package com.payments.controller.rest;

import com.payments.domain.User;
import com.payments.dto.authentication.AuthenticationDTO;
import com.payments.security.JwtUtils;
import com.payments.security.UserDetailsImpl;
import com.payments.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    /**
     * POST /api/auth/login
     * @param loginRequest Body : { "username": "john", "password": "secret" }
     * @return JWT response if credentials are valid.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationDTO.JwtResponse> login(
            @Valid @RequestBody AuthenticationDTO.LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        java.util.Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        return ResponseEntity.ok(new AuthenticationDTO.JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    /**
     * POST /api/auth/signup
     * @param signupRequest Body : { "username": "john", "email": "john@mail.com", "password": "secret" }
     * @return Create a new user.
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthenticationDTO.MessageResponse> signup(
            @Valid @RequestBody AuthenticationDTO.SignupRequest signupRequest) {

        try {
            User user = userService.register(signupRequest);
            return ResponseEntity.ok(
                    new AuthenticationDTO.MessageResponse("User created: " + user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthenticationDTO.MessageResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/auth/logout
     * JWT stateless side, logout is managed client side (removal of token).
     * This path simply confirm the intention.
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticationDTO.MessageResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthenticationDTO.MessageResponse(
                "Logout successful. Remove token client side.")
        );
    }

    /**
     * GET /api/auth/me
     * Return connected user's information.
     * A JWT token must be valid in Authorization header.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticationDTO.UserInformationResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userService.getCurrentUserInfo(userDetails));
    }
}
