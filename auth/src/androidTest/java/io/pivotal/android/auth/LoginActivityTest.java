/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivityTest extends AndroidTestCase {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();

    private static final String AUTH_CODE = UUID.randomUUID().toString();
    private static final String AUTH_URL = UUID.randomUUID().toString();

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

    public void testFetchTokenWithAuthCodeGrantTypeWithWebView() {
        final WebView webView = Mockito.mock(WebView.class);
        final LoginActivity activity = Mockito.spy(new TestLoginActivity());

        Mockito.doReturn(AUTH_URL).when(activity).getAuthorizationUrl();

        activity.fetchTokenWithAuthCodeGrantType(webView);

        Mockito.verify(activity).getAuthorizationUrl();
        Mockito.verify(webView).setWebViewClient(Mockito.any(WebViewClient.class));
        Mockito.verify(webView).loadUrl(AUTH_URL);
    }

    public void testHandleRedirectUrlWithCorrectUrl() {
        final LoginActivity activity = Mockito.spy(new TestLoginActivity());

        Mockito.doNothing().when(activity).fetchTokenWithAuthCodeGrantType(Mockito.anyString());

        final String redirectUrl = Pivotal.getRedirectUrl();
        final String redirectUrlWithCode = String.format("%s?code=%s", redirectUrl, AUTH_CODE);

        assertTrue(activity.handleRedirectUrl(null, redirectUrlWithCode));

        Mockito.verify(activity).fetchTokenWithAuthCodeGrantType(AUTH_CODE);
    }

    public void testHandleRedirectUrlWithIncorrectUrl() {
        final LoginActivity activity = Mockito.spy(new TestLoginActivity());

        final String redirectUrl = String.format("%s?code=%s", UUID.randomUUID(), AUTH_CODE);

        assertFalse(activity.handleRedirectUrl(null, redirectUrl));
    }

    public void testFetchTokenWithAuthCodeGrantType() {
        final LoaderManager manager = Mockito.mock(LoaderManager.class);
        final LoginActivity activity = Mockito.spy(new TestLoginActivity());

        Mockito.doReturn(manager).when(activity).getLoaderManager();
        Mockito.when(manager.restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.any(AuthCodeTokenLoaderCallbacks.class))).thenReturn(null);

        activity.fetchTokenWithAuthCodeGrantType(AUTH_CODE);

        Mockito.verify(activity).getLoaderManager();
        Mockito.verify(manager).restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.isA(AuthCodeTokenLoaderCallbacks.class));
    }

    public void testFetchTokenWithPasswordGrantType() {
        final LoaderManager manager = Mockito.mock(LoaderManager.class);
        final LoginActivity activity = Mockito.spy(new TestLoginActivity());

        Mockito.doReturn(manager).when(activity).getLoaderManager();
        Mockito.when(manager.restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.any(PasswordTokenLoaderCallbacks.class))).thenReturn(null);

        activity.fetchTokenWithPasswordGrantType(USERNAME, PASSWORD);

        Mockito.verify(activity).getLoaderManager();
        Mockito.verify(manager).restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.isA(PasswordTokenLoaderCallbacks.class));
    }

    public void testAuthorizationCompleteAddsAccountAndSetsAccessToken() {
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final TestLoginActivity activity = Mockito.spy(new TestLoginActivity());

        Mockito.doNothing().when(provider).addAccount(Mockito.any(Account.class), Mockito.eq(REFRESH_TOKEN));
        Mockito.doNothing().when(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(ACCESS_TOKEN));
        Mockito.doReturn(USERNAME).when(activity).getUserName();
        Mockito.doNothing().when(activity).setResultIntent(token, USERNAME);
        Mockito.doNothing().when(activity).finish();

        AccountsProxyHolder.init(provider);

        activity.onAuthorizationComplete(token);

        Mockito.verify(provider).addAccount(Mockito.any(Account.class), Mockito.eq(REFRESH_TOKEN));
        Mockito.verify(provider).setAccessToken(Mockito.any(Account.class), Mockito.eq(ACCESS_TOKEN));
        Mockito.verify(activity).getUserName();
        Mockito.verify(activity).setResultIntent(token, USERNAME);
        Mockito.verify(activity).finish();
    }

    public void testSetResultIntent() {
        final Bundle bundle = Bundle.EMPTY;
        final Token token = Mockito.mock(Token.class);
        final Intent intent = Mockito.mock(Intent.class);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final TestLoginActivity activity = Mockito.spy(new TestLoginActivity());

        Mockito.doReturn(intent).when(activity).getResultIntent(token, USERNAME);
        Mockito.when(intent.getExtras()).thenReturn(bundle);

        AccountsProxyHolder.init(provider);

        activity.setResultIntent(token, USERNAME);

        assertEquals(bundle, activity.getResultData());
        assertEquals(bundle, activity.getResultBundle());

        Mockito.verify(activity).getResultIntent(token, USERNAME);
        Mockito.verify(intent, Mockito.times(2)).getExtras();
    }

    public void testGetResultIntent() {
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final Token token = Mockito.mock(Token.class);
        final TestLoginActivity activity = new TestLoginActivity();

        Mockito.when(token.getAccessToken()).thenReturn(ACCESS_TOKEN);

        AccountsProxyHolder.init(provider);
        final Intent intent = activity.getResultIntent(token, USERNAME);

        assertEquals(Pivotal.getAccountType(), intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        assertEquals(ACCESS_TOKEN, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
        assertEquals(activity.getUserName(), intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
    }


    // =========================================


    public static class TestLoginActivity extends LoginActivity {

        @Override
        protected String getUserName() {
            return USERNAME;
        }

        public Bundle getResultBundle() {
            try {
                final Class<?> klass = AccountAuthenticatorActivity.class;
                final Field f = getField(klass, "mResultBundle");
                return (Bundle) f.get(this);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Bundle getResultData() {
            try {
                final Class<?> klass = Activity.class;
                final Field f = getField(klass, "mResultData");
                final Intent result = (Intent) f.get(this);
                return result.getExtras();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        private Field getField(final Class<?> klass, final String name) throws NoSuchFieldException {
            final Field field = klass.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
    }
}