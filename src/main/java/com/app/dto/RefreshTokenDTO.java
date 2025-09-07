package com.app.dto;

import java.util.Objects;

public class RefreshTokenDTO {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken)  {
        this.refreshToken = refreshToken;
    }


    @Override
    public String toString() {
        return "RefreshTokenDTO(refreshToken=" + refreshToken + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(refreshToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokenDTO that = (RefreshTokenDTO) o;
        return Objects.equals(refreshToken, that.getRefreshToken());
    }
}
