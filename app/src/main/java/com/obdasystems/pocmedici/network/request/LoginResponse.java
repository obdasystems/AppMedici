package com.obdasystems.pocmedici.network.request;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LoginResponse {
    @SerializedName("accessToken")
    protected String accessToken;
    @SerializedName("expires")
    protected LocalDate expiration;

    public LoginResponse() {

    }

    public LoginResponse(@Nonnull String accessToken) {
        this(accessToken, null);
    }

    public LoginResponse(@Nonnull String accessToken,
                         @Nullable LocalDate expiration) {
        this.setAccessToken(accessToken);
        this.setExpiration(expiration);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LoginResponse setAccessToken(@Nonnull String accessToken) {
        this.accessToken = Objects.requireNonNull(accessToken, "accessToken cannot be null");
        return this;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public LoginResponse setExpiration(@Nullable LocalDate expiration) {
        this.expiration = expiration;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponse that = (LoginResponse) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(expiration, that.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, expiration);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accessToken", accessToken)
                .add("expiration", expiration)
                .toString();
    }

}

