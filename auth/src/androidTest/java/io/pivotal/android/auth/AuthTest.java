/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.Random;
import java.util.UUID;

public class AuthTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        AuthClientHolder.init(null);
        AccountsProxyHolder.init(null);
        AccountsChangedListener.sAccountsChangedListener = null;
    }

    public void testGetAccessTokenWithLoggedInUser() {
        final Context context = Mockito.mock(Context.class);
        final Response response = Mockito.mock(Response.class);
        final AuthClient client = Mockito.mock(AuthClient.class);
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);

        AuthClientHolder.init(client);
        AccountsProxyHolder.init(accountsProxy);

        Mockito.when(client.requestAccessToken(Mockito.any(Context.class))).thenReturn(response);
        Mockito.when(accountsProxy.getAccounts()).thenReturn(new Account[]{new Account("hi", "bye")});

        assertEquals(response, Auth.getAccessToken(context));

        Mockito.verify(client).requestAccessToken(context);
        Mockito.verify(accountsProxy).removeOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
        Mockito.verify(accountsProxy, Mockito.never()).addOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
    }

    public void testGetAccessTokenWithNoLoggedInUser() {
        final Context context = Mockito.mock(Context.class);
        final Response response = Mockito.mock(Response.class);
        final AuthClient client = Mockito.mock(AuthClient.class);
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);

        AuthClientHolder.init(client);
        AccountsProxyHolder.init(accountsProxy);

        Mockito.when(client.requestAccessToken(Mockito.any(Context.class))).thenReturn(response);
        Mockito.when(accountsProxy.getAccounts()).thenReturn(new Account[]{});
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(preferences);

        assertEquals(response, Auth.getAccessToken(context));

        Mockito.verify(client).requestAccessToken(context);
        Mockito.verify(accountsProxy).removeOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
        Mockito.verify(accountsProxy).addOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
    }

    public void testGetAccessTokenAsyncWithLoggedInUser() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AuthClient client = Mockito.mock(AuthClient.class);
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);

        AuthClientHolder.init(client);
        AccountsProxyHolder.init(accountsProxy);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(new Account[]{new Account("hi", "bye")});

        Auth.getAccessToken(context, listener);

        Mockito.verify(client).requestAccessToken(context, listener);
        Mockito.verify(accountsProxy).removeOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
        Mockito.verify(accountsProxy, Mockito.never()).addOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
    }

    public void testGetAccessTokenAsyncWithNoLoggedInUser() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AuthClient client = Mockito.mock(AuthClient.class);
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);

        AuthClientHolder.init(client);
        AccountsProxyHolder.init(accountsProxy);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(new Account[]{});
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(preferences);

        Auth.getAccessToken(context, listener);

        Mockito.verify(client).requestAccessToken(context, listener);
        Mockito.verify(accountsProxy).removeOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
        Mockito.verify(accountsProxy).addOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
    }

    public void testInvalidateAccessToken() {
        final Account account = Mockito.mock(Account.class);
        final Account[] accounts = new Account[] { account };
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);

        AccountsProxyHolder.init(accountsProxy);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(accounts);
        Mockito.when(accountsProxy.getAccessToken(Mockito.any(Account.class))).thenReturn(ACCESS_TOKEN);

        Auth.invalidateAccessToken(mContext);

        Mockito.verify(accountsProxy).getAccessToken(account);
        Mockito.verify(accountsProxy).invalidateAccessToken(ACCESS_TOKEN);
    }

    public void testLogoutWithLoggedInUser() {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);

        AccountsProxyHolder.init(accountsProxy);

        Account account = new Account("hi", "bye");
        Mockito.when(accountsProxy.getAccounts()).thenReturn(new Account[]{ account });

        Auth.logout(context);

        Mockito.verify(accountsProxy).removeAccount(account);
        Mockito.verify(accountsProxy).removeOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
        Mockito.verify(accountsProxy).addOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
    }

    public void testLogoutWithNoLoggedInUser() {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);

        AccountsProxyHolder.init(accountsProxy);
        Mockito.when(accountsProxy.getAccounts()).thenReturn(new Account[]{});
        Mockito.when(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(preferences);

        Auth.logout(context);

        Mockito.verify(accountsProxy, Mockito.never()).removeAccount(Mockito.any(Account.class));
        Mockito.verify(accountsProxy).removeOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
        Mockito.verify(accountsProxy, Mockito.never()).addOnAccountsUpdatedListener(Mockito.any(AccountsChangedListener.class));
    }

    public void testShouldShowUserPrompt() {
        final boolean enabled = new Random().nextBoolean();
        final AuthClient client = Mockito.mock(AuthClient.class);

        AuthClientHolder.init(client);

        Auth.setShouldShowUserPrompt(null, enabled);

        Mockito.verify(client).setShouldShowUserPrompt(enabled);
    }
}
