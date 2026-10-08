package com.KpotTipouts.KpotPay.Service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

   public String generateToken(String email) {
       SecretKey key = getSigningKey();
       return Jwts
               .builder()
               .subject(email)
               .issuedAt(new Date())
               .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
               .signWith(key)
               .compact();
    }
    public String extractEmail(String token) {
        SecretKey key = getSigningKey();
        return Jwts
                .parser()
               .verifyWith(key)
               .build()
               .parseSignedClaims(token)
               .getPayload().getSubject();
    }
}
