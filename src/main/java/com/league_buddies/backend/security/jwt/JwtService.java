package com.league_buddies.backend.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String privateKey = "SDGJKHJK35346KHJ4/sdfjkhsdka98357qjahf2897452381!";
    public String generateToken(String username) {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

//    public boolean isTokenValid(String token) {
//        Jwts.parserBuilder()
//                .setSigningKey(getSignInKey())
//                .pa
//    }
//
//    private boolean isTokenExpired(String token) {
//        return t
//    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(privateKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
