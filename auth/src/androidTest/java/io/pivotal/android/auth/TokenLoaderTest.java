/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.test.LoaderTestCase;

import com.google.api.client.auth.oauth2.TokenResponse;

import org.mockito.Mockito;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TokenLoaderTest extends LoaderTestCase {

    private static final String MESSAGE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testOnStartLoadingInvokesForceLoad() {
        final Context context = Mockito.mock(Context.class);
        final TokenLoader loader = Mockito.spy(new TestTokenLoader(context));

        Mockito.doNothing().when(loader).forceLoad();

        loader.onStartLoading();

        Mockito.verify(loader).forceLoad();
    }

    public void testErrorResponsePutsError() {
        final Exception exception = Mockito.mock(Exception.class);

        Mockito.when(exception.getLocalizedMessage()).thenReturn(MESSAGE);

        final TokenLoader.ErrorResponse response = new TokenLoader.ErrorResponse(exception);
        assertEquals(MESSAGE, response.get("error"));

        Mockito.verify(exception).getLocalizedMessage();
    }

    public void testErrorResponsePutsUnknownErrorMessage() {
        final Exception exception = Mockito.mock(Exception.class);

        Mockito.when(exception.getLocalizedMessage()).thenReturn(null);

        final TokenLoader.ErrorResponse response = new TokenLoader.ErrorResponse(exception);
        assertEquals("Unknown error.", response.get("error"));

        Mockito.verify(exception).getLocalizedMessage();
    }


    // ==========================================


    public static class TestTokenLoader extends TokenLoader {

        public TestTokenLoader(final Context context) {
            super(context);
        }

        @Override
        public TokenResponse loadInBackground() {
            return null;
        }
    }
}
