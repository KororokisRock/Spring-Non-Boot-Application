package com.app.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.app.dto.JwtAuthenticationDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expiration}")
    private long jwtTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long jwtRefreshTokenExpiration;

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);


    public JwtAuthenticationDTO generateAuthToken(String username) {
        JwtAuthenticationDTO jwtDto = new JwtAuthenticationDTO();
        jwtDto.setToken(generateJwtToken(username));
        jwtDto.setRefreshToken(generateRefreshToken(username));
        return jwtDto;
    }

    public JwtAuthenticationDTO refreshBaseToken(String username, String refreshToken) {
        JwtAuthenticationDTO jwtDto = new JwtAuthenticationDTO();
        jwtDto.setToken(generateJwtToken(username));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                            .verifyWith(getSingInKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

        return claims.getSubject();
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            return getUsernameFromToken(token);
        }
        return null;
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return true;
        } catch (ExpiredJwtException e) {
            logger.error(e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error(e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error(e.getMessage());
        } catch (SecurityException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String generateJwtToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(jwtTokenExpiration).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(username)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private String generateRefreshToken(String username) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(jwtRefreshTokenExpiration).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(username)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

