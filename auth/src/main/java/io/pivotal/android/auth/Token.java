/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;

public class Token {

    private final String mAccessToken;
    private final String mRefreshToken;

    public Token(final String accessToken, final String refreshToken) {
        mAccessToken = accessToken;
        mRefreshToken = refreshToken;
    }

    /* package */ Token(final TokenResponse response) {
        this(response.getAccessToken(), response.getRefreshToken());
    }

    /* package */ Token(final TokenProvider provider, final Account account) {
        this(provider.getAccessToken(account), provider.getRefreshToken(account));
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public boolean isExpired() {
        try {
            final DecodedToken token = getDecodedToken();
            final long timeDifference = getTimeDifference(token.exp);
            Logger.v("Token expires in " + (timeDifference / 60) + " minutes");
            return timeDifference < 30; // expired if valid for less than 30 seconds
        } catch (final Exception e) {
            Logger.ex(e);
            return false;
        }
    }

    private DecodedToken getDecodedToken() throws Exception {
        final String[] parts = getAccessToken().split("\\.");
        final byte[] bytes = Base64.decode(parts[1], Base64.DEFAULT);
        return new ObjectMapper().readValue(bytes, DecodedToken.class);
    }

    private long getTimeDifference(final String expiration) {
        final long expirationTime = Long.parseLong(expiration);
        final long currentTime = System.currentTimeMillis() / 1000;
        final long timeDifference = expirationTime - currentTime;
        return timeDifference;
    }

    private static final class DecodedToken {
        public String exp, iss, jti, iat;
        public String[] aud;
    }
}
