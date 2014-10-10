/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

public class PackageHelperTest extends AndroidTestCase {

    private static final String CUSTOM_ACTIVITY = "io.pivotal.android.auth.PackageHelperTest$LoginActivity";
    private static final String CUSTOM_ACTIVITY2 = "io.pivotal.android.auth.PackageHelperTest$LoginActivity2";
    private static final String CUSTOM_ACTIVITY3 = "io.pivotal.android.auth.PackageHelperTest$NotLoginActivity";
    private static final String PASSWORD_ACTIVITY = "io.pivotal.android.auth.LoginPasswordActivity";

    private static final ActivityInfo[] NULL_ACTIVITY_INFO = null;
    private static final ActivityInfo[] EMPTY_ACTIVITY_INFO = new ActivityInfo[0];
    private static final ActivityInfo[] CUSTOM_ACTIVITY_INFO = new ActivityInfo[1];
    private static final ActivityInfo[] PACKAGE_ACTIVITY_INFO = new ActivityInfo[1];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO = new ActivityInfo[2];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO2 = new ActivityInfo[2];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO3 = new ActivityInfo[3];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO4 = new ActivityInfo[3];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO5 = new ActivityInfo[3];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO6 = new ActivityInfo[4];
    private static final ActivityInfo[] COMBINED_ACTIVITY_INFO7 = new ActivityInfo[4];

    static {
        CUSTOM_ACTIVITY_INFO[0] = new ActivityInfo();
        CUSTOM_ACTIVITY_INFO[0].name = CUSTOM_ACTIVITY;

        PACKAGE_ACTIVITY_INFO[0] = new ActivityInfo();
        PACKAGE_ACTIVITY_INFO[0].name = PASSWORD_ACTIVITY;

        COMBINED_ACTIVITY_INFO[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO[0].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO[1].name = PASSWORD_ACTIVITY;

        COMBINED_ACTIVITY_INFO2[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO2[0].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO2[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO2[1].name = CUSTOM_ACTIVITY;

        COMBINED_ACTIVITY_INFO3[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO3[0].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO3[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO3[1].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO3[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO3[2].name = CUSTOM_ACTIVITY2;

        COMBINED_ACTIVITY_INFO4[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO4[0].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO4[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO4[1].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO4[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO4[2].name = CUSTOM_ACTIVITY2;

        COMBINED_ACTIVITY_INFO5[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO5[0].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO5[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO5[1].name = CUSTOM_ACTIVITY2;
        COMBINED_ACTIVITY_INFO5[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO5[2].name = CUSTOM_ACTIVITY;

        COMBINED_ACTIVITY_INFO6[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO6[0].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO6[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO6[1].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO6[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO6[2].name = CUSTOM_ACTIVITY2;
        COMBINED_ACTIVITY_INFO6[3] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO6[3].name = CUSTOM_ACTIVITY3;

        COMBINED_ACTIVITY_INFO7[0] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO7[0].name = PASSWORD_ACTIVITY;
        COMBINED_ACTIVITY_INFO7[1] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO7[1].name = CUSTOM_ACTIVITY3;
        COMBINED_ACTIVITY_INFO7[2] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO7[2].name = CUSTOM_ACTIVITY;
        COMBINED_ACTIVITY_INFO7[3] = new ActivityInfo();
        COMBINED_ACTIVITY_INFO7[3].name = CUSTOM_ACTIVITY2;
    }


    public void testGetLoginActivityThrowsIllegalStateExceptionForNullActivities() {
        try {
            final Context context = new FakePackageManagerContext(NULL_ACTIVITY_INFO);
            PackageHelper.getLoginActivityClass(context);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }
    }

    public void testGetLoginActivityThrowsIllegalStateExceptionForEmptyActivities() {
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

    public void testGetLoginActivityWithCombinedActivities4() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO4);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY, klass.getName());
    }

    public void testGetLoginActivityWithCombinedActivities5() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO5);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY2, klass.getName());
    }

    public void testGetLoginActivityWithCombinedActivities6() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO6);
        final Class<?> klass = PackageHelper.getLoginActivityClass(context);

        assertEquals(CUSTOM_ACTIVITY, klass.getName());
    }

    public void testGetLoginActivityWithCombinedActivities7() {
        final Context context = new FakePackageManagerContext(COMBINED_ACTIVITY_INFO7);
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
    private static class LoginActivity2 extends AccountAuthenticatorActivity {}
    private static class NotLoginActivity extends Activity {}
}