/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;
import android.util.Base64;

import java.util.UUID;

public class TokenTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();


    public void testSavesAccessToken() {
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);
        assertEquals(ACCESS_TOKEN, token.getAccessToken());
    }

    public void testSavesRefreshToken() {
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);
        assertEquals(REFRESH_TOKEN, token.getRefreshToken());
    }

    public void testNotExpiredToken() {
        final long expiration = System.currentTimeMillis() / 1000 + 60;
        final String accessToken = getAccessToken(expiration);
        final Token token = new Token(accessToken, REFRESH_TOKEN);
        assertFalse(token.isExpired());
    }

    public void testExpiredTokenWithExpiredTimestamp() {
        final long expiration = System.currentTimeMillis() / 1000 - 1;
        final String accessToken = getAccessToken(expiration);
        final Token token = new Token(accessToken, REFRESH_TOKEN);
        assertTrue(token.isExpired());
    }

    public void testExpiredTokenWithInvalidTokenFormat() {
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);
        assertFalse(token.isExpired());
    }

    public void testExpiredTokenThatDoesNotDecode() {
        final long expiration = System.currentTimeMillis() / 1000 + 60;
        final String accessToken = getAccessTokenMissingExpField(expiration);
        final Token token = new Token(accessToken, REFRESH_TOKEN);
        assertFalse(token.isExpired());
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
