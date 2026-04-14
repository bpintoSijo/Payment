package com.payments.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
/**
 * Utilities for jwt token
 * */
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    @Value("${app.jwt.cookie-name")
    private String cookieName;

    /**
     * Generate a jwt token from authentication
     * @param authentication Authentication used to generate jwt token.
     * @return Jwt token.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * Generate a token from username.
     * @param username username used to generate jwt token.
     * @return Jwt token.
     * */
    public String generateTokenFromUsername(String username) {
        return buildToken(username);
    }

    /**
     * Extract username from a jwt token.
     * @param token jwt token.
     * @return Username extracted.
     * */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Valid a jwt token by checking
     * @param jwtToken token.
     * @return true if token is valid else false.
     * */
    public boolean validateJwtToken(String jwtToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwtToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT invalide : {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expiré : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT non supporté : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string vide : {}", e.getMessage());
        }
        return false;
    }

    /**
     * Build a crypted token from a username
     * @param username used to build a token.
     * @return jwt token
     * */
    private String buildToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get the signing key to encrypt jwt token.
     * @return Secret key for encryption.
     * */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(jwtSecret.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
