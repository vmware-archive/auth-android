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

import org.mockito.Mockito;

import java.util.UUID;

public class PackageUtilsTest extends AndroidTestCase {

    private static final String PACKAGE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testGetLoginActivityClassThrowsExceptionWhenPackageInfoNotFound() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenThrow(new RuntimeException());

        try {
            PackageUtils.getLoginActivityClass(context);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    public void testGetLoginActivityClassThrowsExceptionWhenActivitiesNull() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);
        final PackageInfo info = Mockito.mock(PackageInfo.class);

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenReturn(info);

        try {
            PackageUtils.getLoginActivityClass(context);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    public void testGetLoginActivityClassThrowsExceptionWhenActivitiesEmpty() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);
        final PackageInfo info = new PackageInfo();
        info.activities = new ActivityInfo[0];

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenReturn(info);

        try {
            PackageUtils.getLoginActivityClass(context);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    public void testGetLoginActivityClassThrowsExceptionWhenActivityNotFound() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);
        final ActivityInfo activity = new ActivityInfo();
        activity.name = TestActivity.class.getName();
        final PackageInfo info = new PackageInfo();
        info.activities = new ActivityInfo[]{activity};

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenReturn(info);

        try {
            PackageUtils.getLoginActivityClass(context);
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    public void testGetLoginActivityClassFindsAccountAuthenticatorActivitySubclass() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);
        final ActivityInfo activity = new ActivityInfo();
        activity.name = TestLoginActivity.class.getName();
        final PackageInfo info = new PackageInfo();
        info.activities = new ActivityInfo[]{activity};

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenReturn(info);

        assertEquals(activity.name, PackageUtils.getLoginActivityClass(context).getName());

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    public void testGetLoginActivityClassReturnsLoginPasswordActivityWhenItsTheOnlyActivityFound() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);
        final ActivityInfo activity = new ActivityInfo();
        activity.name = LoginPasswordActivity.class.getName();
        final PackageInfo info = new PackageInfo();
        info.activities = new ActivityInfo[]{activity};

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenReturn(info);

        assertEquals(activity.name, PackageUtils.getLoginActivityClass(context).getName());

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    public void testGetLoginActivityClassRemovesLoginPasswordActivityWhenMultipleActivitiesFound() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final PackageManager manager = Mockito.mock(PackageManager.class);
        final ActivityInfo activity1 = new ActivityInfo();
        activity1.name = LoginPasswordActivity.class.getName();
        final ActivityInfo activity2 = new ActivityInfo();
        activity2.name = TestLoginActivity.class.getName();
        final PackageInfo info = new PackageInfo();
        info.activities = new ActivityInfo[]{activity1, activity2};

        Mockito.when(context.getPackageManager()).thenReturn(manager);
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE);
        Mockito.when(manager.getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES)).thenReturn(info);

        assertEquals(activity2.name, PackageUtils.getLoginActivityClass(context).getName());

        Mockito.verify(context).getPackageManager();
        Mockito.verify(context).getPackageName();
        Mockito.verify(manager).getPackageInfo(PACKAGE, PackageManager.GET_ACTIVITIES);
    }

    private static final class TestActivity extends Activity {}
    private static final class TestLoginActivity extends AccountAuthenticatorActivity {}
}