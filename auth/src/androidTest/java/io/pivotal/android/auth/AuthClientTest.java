/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.UUID;

public class AuthClientTest extends AndroidTestCase {

    protected abstract class AccountManagerFutureBundle implements AccountManagerFuture<Bundle> {}
    protected abstract class AccountManagerCallbackBundle implements AccountManagerCallback<Bundle> {}

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testRequestAccessTokenWithActivity() {
        final Activity activity = Mockito.mock(Activity.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthTokenByFeatures(Mockito.any(Activity.class), Mockito.any(AccountManagerCallbackBundle.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).handleFuture(Mockito.any(Activity.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(activity));

        Mockito.verify(proxy).getAuthTokenByFeatures(activity, null);
        Mockito.verify(client).handleFuture(activity, future);
    }

    public void testRequestAccessTokenWithActivityAndListener() {
        final Activity activity = Mockito.mock(Activity.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.doReturn(response).when(client).handleFuture(Mockito.any(Context.class), Mockito.any(AccountManagerFutureBundle.class));
        Mockito.when(proxy.getAuthTokenByFeatures(Mockito.any(Activity.class), Mockito.any(AccountManagerCallbackBundle.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final AccountManagerCallback callback = (AccountManagerCallback) invocationOnMock.getArguments()[1];
                callback.run(future);
                return null;
            }
        });

        client.requestAccessToken(activity, listener);

        Mockito.verify(proxy).getAuthTokenByFeatures(Mockito.eq(activity), Mockito.any(AccountManagerCallbackBundle.class));
        Mockito.verify(client).handleFuture(activity, future);
        Mockito.verify(listener).onResponse(response);
    }

    public void testRequestAccessTokenWithContext() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Account.class), Mockito.any(AccountManagerCallbackBundle.class))).thenReturn(future);
        Mockito.doReturn(account).when(client).getLastUsedAccount(context);
        Mockito.doReturn(response).when(client).handleFuture(Mockito.any(Activity.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(context));

        Mockito.verify(client).getLastUsedAccount(context);
        Mockito.verify(proxy).getAuthToken(account, null);
        Mockito.verify(client).handleFuture(context, future);
    }

    public void testRequestAccessTokenWithContextNoLastUsedAccount() {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.doReturn(null).when(client).getLastUsedAccount(context);
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(Exception.class));

        assertEquals(response, client.requestAccessToken(context));

        Mockito.verify(client).getLastUsedAccount(context);
        Mockito.verify(client).getFailureAuthResponse(Mockito.any(Exception.class));
    }

    public void testRequestAccessTokenWithContextAndListener() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.doReturn(account).when(client).getLastUsedAccount(context);
        Mockito.doReturn(response).when(client).handleFuture(Mockito.any(Context.class), Mockito.any(AccountManagerFutureBundle.class));
        Mockito.when(proxy.getAuthToken(Mockito.eq(account), Mockito.any(AccountManagerCallbackBundle.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final AccountManagerCallback callback = (AccountManagerCallback) invocationOnMock.getArguments()[1];
                callback.run(future);
                return null;
            }
        });

        client.requestAccessToken(context, listener);

        Mockito.verify(client).getLastUsedAccount(context);
        Mockito.verify(proxy).getAuthToken(Mockito.eq(account), Mockito.any(AccountManagerCallbackBundle.class));
        Mockito.verify(client).handleFuture(context, future);
        Mockito.verify(listener).onResponse(response);
    }

    public void testRequestAccessTokenWithContextAndListenerNoLastUsedAccount() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.doReturn(null).when(client).getLastUsedAccount(context);
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(Exception.class));

        client.requestAccessToken(context, listener);

        Mockito.verify(client).getLastUsedAccount(context);
        Mockito.verify(client).getFailureAuthResponse(Mockito.any(Exception.class));
        Mockito.verify(listener).onResponse(response);
    }

    public void testRequestAccessTokenWithAccount() {
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Account.class), Mockito.any(AccountManagerCallbackBundle.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).validateAccountBundle(Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(account));

        Mockito.verify(proxy).getAuthToken(account, null);
        Mockito.verify(client).validateAccountBundle(future);
    }

    public void testRequestAccessTokenWithAccountAndListener() {
        final Account account = Mockito.mock(Account.class);
        final Auth.Listener listener = Mockito.mock(Auth.Listener.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.doReturn(response).when(client).validateAccountBundle(Mockito.any(AccountManagerFutureBundle.class));
        Mockito.when(proxy.getAuthToken(Mockito.eq(account), Mockito.any(AccountManagerCallbackBundle.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final AccountManagerCallback callback = (AccountManagerCallback) invocationOnMock.getArguments()[1];
                callback.run(future);
                return null;
            }
        });

        client.requestAccessToken(account, listener);

        Mockito.verify(proxy).getAuthToken(Mockito.eq(account), Mockito.any(AccountManagerCallbackBundle.class));
        Mockito.verify(client).validateAccountBundle(future);
        Mockito.verify(listener).onResponse(response);
    }

    public void testHandleFutureWithSuccessAndTokenExpired() {
        final Context context = Mockito.mock(Context.class);
        final Auth.Response response = Mockito.spy(new Auth.Response(ACCESS_TOKEN, ACCOUNT_NAME));
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));
        final Account account = Mockito.mock(Account.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);

        Mockito.when(response.isSuccess()).thenReturn(true);
        Mockito.when(response.isTokenExpired()).thenReturn(true);
        Mockito.doReturn(response).when(client).validateAccountBundle(Mockito.any(AccountManagerFutureBundle.class));
        Mockito.doNothing().when(client).setLastUsedAccountName(Mockito.any(Context.class), Mockito.anyString());
        Mockito.doReturn(account).when(client).getAccount(Mockito.any(Context.class), Mockito.anyString());
        Mockito.doReturn(response).when(client).requestAccessToken(Mockito.any(Account.class));

        assertEquals(response, client.handleFuture(context, future));

        Mockito.verify(client).validateAccountBundle(future);
        Mockito.verify(client).setLastUsedAccountName(context, ACCOUNT_NAME);
        Mockito.verify(proxy).invalidateAccessToken(ACCESS_TOKEN);
        Mockito.verify(client).getAccount(context, ACCOUNT_NAME);
        Mockito.verify(client).requestAccessToken(account);
    }

    public void testHandleFutureWithSuccessAndTokenNotExpired() {
        final Context context = Mockito.mock(Context.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final Auth.Response response = Mockito.spy(new Auth.Response(ACCESS_TOKEN, ACCOUNT_NAME));
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(response.isSuccess()).thenReturn(true);
        Mockito.when(response.isTokenExpired()).thenReturn(false);
        Mockito.doReturn(response).when(client).validateAccountBundle(Mockito.any(AccountManagerFutureBundle.class));
        Mockito.doNothing().when(client).setLastUsedAccountName(Mockito.any(Context.class), Mockito.anyString());

        assertEquals(response, client.handleFuture(context, future));

        Mockito.verify(client).validateAccountBundle(future);
        Mockito.verify(client).setLastUsedAccountName(context, ACCOUNT_NAME);
    }

    public void testHandleFutureWithFailure() {
        final Context context = Mockito.mock(Context.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(response.isSuccess()).thenReturn(false);
        Mockito.doReturn(response).when(client).validateAccountBundle(Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.handleFuture(context, future));

        Mockito.verify(client).validateAccountBundle(future);
    }

    public void testValidateAccountBundleWithAccessToken() throws Exception {
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_AUTHTOKEN, ACCESS_TOKEN);
        result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = new AuthClient.Default(proxy);

        Mockito.when(future.getResult()).thenReturn(result);

        final Auth.Response response = client.validateAccountBundle(future);

        assertEquals(ACCESS_TOKEN, response.accessToken);
        assertEquals(ACCOUNT_NAME, response.accountName);

        Mockito.verify(future).getResult();
    }

    public void testValidateAccountBundleWithoutAccessToken() throws Exception {
        final Bundle result = new Bundle();
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));
        final Auth.Response response = Mockito.mock(Auth.Response.class);

        Mockito.when(future.getResult()).thenReturn(result);
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(Exception.class));

        assertEquals(response, client.validateAccountBundle(future));

        Mockito.verify(future).getResult();
        Mockito.verify(client).getFailureAuthResponse(Mockito.any(Exception.class));
    }

    public void testValidateAccountBundleThrowsException() throws Exception {
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));
        final Auth.Response response = Mockito.mock(Auth.Response.class);
        final RuntimeException exception = Mockito.mock(RuntimeException.class);

        Mockito.doThrow(exception).when(future).getResult();
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(RuntimeException.class));

        assertEquals(response, client.validateAccountBundle(future));

        Mockito.verify(future).getResult();
        Mockito.verify(client).getFailureAuthResponse(exception);
    }
}
