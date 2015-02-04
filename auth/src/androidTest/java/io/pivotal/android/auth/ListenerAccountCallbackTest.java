/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Base64;

import java.util.UUID;

public class ListenerAccountCallbackTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

//    public void testRunInvokesListenerOnCompleteIfTokenNotExpired() throws Exception {
//        final Context context = Mockito.mock(Context.class);
//        final Account account = Mockito.mock(Account.class);
//        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
//        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
//        final ListenerAccountCallback callback = new ListenerAccountCallback(context, account, listener);
//
//        Mockito.when(future.getResult()).thenReturn(newBundle(ACCESS_TOKEN, ACCOUNT_NAME));
//
//        callback.run(future);
//
//        Mockito.verify(future).getResult();
//        Mockito.verify(listener).onResponse(ACCESS_TOKEN, ACCOUNT_NAME);
//    }
//
//    public void testRunInvokesProviderIfTokenExpired() throws Exception {
//        final Context context = Mockito.mock(Context.class);
//        final Account account = Mockito.mock(Account.class);
//        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
//        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
//        final ListenerAccountCallback callback = new ListenerAccountCallback(context, account, null);
//
//        final String expiredToken = getExpiredToken();
//
//        Mockito.when(future.getResult()).thenReturn(newBundle(expiredToken, ACCOUNT_NAME));
//        Mockito.doNothing().when(provider).invalidateAccessToken(expiredToken);
//        Mockito.doNothing().when(provider).requestAccessToken(account, null);
//
//        AccountsProxyFactory.init(provider);
//        callback.run(future);
//
//        Mockito.verify(future).getResult();
//        Mockito.verify(provider).invalidateAccessToken(expiredToken);
//        Mockito.verify(provider).requestAccessToken(account, null);
//    }
//
//    public void testRunInvokesListenerOnFailureIfExceptionThrown() throws Exception {
//        final Context context = Mockito.mock(Context.class);
//        final Account account = Mockito.mock(Account.class);
//        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
//        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
//        final ListenerAccountCallback callback = new ListenerAccountCallback(context, account, listener);
//
//        Mockito.doThrow(new IOException()).when(future).getResult();
//
//        callback.run(future);
//
//        Mockito.verify(future).getResult();
//        Mockito.verify(listener).onFailure(Mockito.any(Error.class));
//    }


    // ===================================================


    private Bundle newBundle(final String token, final String account) {
        final Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_AUTHTOKEN, token);
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account);
        return bundle;
    }

    private String getExpiredToken() {
        final long timeInPast = System.currentTimeMillis() / 1000 - 60;
        final String expiration = "{ \"exp\": \"" + timeInPast + "\" }";
        return "." + Base64.encodeToString(expiration.getBytes(), Base64.DEFAULT);
    }
}
