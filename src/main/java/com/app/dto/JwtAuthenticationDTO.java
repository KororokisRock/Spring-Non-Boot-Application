package com.app.dto;

import java.util.Objects;

public class JwtAuthenticationDTO {
    private String token;
    private String refreshToken;

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "JwtAuthenticationDTO(token=" + token + ", refreshToken=" + refreshToken + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, refreshToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtAuthenticationDTO that = (JwtAuthenticationDTO) o;
        return Objects.equals(token, that.getToken()) &&
            Objects.equals(refreshToken, that.getRefreshToken());
    }
}
