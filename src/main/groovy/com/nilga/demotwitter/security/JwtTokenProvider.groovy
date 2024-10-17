package com.nilga.demotwitter.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.crypto.SecretKey
import java.util.Date

/**
 * A component responsible for generating and validating JWT tokens.
 */
@Component
class JwtTokenProvider {

    @Value('${jwt.secret}')
    private String JWT_SECRET

    /**
     * Converts the secret key string into a SecretKey object for use with the HS512 algorithm.
     *
     * @return the SecretKey generated from the JWT_SECRET.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.bytes)
    }

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username for which the token is generated.
     * @return the generated JWT token as a string.
     */
    String generateToken(String username) {
        Date now = new Date()
        Date expiryDate = new Date(now.time + 86400000)  // Token validity: 24 hours

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)  // Sign with the SecretKey using HS512
                .compact()
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token.
     * @return the username contained in the token.
     */
    String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()

        return claims.getSubject()
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
            return true
        } catch (SignatureException ex) {
            println("Invalid JWT signature: ${ex.message}")
        } catch (Exception ex) {
            println("Invalid JWT token: ${ex.message}")
        }
        return false
    }
}
