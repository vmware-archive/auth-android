/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
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
    }

    public void testGetAccessToken() {
        final Context context = Mockito.mock(Context.class);
        final Response response = Mockito.mock(Response.class);
        final AuthClient client = Mockito.mock(AuthClient.class);

        AuthClientHolder.init(client);

        Mockito.when(client.requestAccessToken(Mockito.any(Context.class))).thenReturn(response);

        assertEquals(response, Auth.getAccessToken(context));

        Mockito.verify(client).requestAccessToken(context);
    }

    public void testGetAccessTokenAsync() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AuthClient client = Mockito.mock(AuthClient.class);

        AuthClientHolder.init(client);

        Auth.getAccessToken(context, listener);

        Mockito.verify(client).requestAccessToken(context, listener);
    }

    public void testInvalidateAccessToken() {
        final Account account = Mockito.mock(Account.class);
        final Account[] accounts = new Account[] { account };
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);

        AccountsProxyHolder.init(proxy);

        Mockito.when(proxy.getAccounts()).thenReturn(accounts);
        Mockito.when(proxy.getAccessToken(Mockito.any(Account.class))).thenReturn(ACCESS_TOKEN);

        Auth.invalidateAccessToken(mContext);

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(proxy).invalidateAccessToken(ACCESS_TOKEN);
    }

    public void testShouldShowUserPrompt() {
        final boolean enabled = new Random().nextBoolean();
        final AuthClient client = Mockito.mock(AuthClient.class);

        AuthClientHolder.init(client);

        Auth.setShouldShowUserPrompt(null, enabled);

        Mockito.verify(client).setShouldShowUserPrompt(enabled);
    }
}
