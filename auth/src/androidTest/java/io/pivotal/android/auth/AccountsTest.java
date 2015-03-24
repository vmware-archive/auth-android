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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        AccountsProxyHolder.init(null);
    }

    public void testAddAccountInvokesProviderWhenNoExistingAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);

        Mockito.doReturn(new Account[] { null }).when(provider).getAccounts();
        Mockito.doNothing().when(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.doNothing().when(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));

        AccountsProxyHolder.init(provider);
        Accounts.addAccount(context, ACCOUNT_NAME, token);

        Mockito.verify(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.verify(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));
    }

    public void testAddAccountInvokesProviderWhenExistingAccountWithSameName() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);

        Mockito.doReturn(new Account[] {account}).when(provider).getAccounts();
        Mockito.doNothing().when(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.doNothing().when(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));

        AccountsProxyHolder.init(provider);
        Accounts.addAccount(context, ACCOUNT_NAME, token);

        Mockito.verify(provider).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.verify(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));
    }

    public void testAddAccountInvokesProviderWhenExistingAccountWithDifferentName() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final Account account = new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);

        Mockito.doReturn(new Account[] {account}).when(provider).getAccounts();

        AccountsProxyHolder.init(provider);
        Accounts.addAccount(context, ACCOUNT_NAME, token);

        Mockito.verify(provider, Mockito.never()).addAccount(Mockito.any(Account.class), Mockito.eq(token.getRefreshToken()));
        Mockito.verify(provider, Mockito.never()).setAccessToken(Mockito.any(Account.class), Mockito.eq(token.getAccessToken()));
    }

    public void testRemoveAccountInvokesProvider() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);

        final Account[] accounts = new Account[] {
            new Account(UUID.randomUUID().toString(), ACCOUNT_TYPE)
        };

        Mockito.doReturn(accounts).when(provider).getAccounts();
        Mockito.doNothing().when(provider).removeAccount(Mockito.any(Account.class));

        AccountsProxyHolder.init(provider);
        Accounts.removeAccount(context);

        Mockito.verify(provider).removeAccount(Mockito.any(Account.class));
    }

    public void testGetAccountWithSingleAccount() {
        final Context context = Mockito.mock(Context.class);
        final Account account = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        final Account[] accounts = new Account[] { account };
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);

        AccountsProxyHolder.init(provider);

        Mockito.when(provider.getAccounts()).thenReturn(accounts);

        assertEquals(account, Accounts.getAccount(context));

        Mockito.verify(provider).getAccounts();
    }
}
