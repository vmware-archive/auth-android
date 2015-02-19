/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.UUID;

public class AccountsTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();

    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();
    private static final String ACCOUNT_TYPE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testAddAccountInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);

        Mockito.doNothing().when(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.doNothing().when(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));

        AccountsProxyHolder.init(provider);
        Accounts.addAccount(context, ACCOUNT_NAME, token);

        Mockito.verify(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.verify(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));
    }

    public void testGetAccountsInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Account[] accounts = new Account[0];

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(provider);
        assertEquals(accounts, Accounts.getAccounts(context));

        Mockito.verify(provider).getAccounts();
    }

    public void testGetAccountWithNullNameReturnsFirstAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account account2 = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account, account2 };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(provider);
        assertEquals(account, Accounts.getAccount(context, null));

        Mockito.verify(provider).getAccounts();
    }

    public void testGetAccountWithNameReturnsCorrectAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Account account = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account account2 = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account, account2 };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(provider);
        assertEquals(account2, Accounts.getAccount(context, ACCOUNT_NAME));

        Mockito.verify(provider).getAccounts();
    }

    public void testGetAccountWithNameReturnsNullIfNotFound() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Account account = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account account2 = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account, account2 };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(provider);
        assertNull(Accounts.getAccount(context, ACCOUNT_NAME));

        Mockito.verify(provider).getAccounts();
    }

    public void testRemoveAccountInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);

        Mockito.doNothing().when(provider).removeAccount(Mockito.any(Account.class));

        AccountsProxyHolder.init(provider);
        Accounts.removeAccount(context, ACCOUNT_NAME);

        Mockito.verify(provider).removeAccount(Mockito.any(Account.class));
    }

    public void testRemoveAllAccountsInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);

        final Account[] accounts = new Account[] {
            new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE),
            new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE)
        };

        Mockito.when(provider.getAccounts()).thenReturn(accounts);
        Mockito.doNothing().when(provider).removeAccount(Mockito.any(Account.class));

        AccountsProxyHolder.init(provider);
        Accounts.removeAllAccounts(context);

        Mockito.verify(provider).getAccounts();
        Mockito.verify(provider, Mockito.times(accounts.length)).removeAccount(Mockito.any(Account.class));
    }

    public void testGetLastUsedAccountWithSingleAccount() {
        final Context context = Mockito.mock(Context.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account };
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);

        AccountsProxyHolder.init(provider);

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        assertEquals(account, Accounts.getLastUsedAccount(context));

        Mockito.verify(provider).getAccounts();
    }
}
