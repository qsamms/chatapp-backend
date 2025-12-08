package com.chatappbackend.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private final String SECRET_KEY = System.getenv("JWT_SECRET");

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
  }

  public String generateAccessToken(String username) {
    long EXPIRATION_TIME_MS = 60 * 10 * 1000; // 10 minutes
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(String username) {
    long EXPIRATION_TIME_MS = 60 * 60 * 1000 * 12; // 12 hours
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public String extractUsername(String token) throws MalformedJwtException {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateToken(String token) throws ExpiredJwtException {
    return !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration()
        .before(new Date());
  }
}
