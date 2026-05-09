package org.example.gatewayservice.security.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenVerifier {

    private final JWTVerifier verifier;

    public JwtTokenVerifier(JwtAuthenticationProperties jwtAuthenticationProperties) {
        this.verifier = JWT.require(Algorithm.HMAC256(jwtAuthenticationProperties.getSecret()))
                .withIssuer(jwtAuthenticationProperties.getIssuer())
                .build();
    }

    public AuthenticatedUser verify(String token) {
        DecodedJWT decodedJwt = verifier.verify(token);
        return new AuthenticatedUser(
                decodedJwt.getClaim("userId").asLong(),
                decodedJwt.getClaim("username").asString(),
                decodedJwt.getClaim("nickname").asString());
    }
}
