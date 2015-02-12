/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.UUID;

public class AuthTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();
    private static final String ACCOUNT_TYPE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }


    public void testGetAccessToken() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AuthClient client = Mockito.mock(AuthClient.class);

        AuthClientFactory.init(client);

        Mockito.when(client.requestAccessToken(Mockito.any(Context.class))).thenReturn(response);

        assertEquals(response, Auth.getAccessToken(context));

        Mockito.verify(client).requestAccessToken(context);
    }

    public void testGetAccessTokenAsync() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AuthClient client = Mockito.mock(AuthClient.class);

        AuthClientFactory.init(client);

        Auth.getAccessToken(context, listener);

        Mockito.verify(client).requestAccessToken(context, listener);
    }

    public void testGetAccessTokenWithAccountName() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient client = Mockito.mock(AuthClient.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account };

        AuthClientFactory.init(client);
        AccountsProxyFactory.init(proxy);

        Mockito.when(proxy.getAccounts()).thenReturn(accounts);
        Mockito.when(client.requestAccessToken(Mockito.any(Account.class))).thenReturn(response);

        assertEquals(response, Auth.getAccessToken(context, ACCOUNT_NAME));

        Mockito.verify(client).requestAccessToken(account);
    }

    public void testGetAccessTokenWithAccountNameAsync() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient client = Mockito.mock(AuthClient.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account };

        AuthClientFactory.init(client);
        AccountsProxyFactory.init(proxy);

        Mockito.when(proxy.getAccounts()).thenReturn(accounts);

        Auth.getAccessToken(context, ACCOUNT_NAME, listener);

        Mockito.verify(client).requestAccessToken(account, listener);
    }

    public void testInvalidateAccessToken() {
        final Account account = Mockito.mock(Account.class);
        final Account[] accounts = new Account[] { account };
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);

        AccountsProxyFactory.init(proxy);

        Mockito.when(proxy.getAccounts()).thenReturn(accounts);
        Mockito.when(proxy.getAccessToken(Mockito.any(Account.class))).thenReturn(ACCESS_TOKEN);

        Auth.invalidateAccessToken(null);

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(proxy).invalidateAccessToken(ACCESS_TOKEN);
    }

    public void testInvalidateAccessTokenWithAccountName() {
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account };
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);

        AccountsProxyFactory.init(proxy);

        Mockito.when(proxy.getAccounts()).thenReturn(accounts);
        Mockito.when(proxy.getAccessToken(Mockito.any(Account.class))).thenReturn(ACCESS_TOKEN);

        Auth.invalidateAccessToken(null, ACCOUNT_NAME);

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(proxy).invalidateAccessToken(ACCESS_TOKEN);
    }

    public void testResponseSuccess() {
        final Auth.Response response = new Auth.Response(ACCESS_TOKEN, ACCOUNT_NAME);

        assertTrue(response.isSuccess());
        assertFalse(response.isFailure());
    }

    public void testResponseFailure() {
        final AuthError error = new AuthError(new Exception());
        final Auth.Response response = new Auth.Response(error);

        assertTrue(response.isFailure());
        assertFalse(response.isSuccess());
    }

}
