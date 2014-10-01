/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.test.AndroidTestCase;
import android.util.Base64;

import com.google.api.client.auth.oauth2.TokenResponse;

public class TokenTest extends AndroidTestCase {

    private static final String TEST_ACCESS_TOKEN = "TEST ACCESS TOKEN";
    private static final String TEST_REFRESH_TOKEN = "TEST REFRESH TOKEN";
    private static final String TEST_ACCOUNT_NAME = "TEST ACCOUNT NAME";
    private static final String TEST_ACCOUNT_TYPE = "TEST ACCOUNT TYPE";


    public void testSavesAccessToken() {
        final Token token = new Token(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);
        assertEquals(TEST_ACCESS_TOKEN, token.getAccessToken());
    }

    public void testSavesRefreshToken() {
        final Token token = new Token(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);
        assertEquals(TEST_REFRESH_TOKEN, token.getRefreshToken());
    }

    public void testConstructsFromTokenResponse() {
        final TokenResponse response = new TokenResponse();
        response.setAccessToken(TEST_ACCESS_TOKEN);
        response.setRefreshToken(TEST_REFRESH_TOKEN);

        final Token token = new Token(response);
        assertEquals(TEST_ACCESS_TOKEN, token.getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, token.getRefreshToken());
    }

    public void testConstructsFromTokenProviderAndAccount() {
        final TokenProvider provider = new MockTokenProvider() {

            @Override
            public String getAuthToken(Account account) {
                return TEST_ACCESS_TOKEN;
            }

            @Override
            public String getRefreshToken(Account account) {
                return TEST_REFRESH_TOKEN;
            }
        };

        final Account account = new Account(TEST_ACCOUNT_NAME, TEST_ACCOUNT_TYPE);

        final Token token = new Token(provider, account);
        assertEquals(TEST_ACCESS_TOKEN, token.getAccessToken());
        assertEquals(TEST_REFRESH_TOKEN, token.getRefreshToken());
    }

    public void testNotExpiredToken() {
        final long expiration = System.currentTimeMillis() / 1000 + 60;
        final String accessToken = getAccessToken(expiration);
        final Token token = new Token(accessToken, TEST_REFRESH_TOKEN);
        assertFalse(token.isExpired());
    }

    public void testExpiredTokenWithExpiredTimestamp() {
        final long expiration = System.currentTimeMillis() / 1000 - 1;
        final String accessToken = getAccessToken(expiration);
        final Token token = new Token(accessToken, TEST_REFRESH_TOKEN);
        assertTrue(token.isExpired());
    }

    public void testExpiredTokenWithInvalidTokenFormat() {
        final Token token = new Token(TEST_ACCESS_TOKEN, TEST_REFRESH_TOKEN);
        assertTrue(token.isExpired());
    }

    public void testExpiredTokenThatDoesNotDecode() {
        final long expiration = System.currentTimeMillis() / 1000 + 60;
        final String accessToken = getAccessTokenMissingExpField(expiration);
        final Token token = new Token(accessToken, TEST_REFRESH_TOKEN);
        assertTrue(token.isExpired());
    }

    private String getAccessToken(final long expirationInSeconds) {
        final String expirationComponent = "{ \"exp\": \"" + expirationInSeconds + "\" }";
        return "." + Base64.encodeToString(expirationComponent.getBytes(), Base64.DEFAULT);
    }

    private String getAccessTokenMissingExpField(final long expirationInSeconds) {
        final String expirationComponent = "{ \"not-exp\": \"" + expirationInSeconds + "\" }";
        return "." + Base64.encodeToString(expirationComponent.getBytes(), Base64.DEFAULT);
    }

}
