/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import java.util.UUID;

public class ResponseTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();


    public void testResponseSuccess() {
        final Response response = new Response(ACCESS_TOKEN, ACCOUNT_NAME);

        assertTrue(response.isSuccess());
        assertFalse(response.isFailure());
    }

    public void testResponseFailure() {
        final AuthError error = new AuthError(new Exception());
        final Response response = new Response(error);

        assertTrue(response.isFailure());
        assertFalse(response.isSuccess());
    }

}
