/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.UUID;

public class AuthTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();

    private static final String ACCOUNT_TYPE = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testGetAccessTokenInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);

        Mockito.when(provider.getAccounts()).thenReturn(new Account[]{account});
        Mockito.when(provider.getAccessTokenOrThrow(account)).thenReturn(ACCESS_TOKEN);

        TokenProviderFactory.init(provider);
        assertEquals(ACCESS_TOKEN, Auth.getAccessToken(context, ACCOUNT_NAME));

        Mockito.verify(provider).getAccounts();
        Mockito.verify(provider).getAccessTokenOrThrow(account);
    }

    public void testAsyncGetAccessTokenInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Activity activity = Mockito.mock(Activity.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);

        Mockito.doNothing().when(provider).getAccessToken(activity, listener);

        TokenProviderFactory.init(provider);
        Auth.getAccessToken(activity, listener);

        Mockito.verify(provider).getAccessToken(activity, listener);
    }

    public void testAsyncGetAccessTokenInvokesProviderForSingleAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);

        Mockito.when(provider.getAccounts()).thenReturn(new Account[]{account});
        Mockito.doNothing().when(provider).getAccessToken(context, account, false, listener);

        TokenProviderFactory.init(provider);
        Auth.getAccessToken(context, false, listener);

        Mockito.verify(provider).getAccounts();
        Mockito.verify(provider).getAccessToken(context, account, false, listener);
    }

    public void testAsyncGetAccessTokenInvokesProviderForLastUsedAccountName() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account account2 = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);

        Mockito.when(provider.getAccounts()).thenReturn(new Account[]{account, account2});
        Mockito.when(context.getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE)).thenReturn(preferences);
        Mockito.when(preferences.getString(AuthPreferences.Keys.LAST_USED_ACCOUNT, "")).thenReturn(ACCOUNT_NAME);
        Mockito.doNothing().when(provider).getAccessToken(context, account, false, listener);

        TokenProviderFactory.init(provider);
        Auth.getAccessToken(context, false, listener);

        Mockito.verify(provider, Mockito.times(2)).getAccounts();
        Mockito.verify(provider).getAccessToken(context, account, false, listener);
    }

    public void testAsyncGetAccessTokenInvokesListenerForNoAccounts() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);

        Mockito.when(provider.getAccounts()).thenReturn(new Account[0]);
        Mockito.when(context.getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE)).thenReturn(preferences);
        Mockito.when(preferences.getString(AuthPreferences.Keys.LAST_USED_ACCOUNT, "")).thenReturn(ACCOUNT_NAME);

        TokenProviderFactory.init(provider);
        Auth.getAccessToken(context, false, listener);

        Mockito.verify(provider, Mockito.times(2)).getAccounts();
        Mockito.verify(listener).onFailure(Mockito.any(Error.class));
    }

    public void testInvalidateAccessTokenInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);

        Mockito.doNothing().when(provider).invalidateAccessToken(ACCESS_TOKEN);

        TokenProviderFactory.init(provider);
        Auth.invalidateAccessToken(context, ACCESS_TOKEN);

        Mockito.verify(provider).invalidateAccessToken(ACCESS_TOKEN);
    }

    public void testAddAccountInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);

        Mockito.doNothing().when(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.doNothing().when(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));

        TokenProviderFactory.init(provider);
        Auth.addAccount(context, ACCOUNT_NAME, token);

        Mockito.verify(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.verify(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));
    }

    public void testGetAccountsInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Account[] accounts = new Account[0];

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        TokenProviderFactory.init(provider);
        assertEquals(accounts, Auth.getAccounts(context));

        Mockito.verify(provider).getAccounts();
    }

    public void testGetAccountWithNullNameReturnsFirstAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account account2 = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account, account2 };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        TokenProviderFactory.init(provider);
        assertEquals(account, Auth.getAccount(context, null));

        Mockito.verify(provider).getAccounts();
    }

    public void testGetAccountWithNameReturnsCorrectAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Account account = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account account2 = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account, account2 };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        TokenProviderFactory.init(provider);
        assertEquals(account2, Auth.getAccount(context, ACCOUNT_NAME));

        Mockito.verify(provider).getAccounts();
    }

    public void testGetAccountWithNameReturnsNullIfNotFound() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Account account = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account account2 = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account, account2 };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        TokenProviderFactory.init(provider);
        assertNull(Auth.getAccount(context, ACCOUNT_NAME));

        Mockito.verify(provider).getAccounts();
    }

    public void testRemoveAccountInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);

        Mockito.doNothing().when(provider).removeAccount(Mockito.any(Account.class));

        TokenProviderFactory.init(provider);
        Auth.removeAccount(context, ACCOUNT_NAME);

        Mockito.verify(provider).removeAccount(Mockito.any(Account.class));
    }

    public void testRemoveAllAccountsInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);

        final Account[] accounts = new Account[] {
            new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE),
            new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE)
        };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);
        Mockito.doNothing().when(provider).removeAccount(Mockito.any(Account.class));

        TokenProviderFactory.init(provider);
        Auth.removeAllAccounts(context);

        Mockito.verify(provider).getAccounts();
        Mockito.verify(provider, Mockito.times(accounts.length)).removeAccount(Mockito.any(Account.class));
    }
}
