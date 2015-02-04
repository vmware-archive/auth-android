/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Base64;

import java.util.UUID;

public class ListenerExpirationCallbackTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();
    private static final String ACCOUNT_TYPE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

//    public void testRunInvokesListenerOnCompleteIfTokenNotExpired() throws Exception {
//        final Activity activity = Mockito.mock(Activity.class);
//        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);
//        final SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);
//        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
//        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
//        final ListenerExpirationCallback callback = new ListenerExpirationCallback(activity, listener);
//
//        Mockito.when(future.getResult()).thenReturn(newBundle(ACCESS_TOKEN, ACCOUNT_NAME));
//        Mockito.when(activity.getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE)).thenReturn(preferences);
//        Mockito.when(preferences.edit()).thenReturn(editor);
//        Mockito.when(editor.putString(AuthPreferences.Keys.LAST_USED_ACCOUNT, ACCOUNT_NAME)).thenReturn(editor);
//        Mockito.when(editor.commit()).thenReturn(true);
//
//        callback.run(future);
//
//        Mockito.verify(future).getResult();
//        Mockito.verify(editor).putString(AuthPreferences.Keys.LAST_USED_ACCOUNT, ACCOUNT_NAME);
//        Mockito.verify(listener).onResponse(ACCESS_TOKEN, ACCOUNT_NAME);
//    }
//
//    public void testRunInvokesProviderIfTokenExpired() throws Exception {
//        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
//        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
//        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
//        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
//        final ListenerExpirationCallback callback = new ListenerExpirationCallback(null, listener);
//
//        final String expiredToken = getExpiredToken();
//
//        Mockito.when(future.getResult()).thenReturn(newBundle(expiredToken, ACCOUNT_NAME));
//        Mockito.when(provider.getAccounts()).thenReturn(new Account[] {account});
//        Mockito.doNothing().when(provider).invalidateAccessToken(expiredToken);
//        Mockito.doNothing().when(provider).requestAccessToken(account, null);
//
//        AccountsProxyFactory.init(provider);
//        callback.run(future);
//
//        Mockito.verify(future).getResult();
//        Mockito.verify(provider).getAccounts();
//        Mockito.verify(provider).invalidateAccessToken(expiredToken);
//        Mockito.verify(provider).requestAccessToken(null, account, listener);
//    }
//
//    public void testRunInvokesListenerOnFailureIfExceptionThrown() throws Exception {
//        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
//        final AccountManagerFuture future = Mockito.mock(AccountManagerFuture.class);
//        final ListenerExpirationCallback callback = new ListenerExpirationCallback(null, listener);
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
