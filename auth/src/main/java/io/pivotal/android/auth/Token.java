package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.TokenResponse;

public class Token {

    private final String mAccessToken;
    private final String mRefreshToken;

    private Token(final String accessToken, final String refreshToken) {
        mAccessToken = accessToken;
        mRefreshToken = refreshToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    /* package */ static final class New extends Token {

        public New(final TokenResponse response) {
            super(response.getAccessToken(), response.getRefreshToken());
        }
    }

    /* package */ static final class Existing extends Token {

        public Existing(final AccountManager manager, final Account account) {
            super(manager.peekAuthToken(account, Pivotal.Property.TOKEN_TYPE), manager.getPassword(account));
        }

        private static final class DecodedToken {
            public String exp, iss, jti, iat;
            public String[] aud;
        }

        public boolean isExpired() {
            try {
                final String[] parts = getAccessToken().split("\\.");
                final byte[] bytes = Base64.decode(parts[1], Base64.DEFAULT);
                final DecodedToken token = new ObjectMapper().readValue(bytes, DecodedToken.class);

                final long expirationTime = Long.parseLong(token.exp);
                final long currentTime = System.currentTimeMillis() / 1000;

                final long timeDifference = expirationTime - currentTime;

                Logger.v("isExpired expirationTime: " + expirationTime + ", current: " + currentTime + ", expires in " + timeDifference / 60 + " minutes");

                return timeDifference < 30; // expired if valid for less than 30 seconds
            } catch (final Exception e) {
                Logger.ex(e);
                return true;
            }
        }
    }
}
