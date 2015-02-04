/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.TokenResponse;

import org.mockito.Mockito;

import java.util.UUID;

public class TokenLoaderCallbacksTest extends AndroidTestCase {

    private static final String MESSAGE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testSuccessfulCallback() {
        final TokenLoader.Listener listener = Mockito.mock(TokenLoader.Listener.class);
        final TokenResponse response = Mockito.spy(new TokenResponse());
        final TestTokenLoaderCallbacks callbacks = new TestTokenLoaderCallbacks(null, listener);

        Mockito.doReturn(false).when(response).containsKey("error");

        callbacks.onLoadFinished(null, response);

        Mockito.verify(response).containsKey("error");
        Mockito.verify(listener).onAuthorizationComplete(Mockito.any(Token.class));
    }

    public void testFailedCallback() {
        final TokenLoader.Listener listener = Mockito.mock(TokenLoader.Listener.class);
        final TokenResponse response = Mockito.spy(new TokenResponse().set("error", MESSAGE));
        final TestTokenLoaderCallbacks callbacks = new TestTokenLoaderCallbacks(null, listener);

        callbacks.onLoadFinished(null, response);

        Mockito.verify(response).containsKey("error");
        Mockito.verify(listener).onAuthorizationFailed(Mockito.any(Error.class));
    }


    // ===================================================


    public static final class TestTokenLoaderCallbacks extends TokenLoaderCallbacks {

        public TestTokenLoaderCallbacks(final Context context, final TokenLoader.Listener listener) {
            super(context, listener);
        }

        @Override
        public Loader<TokenResponse> onCreateLoader(final int id, final Bundle bundle) {
            return null;
        }
    }
}
