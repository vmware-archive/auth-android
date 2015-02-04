/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import java.util.UUID;

public class TokenTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();


    public void testConstructor() {
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);
        assertEquals(ACCESS_TOKEN, token.getAccessToken());
        assertEquals(REFRESH_TOKEN, token.getRefreshToken());
    }
}
