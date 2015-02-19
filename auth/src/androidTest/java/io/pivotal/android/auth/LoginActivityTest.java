/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;

import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.UUID;

public class LoginActivityTest extends ActivityUnitTestCase<LoginActivityTest.TestLoginActivity> {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();

    public LoginActivityTest() {
        super(TestLoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Context context = getInstrumentation().getContext();
        System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath());
    }

    public void testAuthorizationCompleteAddsAccountAndSetsAccessToken() {
        final Token token = new Token(ACCESS_TOKEN, REFRESH_TOKEN);
        final AccountsProxy provider = Mockito.mock(AccountsProxy.class);
        final TestLoginActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

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
        final TestLoginActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

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
        final TestLoginActivity activity = startActivity(new Intent(), null, null);

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