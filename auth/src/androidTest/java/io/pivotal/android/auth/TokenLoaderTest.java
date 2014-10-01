/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.test.LoaderTestCase;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TokenLoaderTest extends LoaderTestCase {

    private static final TokenResponse TEST_TOKEN_RESPONSE = new TokenResponse();
    private static final String TEST_EXCEPTION_TEXT = "SOMETHING TERRIBLE HAS HAPPENED";
    private static final Exception TEST_EXCEPTION = new Exception(TEST_EXCEPTION_TEXT);
    private static final Exception TEST_EMPTY_EXCEPTION = new Exception();

    public void testTokenLoader() {
        final TestTokenLoader loader = new TestTokenLoader(getContext());
        final TokenResponse response = getLoaderResultSynchronously(loader);
        assertTrue(TEST_TOKEN_RESPONSE == response);
    }

    public void testErrorResponse() {
        final TokenLoader.ErrorResponse response = new TokenLoader.ErrorResponse(TEST_EXCEPTION);
        assertEquals(TEST_EXCEPTION_TEXT, response.get("error"));
    }

    public void testEmptyErrorResponse() {
        final TokenLoader.ErrorResponse response = new TokenLoader.ErrorResponse(TEST_EMPTY_EXCEPTION);
        assertEquals("Unknown error.", response.get("error"));
    }

    public static final class TestTokenLoader extends TokenLoader {

        public TestTokenLoader(Context context) {
            super(context);
        }

        @Override
        public TokenResponse loadInBackground() {
            return TEST_TOKEN_RESPONSE;
        }
    }
}
