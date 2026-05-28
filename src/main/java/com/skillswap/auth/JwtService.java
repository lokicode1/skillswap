package com.skillswap.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final String issuer;
  private final SecretKey key;
  private final long accessTokenMinutes;

  public JwtService(
      @Value("${skillswap.jwt.issuer}") String issuer,
      @Value("${skillswap.jwt.secret}") String secret,
      @Value("${skillswap.jwt.accessTokenMinutes}") long accessTokenMinutes
  ) {
    this.issuer = issuer;
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenMinutes = accessTokenMinutes;
  }

  public String issueAccessToken(Long userId, String email, String role) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(accessTokenMinutes * 60);
    return Jwts.builder()
        .issuer(issuer)
        .subject(String.valueOf(userId))
        .claim("email", email)
        .claim("role", role)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public Jws<Claims> parse(String jwt) {
    return Jwts.parser()
        .verifyWith(key)
        .requireIssuer(issuer)
        .build()
        .parseSignedClaims(jwt);
  }
}

