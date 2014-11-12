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

public class AuthenticatorTest extends AndroidTestCase {

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();
    private static final String TOKEN_LABEL = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testAddAccount() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));
        final Class<LoginActivity> klass = LoginActivity.class;

        Mockito.doReturn(klass).when(authenticator).getLoginActivityClass();

        final Bundle bundle = authenticator.addAccount(response, null, null, null, null);
        final Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        final ComponentName component = intent.getComponent();

        assertEquals(klass.getName(), component.getClassName());
        assertEquals(response, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
        assertEquals(Intent.FLAG_ACTIVITY_NO_HISTORY, intent.getFlags() & Intent.FLAG_ACTIVITY_NO_HISTORY);

        Mockito.verify(authenticator).getLoginActivityClass();
    }

    public void testConfirmCredentialsThrowsUnsupportedOperationException() throws Exception {
        try {
            final Authenticator authenticator = new Authenticator(null);
            authenticator.confirmCredentials(null, null, null);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }

    public void testEditPropertiesThrowsUnsupportedOperationException() throws Exception {
        try {
            final Authenticator authenticator = new Authenticator(null);
            authenticator.editProperties(null, null);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenTokenNotEmptyOrExpired() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        TokenProviderFactory.init(provider);

        Mockito.when(provider.getAccessToken(account)).thenReturn(ACCESS_TOKEN);
        Mockito.doReturn(bundle).when(authenticator).newAuthTokenBundle(account, ACCESS_TOKEN);

        assertEquals(bundle, authenticator.getAuthToken(null, account, null, null));

        Mockito.verify(provider).getAccessToken(account);
        Mockito.verify(authenticator).newAuthTokenBundle(account, ACCESS_TOKEN);
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenTokenEmpty() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        TokenProviderFactory.init(provider);

        Mockito.when(provider.getAccessToken(account)).thenReturn("");
        Mockito.when(provider.getRefreshToken(account)).thenReturn(REFRESH_TOKEN);
        Mockito.doReturn(bundle).when(authenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);

        assertEquals(bundle, authenticator.getAuthToken(response, account, null, null));

        Mockito.verify(provider).getAccessToken(account);
        Mockito.verify(provider).getRefreshToken(account);
        Mockito.verify(authenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenTokenExpired() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        TokenProviderFactory.init(provider);

        Mockito.when(provider.getAccessToken(account)).thenReturn(getExpiredToken());
        Mockito.when(provider.getRefreshToken(account)).thenReturn(REFRESH_TOKEN);
        Mockito.doReturn(bundle).when(authenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);

        assertEquals(bundle, authenticator.getAuthToken(response, account, null, null));

        Mockito.verify(provider).getAccessToken(account);
        Mockito.verify(provider).getRefreshToken(account);
        Mockito.verify(authenticator).newAuthTokenBundle(response, account, REFRESH_TOKEN);
    }

    public void testGetAuthTokenReturnsAuthTokenBundleWhenRefreshTokenEmpty() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final TokenProvider provider = Mockito.mock(TokenProvider.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        TokenProviderFactory.init(provider);

        Mockito.when(provider.getAccessToken(account)).thenReturn("");
        Mockito.when(provider.getRefreshToken(account)).thenReturn("");
        Mockito.doReturn(bundle).when(authenticator).newAccountBundle(response);

        assertEquals(bundle, authenticator.getAuthToken(response, account, null, null));

        Mockito.verify(provider).getAccessToken(account);
        Mockito.verify(provider).getRefreshToken(account);
        Mockito.verify(authenticator).newAccountBundle(response);
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenForAuthToken() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AuthProvider provider = Mockito.mock(AuthProvider.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));
        final TokenResponse response = new TokenResponse();
        response.setAccessToken(ACCESS_TOKEN);

        AuthProviderFactory.init(provider);

        Mockito.when(provider.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.when(request.execute()).thenReturn(response);
        Mockito.doReturn(bundle).when(authenticator).newAuthTokenBundle(account, ACCESS_TOKEN);

        assertEquals(bundle, authenticator.newAuthTokenBundle(null, account, REFRESH_TOKEN));

        Mockito.verify(provider).newRefreshTokenRequest(REFRESH_TOKEN);
        Mockito.verify(authenticator).newAuthTokenBundle(account, ACCESS_TOKEN);
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenAndFailsWith401() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AuthProvider provider = Mockito.mock(AuthProvider.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final AccountAuthenticatorResponse response = Mockito.mock(AccountAuthenticatorResponse.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        AuthProviderFactory.init(provider);

        final HttpResponseException.Builder builder = new HttpResponseException.Builder(401, null, new HttpHeaders());
        final HttpResponseException exception = new TestResponseException(builder);

        Mockito.when(provider.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.doThrow(exception).when(request).execute();
        Mockito.doReturn(bundle).when(authenticator).newAccountBundle(response);

        assertEquals(bundle, authenticator.newAuthTokenBundle(response, account, REFRESH_TOKEN));

        Mockito.verify(provider).newRefreshTokenRequest(REFRESH_TOKEN);
        Mockito.verify(authenticator).newAccountBundle(response);
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenAndFailsWith400() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AuthProvider provider = Mockito.mock(AuthProvider.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        AuthProviderFactory.init(provider);

        final HttpResponseException.Builder builder = new HttpResponseException.Builder(400, null, new HttpHeaders());
        final HttpResponseException exception = new TestResponseException(builder);

        Mockito.when(provider.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.doThrow(exception).when(request).execute();
        Mockito.doReturn(bundle).when(authenticator).newErrorBundle(Mockito.eq(account), Mockito.anyString());

        assertEquals(bundle, authenticator.newAuthTokenBundle(null, account, REFRESH_TOKEN));

        Mockito.verify(provider).newRefreshTokenRequest(REFRESH_TOKEN);
        Mockito.verify(authenticator).newErrorBundle(Mockito.eq(account), Mockito.anyString());
    }

    public void testNewAuthTokenBundleExchangesRefreshTokenAndFailsWithException() throws Exception {
        final Bundle bundle = Bundle.EMPTY;
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AuthProvider provider = Mockito.mock(AuthProvider.class);
        final RefreshTokenRequest request = Mockito.mock(RefreshTokenRequest.class);
        final Authenticator authenticator = Mockito.spy(new Authenticator(context));

        AuthProviderFactory.init(provider);

        Mockito.when(provider.newRefreshTokenRequest(REFRESH_TOKEN)).thenReturn(request);
        Mockito.doThrow(new IOException()).when(request).execute();
        Mockito.doReturn(bundle).when(authenticator).newErrorBundle(Mockito.eq(account), Mockito.anyString());

        assertEquals(bundle, authenticator.newAuthTokenBundle(null, account, REFRESH_TOKEN));

        Mockito.verify(authenticator).newErrorBundle(Mockito.eq(account), Mockito.anyString());
    }

    public void testGetAuthTokenLabelReturnsAuthTokenType() throws Exception {
        final Authenticator authenticator = new Authenticator(null);
        final String label = authenticator.getAuthTokenLabel(TOKEN_LABEL);

        assertEquals(TOKEN_LABEL, label);
    }

    public void testHasFeaturesReturnsFalseForNullFeatures() throws Exception {
        final Authenticator authenticator = new Authenticator(null);
        final Bundle bundle = authenticator.hasFeatures(null, null, null);

        assertFalse(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }

    public void testHasFeaturesReturnsFalseForEmptyFeatures() throws Exception {
        final Authenticator authenticator = new Authenticator(null);
        final Bundle bundle = authenticator.hasFeatures(null, null, new String[] {});

        assertFalse(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }

    public void testHasFeaturesReturnsFalseForOpenIdFeature() throws Exception {
        final Authenticator authenticator = new Authenticator(null);
        final Bundle bundle = authenticator.hasFeatures(null, null, new String[] { "openid" });

        assertFalse(bundle.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }

    public void testUpdateCredentialsThrowsUnsupportedOperationException() throws Exception {
        try {
            final Authenticator authenticator = new Authenticator(null);
            authenticator.updateCredentials(null, null, null, null);
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
