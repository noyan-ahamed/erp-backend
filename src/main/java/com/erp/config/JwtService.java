package com.erp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {
//    public static void main(String[] args) {
//        String key = Base64.getEncoder().encodeToString(
//                Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded()
//        );
//        System.out.println("Noyon eta dekho: "+key);
//    }
//    private static final String SECRET_KEY = "7060df9ac97d9a29b30a6dc6735b28d9a4ae809a3865795c6e1a2d6a0136c07846b2c21b04bfc0b4baac5b99a940efb32de9a748b367faea5f78c811358204c53a70e4acfcc90d8925a9350ada1e9f56a225f485ebfdb6cbe88d3df5a2f2d7edc4a004333deebbe7de7ed98d22d58c47290b2d9032a7c528c547a58bf97f55c7";

    private static  final String SECRET_KEY = "CMWX1Qg6yi7z1yLeyzbqYGQKjfXki9OFcKnyIloIoYw=";
    public String extractUserName(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
            ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateTokenWithUserDetails(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }


    //token validation
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //token Expiration
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}

