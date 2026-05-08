package org.example.userservice.support.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import org.example.userservice.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;

    private final String issuer;

    private final long expireSeconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expire-seconds}") long expireSeconds) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expireSeconds = expireSeconds;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expireSeconds);

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withSubject(String.valueOf(user.getId()))
                .withClaim("userId", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("nickname", user.getNickname())
                .sign(algorithm);
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }
}
