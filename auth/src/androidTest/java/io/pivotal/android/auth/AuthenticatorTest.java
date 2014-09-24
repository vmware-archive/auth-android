/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

import java.io.IOException;

public class AuthenticatorTest extends AndroidTestCase {

    public static final String TEST_PACKAGE_NAME = "io.pivotal.android.auth";
    public static final String TEST_ACTIVITY_NAME = "AuthenticatorTest$LoginActivity";

    public void testAddAccountFailsIfActivityNotFound() throws Exception {
        try {
            final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, "FakeActivity");
            final Authenticator authenticator = new Authenticator(context);
            authenticator.addAccount(null, null, null, null, null);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }
    }

    public void testAddAccountHasCorrectComponentInfo() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Authenticator authenticator = new Authenticator(context);
        final Bundle bundle = authenticator.addAccount(null, null, null, null, null);
        final Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);

        assertEquals(TEST_PACKAGE_NAME, intent.getComponent().getPackageName());
        assertEquals(TEST_ACTIVITY_NAME, intent.getComponent().getShortClassName().replace(".", ""));
    }

    public void testAddAccountBundleHasNoHistoryFlag() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Authenticator authenticator = new Authenticator(context);
        final Bundle bundle = authenticator.addAccount(null, null, null, null, null);
        final Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);

        assertEquals(Intent.FLAG_ACTIVITY_NO_HISTORY, intent.getFlags() & Intent.FLAG_ACTIVITY_NO_HISTORY);
    }

    public void testAddAccountBundleHasAuthenticatorResponse() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Authenticator authenticator = new Authenticator(context);
        final Bundle bundle = authenticator.addAccount(null, null, null, null, null);
        final Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);

        assertTrue(intent.hasExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
    }

    public void testGetAuthTokenWithEmptyTokens() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Authenticator authenticator = new Authenticator(context) {
            @Override
            protected Token getExistingToken(final Account account) {
                return new EmptyToken();
            }
        };

        final Bundle bundle = authenticator.getAuthToken(null, null, null, null);
        assertTrue(bundle.containsKey(AccountManager.KEY_INTENT));
    }

    public void testGetAuthTokenWithEmptyAuthTokenAndInvalidRefreshToken() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Authenticator authenticator = new Authenticator(context) {
            @Override
            protected Token getExistingToken(final Account account) {
                return new RefreshToken("token");
            }

            @Override
            protected Token getNewToken(final String refreshToken) throws IOException {
                throw new IllegalArgumentException("Invalid token.");
            }
        };

        final Bundle bundle = authenticator.getAuthToken(null, null, null, null);
        assertTrue(bundle.containsKey(AccountManager.KEY_INTENT));
    }

    public void testGetAuthTokenWithEmptyAuthTokenAndValidRefreshToken() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Account account = new Account("account", "account_type");
        final Authenticator authenticator = new Authenticator(context) {
            @Override
            protected Token getExistingToken(final Account account) {
                return new RefreshToken("token");
            }

            @Override
            protected Token getNewToken(final String refreshToken) throws IOException {
                return new AuthToken("token", "token");
            }
        };

        final Bundle bundle = authenticator.getAuthToken(null, account, null, null);

        assertEquals(account.name, bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
        assertEquals(account.type, bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));
        assertEquals("token", bundle.getString(AccountManager.KEY_AUTHTOKEN));
    }

    public void testGetAuthTokenWithValidAccessToken() throws Exception {
        final Context context = new FakePackageManagerContext(TEST_PACKAGE_NAME, TEST_ACTIVITY_NAME);
        final Account account = new Account("account", "account_type");
        final Authenticator authenticator = new Authenticator(context) {
            @Override
            protected Token getExistingToken(final Account account) {
                return new AuthToken("token", "token");
            }
        };

        final Bundle bundle = authenticator.getAuthToken(null, account, null, null);

        assertEquals(account.name, bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
        assertEquals(account.type, bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));
        assertEquals("token", bundle.getString(AccountManager.KEY_AUTHTOKEN));
    }

    public void testGetAuthTokenLabelReturnsAuthTokenType() throws Exception {
        final Authenticator authenticator = new Authenticator(null);
        final String label = authenticator.getAuthTokenLabel("test_label_type");

        assertEquals("test_label_type", label);
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

    public void testUpdateCredentialsThrowsUnsupportedOperationException() throws Exception {
        try {
            final Authenticator authenticator = new Authenticator(null);
            authenticator.updateCredentials(null, null, null, null);
            fail();
        } catch (final UnsupportedOperationException e) {
            assertNotNull(e);
        }
    }


    // ====================================================


    private static class EmptyToken extends Token {

        public EmptyToken() {
            super("", "");
        }
    }

    private static class RefreshToken extends Token {

        public RefreshToken(final String refreshToken) {
            super("", refreshToken);
        }
    }

    private static class AuthToken extends Token {

        public AuthToken(final String authToken, final String refreshToken) {
            super(authToken, refreshToken);
        }

        @Override
        public boolean isExpired() {
            return false;
        }
    }

    private static class FakePackageManagerContext extends MockContext {

        private final String mPackageName;
        private final PackageManager mPackageManager;

        public FakePackageManagerContext(final String packageName, final String activityName) {
            mPackageName = packageName;
            mPackageManager = new FakePackageManager(packageName, activityName);
        }

        @Override
        public PackageManager getPackageManager() {
            return mPackageManager;
        }

        @Override
        public String getPackageName() {
            return mPackageName;
        }
    }

    private static class FakePackageManager extends MockPackageManager {

        private final String mPackageName;
        private final String mActivityName;

        public FakePackageManager(final String packageName, final String activityName) {
            mPackageName = packageName;
            mActivityName = activityName;
        }

        @Override
        public PackageInfo getPackageInfo(final String packageName, final int flags) throws NameNotFoundException {
            if (!mPackageName.equals(packageName)) {
                throw new NameNotFoundException("Required package name: " + mPackageName + " doesnt' match: " + packageName);
            }

            final ActivityInfo[] activities = new ActivityInfo[1];
            activities[0] = new ActivityInfo();
            activities[0].name = mPackageName + "." + mActivityName;

            final PackageInfo info = new PackageInfo();
            info.activities = activities;
            return info;
        }
    }

    private static class LoginActivity extends AccountAuthenticatorActivity {}
}
