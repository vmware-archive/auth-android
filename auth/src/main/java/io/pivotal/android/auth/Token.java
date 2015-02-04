/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

public class Token {

    private final String mAccessToken;
    private final String mRefreshToken;

    public Token(final String accessToken, final String refreshToken) {
        mAccessToken = accessToken;
        mRefreshToken = refreshToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public boolean isExpired() {
        return TokenUtil.isExpired(mAccessToken);
    }
}
