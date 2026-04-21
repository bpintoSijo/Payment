package com.payments.repository;

import com.payments.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User buildUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .password("password")
                .build();
    }

    @Test
    void save_andFindById_returnsEntity() {
        User saved = repository.save(buildUser("john", "john@test.com"));
        assertThat(repository.findById(saved.getId())).isPresent()
                .get().extracting("username").isEqualTo("john");
    }

    @Test
    void findAll_returnsAllSavedUsers() {
        repository.save(buildUser("john", "john@test.com"));
        repository.save(buildUser("jane", "jane@test.com"));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void deleteById_removesEntity() {
        User saved = repository.save(buildUser("john", "john@test.com"));
        repository.deleteById(saved.getId());
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findByUsername_existingUsername_returnsUser() {
        repository.save(buildUser("john", "john@test.com"));
        assertThat(repository.findByUsername("john")).isPresent()
                .get().extracting("email").isEqualTo("john@test.com");
    }

    @Test
    void findByUsername_unknownUsername_returnsEmpty() {
        assertThat(repository.findByUsername("unknown")).isEmpty();
    }

    @Test
    void existsByUsername_existingUsername_returnsTrue() {
        repository.save(buildUser("john", "john@test.com"));
        assertThat(repository.existsByUsername("john")).isTrue();
    }

    @Test
    void existsByUsername_unknownUsername_returnsFalse() {
        assertThat(repository.existsByUsername("unknown")).isFalse();
    }

    @Test
    void existsByEmail_existingEmail_returnsTrue() {
        repository.save(buildUser("john", "john@test.com"));
        assertThat(repository.existsByEmail("john@test.com")).isTrue();
    }

    @Test
    void existsByEmail_unknownEmail_returnsFalse() {
        assertThat(repository.existsByEmail("unknown@test.com")).isFalse();
    }

    @Test
    void save_duplicateUsername_throwsException() {
        repository.save(buildUser("john", "john@test.com"));
        assertThatThrownBy(() -> repository.saveAndFlush(buildUser("john", "other@test.com")))
                .isInstanceOf(Exception.class);
    }

    @Test
    void save_duplicateEmail_throwsException() {
        repository.save(buildUser("john", "john@test.com"));
        assertThatThrownBy(() -> repository.saveAndFlush(buildUser("other", "john@test.com")))
                .isInstanceOf(Exception.class);
    }

    @Test
    void count_returnsCorrectCount() {
        repository.save(buildUser("john", "john@test.com"));
        repository.save(buildUser("jane", "jane@test.com"));
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void save_emptyRepository_countIsZero() {
        assertThat(repository.count()).isZero();
    }
}