package com.example.demoSWP.service;

import com.example.demoSWP.entity.Account;
import com.example.demoSWP.enums.Role;
import com.example.demoSWP.repository.AuthenticationRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class TokenService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    private final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";

    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Account account) {
        // Lấy thông tin doctor nếu có (trường hợp account là bác sĩ)
        Long doctorId = (account.getDoctor() != null) ? account.getDoctor().getDoctorId() : null;

        // Tạo claims (payload của token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", account.getRole().name());
        if (doctorId != null) {
            claims.put("doctorId", doctorId);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(account.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24h
                .signWith(getSigninKey())
                .compact();
    }


    // form token to Claim Object
    public Claims extractAllClaims(String token) {
        return  Jwts.parser().
                verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // get userName form CLAIM
    public Account extractAccount(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();
        String roleName = (String) claims.get("role");

        Account account = authenticationRepository.findAccountByEmail(email);
        if (account != null && roleName != null) {
            account.setRole(Role.valueOf(roleName)); // Gán lại role từ token
        }
        return account;
    }



    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    // get Expiration form CLAIM
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    // from claim and extract specific data type.
    public <T> T extractClaim(String token, Function<Claims,T> resolver){
        Claims claims = extractAllClaims(token);
        return  resolver.apply(claims);
    }
}