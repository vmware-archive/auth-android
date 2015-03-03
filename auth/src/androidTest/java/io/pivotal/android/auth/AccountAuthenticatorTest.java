/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Base64;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;

import org.mockito.Mockito;

import java.io.IOException;
import java.util.UUID;

public class AccountAuthenticatorTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();
    private static final String TOKEN_LABEL = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        AccountsProxyHolder.init(null);
        RemoteAuthenticatorHolder.init(null);
    }

    public void testAddAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));
        final Class<LoginActivity> klass = LoginActivity.class;

        Mockito.doReturn(klass).when(accountAuthenticator).getLoginActivityClass();

        final Bundle bundle = accountAuthenticator.addAccount(response, null, null, null, null);
        final Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        final ComponentName component = intent.getComponent();

        assertEquals(klass.getName(), component.getClassName());
        assertEquals(response, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));

        Mockito.verify(accountAuthenticator).getLoginActivityClass();
    }

    public void testConfirmCredentialsThrowsUnsupportedOperationException() throws Exception {
        try {
            final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
            accountAuthenticator.confirmCredentials(null, null, null);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }

    public void testEditPropertiesThrowsUnsupportedOperationException() throws Exception {
        try {
            final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
            accountAuthenticator.editProperties(null, null);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenTokenNotEmptyOrExpired() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        AccountsProxyHolder.init(proxy);

        Mockito.when(proxy.getAccessToken(account)).thenReturn(ACCESS_TOKEN);
        Mockito.doReturn(bundle).when(accountAuthenticator).newAuthTokenBundle(account, ACCESS_TOKEN);

        assertEquals(bundle, accountAuthenticator.getAuthToken(null, account, null, null));

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(accountAuthenticator).newAuthTokenBundle(account, ACCESS_TOKEN);
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenTokenEmpty() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        AccountsProxyHolder.init(proxy);

        Mockito.when(proxy.getAccessToken(account)).thenReturn("");
        Mockito.when(proxy.getRefreshToken(account)).thenReturn(REFRESH_TOKEN);
        Mockito.doReturn(bundle).when(accountAuthenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);

        assertEquals(bundle, accountAuthenticator.getAuthToken(response, account, null, null));

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(proxy).getRefreshToken(account);
        Mockito.verify(accountAuthenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenTokenExpired() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        AccountsProxyHolder.init(proxy);

        Mockito.when(proxy.getAccessToken(account)).thenReturn(getExpiredToken());
        Mockito.when(proxy.getRefreshToken(account)).thenReturn(REFRESH_TOKEN);
        Mockito.doReturn(bundle).when(accountAuthenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);

        assertEquals(bundle, accountAuthenticator.getAuthToken(response, account, null, null));

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(proxy).getRefreshToken(account);
        Mockito.verify(accountAuthenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenRefreshTokenEmpty() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        AccountsProxyHolder.init(proxy);

        Mockito.when(proxy.getAccessToken(account)).thenReturn("");
        Mockito.when(proxy.getRefreshToken(account)).thenReturn("");
        Mockito.doReturn(bundle).when(accountAuthenticator).newAccountBundle(response);

        assertEquals(bundle, accountAuthenticator.getAuthToken(response, account, null, null));

        Mockito.verify(proxy).getAccessToken(account);
        Mockito.verify(proxy).getRefreshToken(account);
        Mockito.verify(accountAuthenticator).newAccountBundle(response);
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenForAuthToken() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final RemoteAuthenticator authenticator = Mockito.mock(RemoteAuthenticator.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));
        final TokenResponse response = new TokenResponse();
        response.setAccessToken(ACCESS_TOKEN);

        RemoteAuthenticatorHolder.init(authenticator);

        Mockito.when(authenticator.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.when(request.execute()).thenReturn(response);
        Mockito.doReturn(bundle).when(accountAuthenticator).newAuthTokenBundle(account, ACCESS_TOKEN);

        assertEquals(bundle, accountAuthenticator.newAuthTokenBundle(null, account, REFRESH_TOKEN));

        Mockito.verify(authenticator).newRefreshTokenRequest(REFRESH_TOKEN);
        Mockito.verify(accountAuthenticator).newAuthTokenBundle(account, ACCESS_TOKEN);
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenAndFailsWith401() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final RemoteAuthenticator authenticator = Mockito.mock(RemoteAuthenticator.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        RemoteAuthenticatorHolder.init(authenticator);

        final HttpResponseException.Builder builder = new HttpResponseException.Builder(401, null, new HttpHeaders());
        final HttpResponseException exception = new TestResponseException(builder);

        Mockito.when(authenticator.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.doThrow(exception).when(request).execute();
        Mockito.doReturn(bundle).when(accountAuthenticator).newAccountBundle(response);

        assertEquals(bundle, accountAuthenticator.newAuthTokenBundle(response, account, REFRESH_TOKEN));

        Mockito.verify(authenticator).newRefreshTokenRequest(REFRESH_TOKEN);
        Mockito.verify(accountAuthenticator).newAccountBundle(response);
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenAndFailsWith400() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final RemoteAuthenticator authenticator = Mockito.mock(RemoteAuthenticator.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        RemoteAuthenticatorHolder.init(authenticator);

        final HttpResponseException.Builder builder = new HttpResponseException.Builder(400, null, new HttpHeaders());
        final HttpResponseException exception = new TestResponseException(builder);

        Mockito.when(authenticator.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.doThrow(exception).when(request).execute();
        Mockito.doReturn(bundle).when(accountAuthenticator).newErrorBundle(Mockito.eq(account), Mockito.any(Throwable.class));

        assertEquals(bundle, accountAuthenticator.newAuthTokenBundle(null, account, REFRESH_TOKEN));

        Mockito.verify(authenticator).newRefreshTokenRequest(REFRESH_TOKEN);
        Mockito.verify(accountAuthenticator).newErrorBundle(Mockito.eq(account), Mockito.any(Throwable.class));
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenAndFailsWithException() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final RemoteAuthenticator authenticator = Mockito.mock(RemoteAuthenticator.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final AccountAuthenticator accountAuthenticator = Mockito.spy(new AccountAuthenticator(context));

        RemoteAuthenticatorHolder.init(authenticator);

        Mockito.when(authenticator.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.doThrow(new IOException()).when(request).execute();
        Mockito.doReturn(bundle).when(accountAuthenticator).newErrorBundle(Mockito.eq(account), Mockito.any(Throwable.class));

        assertEquals(bundle, accountAuthenticator.newAuthTokenBundle(null, account, REFRESH_TOKEN));

        Mockito.verify(accountAuthenticator).newErrorBundle(Mockito.eq(account), Mockito.any(Throwable.class));
    }

    public void testGetAuthTokenLabelReturnsAuthTokenType() throws Exception {
        final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
        final String label = accountAuthenticator.getAuthTokenLabel(TOKEN_LABEL);

        assertEquals(TOKEN_LABEL, label);
    }

    public void testHasFeaturesReturnsFalseForNullFeatures() throws Exception {
        final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
        final Bundle bundle = accountAuthenticator.hasFeatures(null, null, null);

        assertFalse(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }

    public void testHasFeaturesReturnsFalseForEmptyFeatures() throws Exception {
        final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
        final Bundle bundle = accountAuthenticator.hasFeatures(null, null, new String[] {});

        assertFalse(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }

    public void testHasFeaturesReturnsFalseForOpenIdFeature() throws Exception {
        final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
        final Bundle bundle = accountAuthenticator.hasFeatures(null, null, new String[] { "openid" });

        assertFalse(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }

    public void testUpdateCredentialsThrowsUnsupportedOperationException() throws Exception {
        try {
            final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(null);
            accountAuthenticator.updateCredentials(null, null, null, null);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }


    // ====================================


    private String getExpiredToken() {
        final long expirationInSeconds = System.currentTimeMillis() / 1000 - 60;
        final String component = "{ \"exp\": \"" + expirationInSeconds + "\" }";
        return "." + Base64.encodeToString(component.getBytes(), Base64.DEFAULT);
    }

    private static class LoginActivity extends AccountAuthenticatorActivity {
    }

    private static class TestResponseException extends HttpResponseException {

        public TestResponseException(final Builder builder) {
            super(builder);
        }
    }
}
