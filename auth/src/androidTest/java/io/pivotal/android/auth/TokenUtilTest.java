/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;
import android.util.Base64;

import java.util.UUID;

public class TokenUtilTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();

    public void testNotExpiredToken() {
        final long expiration = System.currentTimeMillis() / 1000 + 60;
        final String accessToken = getAccessToken(expiration);
        assertFalse(TokenUtil.isExpired(accessToken));
    }

    public void testExpiredTokenWithExpiredTimestamp() {
        final long expiration = System.currentTimeMillis() / 1000 - 1;
        final String accessToken = getAccessToken(expiration);
        assertTrue(TokenUtil.isExpired(accessToken));
    }

    public void testExpiredTokenWithInvalidTokenFormat() {
        assertFalse(TokenUtil.isExpired(ACCESS_TOKEN));
    }

    public void testExpiredTokenThatDoesNotDecode() {
        final long expiration = System.currentTimeMillis() / 1000 + 60;
        final String accessToken = getAccessTokenMissingExpField(expiration);
        assertFalse(TokenUtil.isExpired(accessToken));
    }


    // ====================================


    private String getAccessToken(final long expirationInSeconds) {
        final String expirationComponent = "{ \"exp\": \"" + expirationInSeconds + "\" }";
        return "." + Base64.encodeToString(expirationComponent.getBytes(), Base64.DEFAULT);
    }

    private String getAccessTokenMissingExpField(final long expirationInSeconds) {
        final String expirationComponent = "{ \"not-exp\": \"" + expirationInSeconds + "\" }";
        return "." + Base64.encodeToString(expirationComponent.getBytes(), Base64.DEFAULT);
    }

}
