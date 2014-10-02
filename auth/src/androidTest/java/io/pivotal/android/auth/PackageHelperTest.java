/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

public class PackageHelperTest extends AndroidTestCase {

    private static final String CUSTOM_ACTIVITY = "io.pivotal.android.auth.PackageHelperTest$LoginActivity";
    private static final String PASSWORD_ACTIVITY = "io.pivotal.android.auth.LoginPasswordActivity";
    private static final String AUTH_CODE_ACTIVITY = "io.pivotal.android.auth.LoginAuthCodeActivity";

    private static final ActivityInfo[] EMPTY_ACTIVITY_INFO = new ActivityInfo[0];
    private static final ActivityInfo[] CUSTOM_ACTIVITY_INFO = new ActivityInfo[1];
    private static final ActivityInfo[] PACKAGE_ACTIVITY_INFO = new ActivityInfo[2];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO = new ActivityInfo[3];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO2 = new ActivityInfo[3];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO3 = new ActivityInfo[3];

    static {
        CUSTOM_ACTIVITY_INFO[0] = new ActivityInfo();
        CUSTOM_ACTIVITY_INFO[0].name = CUSTOM_ACTIVITY;

        PACKAGE_ACTIVITY_INFO[0] = new ActivityInfo();
        PACKAGE_ACTIVITY_INFO[0].name = PASSWORD_ACTIVITY;
        PACKAGE_ACTIVITY_INFO[1] = new ActivityInfo();
        PACKAGE_ACTIVITY_INFO[1].name = AUTH_CODE_ACTIVITY;

        COMBINED_ACTIVITY_INFO[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO[0].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO[1].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO[2].name = AUTH_CODE_ACTIVITY;

        COMBINED_ACTIVITY_INFO2[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO2[0].name = AUTH_CODE_ACTIVITY;
        COMBINED_ACTIVITY_INFO2[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO2[1].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO2[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO2[2].name = CUSTOM_ACTIVITY;

        COMBINED_ACTIVITY_INFO3[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO3[0].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO3[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO3[1].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO3[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO3[2].name = AUTH_CODE_ACTIVITY;

    }

    public void testGetLoginActivityThrowsIllegalStateException() {
        try {
            final Context context = new FakePackageManagerContext(EMPTY_ACTIVITY_INFO);
            PackageHelper.getLoginActivityClass(context);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }
    }

    public void testGetLoginActivityWithCustomActivity() {
        final Context context = new FakePackageManagerContext(CUSTOM_ACTIVITY_INFO);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY, klass.getName());
    }

    public void testGetLoginActivityWithPackagedActivities() {
        final Context context = new FakePackageManagerContext(PACKAGE_ACTIVITY_INFO);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(PASSWORD_ACTIVITY, klass.getName());
    }

    public void testGetLoginActivityWithCombinedActivities() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY, klass.getName());
    }

    public void testGetLoginActivityWithCombinedActivities2() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO2);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY, klass.getName());
    }

    public void testGetLoginActivityWithCombinedActivities3() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO3);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY, klass.getName());
    }

    private static class FakePackageManagerContext extends MockContext {

        private final ActivityInfo[] mActivities;

        public FakePackageManagerContext(final ActivityInfo[] activities) {
            mActivities = activities;
        }

        @Override
        public PackageManager getPackageManager() {
            return new FakePackageManager(mActivities);
        }

        @Override
        public String getPackageName() {
            return "";
        }
    }

    private static class FakePackageManager extends MockPackageManager {

        private final ActivityInfo[] mActivities;

        public FakePackageManager(final ActivityInfo[] activities) {
            mActivities = activities;
        }

        @Override
        public PackageInfo getPackageInfo(final String packageName, final int flags) throws NameNotFoundException {
            final PackageInfo info = new PackageInfo();
            info.activities = mActivities;
            return info;
        }
    }

    private static class LoginActivity extends AccountAuthenticatorActivity {}
}