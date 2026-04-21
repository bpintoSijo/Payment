package com.payments.service;

import com.payments.domain.User;
import com.payments.dto.authentication.AuthenticationDTO;
import com.payments.repository.UserRepository;
import com.payments.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private AuthenticationDTO.SignupRequest buildRequest(String username, String email, Set<String> roles) {
        return new AuthenticationDTO.SignupRequest(username, email, "password", roles);
    }

    @Test
    void register_validRequest_savesAndReturnsUser() {
        AuthenticationDTO.SignupRequest request = buildRequest("john", "john@test.com", Set.of("ROLE_USER"));
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.register(request);

        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getEmail()).isEqualTo("john@test.com");
        assertThat(result.getPassword()).isEqualTo("encoded");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsIllegalArgumentException() {
        AuthenticationDTO.SignupRequest request = buildRequest("john", "john@test.com", null);
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    void register_duplicateEmail_throwsIllegalArgumentException() {
        AuthenticationDTO.SignupRequest request = buildRequest("john", "john@test.com", null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already taken");
    }

    @Test
    void register_nullRoles_assignsDefaultRole() {
        AuthenticationDTO.SignupRequest request = buildRequest("john", "john@test.com", null);
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.register(request);

        assertThat(result.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void register_emptyRoles_assignsDefaultRole() {
        AuthenticationDTO.SignupRequest request = buildRequest("john", "john@test.com", Set.of());
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.register(request);

        assertThat(result.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void getCurrentUserInfo_returnsCorrectResponse() {
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("john");
        when(userDetails.getEmail()).thenReturn("john@test.com");
        when(userDetails.getAuthorities()).thenReturn(List.of());

        AuthenticationDTO.UserInformationResponse result = userService.getCurrentUserInfo(userDetails);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getEmail()).isEqualTo("john@test.com");
    }

    @Test
    void findByUsername_existingUsername_returnsUser() {
        User user = User.builder().username("john").email("john@test.com").password("password").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThat(userService.findByUsername("john")).isEqualTo(user);
    }

    @Test
    void findByUsername_unknownUsername_throwsIllegalArgumentException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown user: unknown");
    }
}