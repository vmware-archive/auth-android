/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.TokenResponse;

public class TokenLoaderCallbacksTest extends AndroidTestCase {

    private static final String TEST_EXCEPTION_TEXT = "SOMETHING TERRIBLE HAS HAPPENED";

    public static final class TestTokenLoaderCallbacks extends TokenLoaderCallbacks {

        public TestTokenLoaderCallbacks(Context context, TokenListener listener) {
            super(context, listener);
        }

        @Override
        public Loader<TokenResponse> onCreateLoader(int i, Bundle bundle) {
            return null;
        }
    }


    public void testSuccessfulCallback() {
        final AssertionLatch latch = new AssertionLatch(1);
        final TokenResponse token = new TokenResponse();
        final TestTokenLoaderCallbacks callbacks = new TestTokenLoaderCallbacks(mContext, new TokenListener() {
            @Override
            public void onAuthorizationComplete(Token token) {
                latch.countDown();
            }

            @Override
            public void onAuthorizationFailed(Error error) {
                fail();
            }
        });
        callbacks.onLoadFinished(null, token);
        latch.assertComplete();
    }

    public void testFailedCallback() {
        final AssertionLatch latch = new AssertionLatch(1);
        final TokenResponse token = new TokenLoader.ErrorResponse(new Exception(TEST_EXCEPTION_TEXT));
        final TestTokenLoaderCallbacks callbacks = new TestTokenLoaderCallbacks(mContext, new TokenListener() {
            @Override
            public void onAuthorizationComplete(Token token) {
                fail();
            }

            @Override
            public void onAuthorizationFailed(Error error) {
                assertEquals(TEST_EXCEPTION_TEXT, error.getMessage());
                latch.countDown();
            }
        });
        callbacks.onLoadFinished(null, token);
        latch.assertComplete();
    }
}
