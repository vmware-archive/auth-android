/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.io.IOException;
import java.util.UUID;

public class ListenerCallbackTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testRunInvokesListenerOnCompleteIfTokenNotNull() throws Exception {
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
        final ListenerCallback callback = new ListenerCallback(listener);

        Mockito.when(future.getResult()).thenReturn(newBundle(ACCESS_TOKEN, ACCOUNT_NAME));

        callback.run(future);

        Mockito.verify(future).getResult();
        Mockito.verify(listener).onComplete(ACCESS_TOKEN, ACCOUNT_NAME);
    }

    public void testRunInvokesListenerOnFailureIfTokenNull() throws Exception {
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
        final ListenerCallback callback = new ListenerCallback(listener);

        Mockito.when(future.getResult()).thenReturn(newBundle(null, ACCOUNT_NAME));

        callback.run(future);

        Mockito.verify(future).getResult();
        Mockito.verify(listener).onFailure(Mockito.any(Error.class));
    }

    public void testRunInvokesListenerOnFailureIfExceptionThrown() throws Exception {
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
        final ListenerCallback callback = new ListenerCallback(listener);

        Mockito.doThrow(new IOException()).when(future).getResult();

        callback.run(future);

        Mockito.verify(future).getResult();
        Mockito.verify(listener).onFailure(Mockito.any(Error.class));
    }


    // ===================================================


    private Bundle newBundle(final String token, final String account) {
        final Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_AUTHTOKEN, token);
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account);
        return bundle;
    }
}
