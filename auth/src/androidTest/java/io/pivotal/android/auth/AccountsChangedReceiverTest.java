/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class AccountsChangedReceiverTest extends AndroidTestCase {

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

    public void testLoggedInNotificationWhenLoggedOut() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final Account[] accounts = new Account[] {account};
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final LoginListener listener = Mockito.mock(LoginListener.class);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(accountsProxy);
        AccountsChangedReceiver.registerLoginListener(context, listener);
        AccountsChangedReceiver.setIsLoggedIn(false);

        final AccountsChangedReceiver receiver = new AccountsChangedReceiver();
        receiver.onReceive(context, null);

        Mockito.verify(listener).onLogin(context);
    }

    public void testLoggedInNotificationWhenLoggedIn() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final Account[] accounts = new Account[] {account};
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final LoginListener listener = Mockito.mock(LoginListener.class);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(accountsProxy);
        AccountsChangedReceiver.registerLoginListener(context, listener);
        AccountsChangedReceiver.setIsLoggedIn(true);

        final AccountsChangedReceiver receiver = new AccountsChangedReceiver();
        receiver.onReceive(context, null);

        Mockito.verify(listener, Mockito.never()).onLogin(context);
    }

    public void testLoggedOutNotificationWhenLoggedOut() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final Account[] accounts = new Account[] {account};
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final LogoutListener listener = Mockito.mock(LogoutListener.class);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(accountsProxy);
        AccountsChangedReceiver.registerLogoutListener(context, listener);
        AccountsChangedReceiver.setIsLoggedIn(false);

        final AccountsChangedReceiver receiver = new AccountsChangedReceiver();
        receiver.onReceive(context, null);

        Mockito.verify(listener, Mockito.never()).onLogout(context);

    }

    public void testLoggedOutNotificationWhenLoggedIn() {
        final Context context = Mockito.mock(Context.class);
        final Account[] accounts = new Account[] { null };
        final AccountsProxy accountsProxy = Mockito.mock(AccountsProxy.class);
        final LogoutListener listener = Mockito.mock(LogoutListener.class);

        Mockito.when(accountsProxy.getAccounts()).thenReturn(accounts);

        AccountsProxyHolder.init(accountsProxy);
        AccountsChangedReceiver.registerLogoutListener(context, listener);
        AccountsChangedReceiver.setIsLoggedIn(true);

        final AccountsChangedReceiver receiver = new AccountsChangedReceiver();
        receiver.onReceive(context, null);

        Mockito.verify(listener).onLogout(context);

    }
}
