/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;

public class LoginAuthCodeActivityTest extends ActivityUnitTestCase<LoginAuthCodeActivityTest.TestLoginAuthCodeActivity> {

    public LoginAuthCodeActivityTest() {
        super(TestLoginAuthCodeActivity.class);
    }

    public void testInitialAuthorize() {
        TestLoginAuthCodeActivity.sWasRestartLoaderCalled = false;
        TestLoginAuthCodeActivity.sWasStartActivityCalled = false;
        startActivity(new Intent(), null, null);
        assertFalse(TestLoginAuthCodeActivity.sWasRestartLoaderCalled);
        assertTrue(TestLoginAuthCodeActivity.sWasStartActivityCalled);
    }

    public void testCallback() {
        TestLoginAuthCodeActivity.sWasRestartLoaderCalled = false;
        TestLoginAuthCodeActivity.sWasStartActivityCalled = false;
        startActivity(getIntentForCallback(), null, null);
        assertTrue(TestLoginAuthCodeActivity.sWasRestartLoaderCalled);
        assertFalse(TestLoginAuthCodeActivity.sWasStartActivityCalled);
    }

    private Intent getIntentForCallback() {
        final Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(Pivotal.getRedirectUrl().toLowerCase()));
        return intent;
    }

    public static class TestLoginAuthCodeActivity extends LoginAuthCodeActivity {

        private static boolean sWasRestartLoaderCalled;
        private static boolean sWasStartActivityCalled;

        @Override
        public void startActivity(Intent intent) {
            super.startActivity(intent);
            sWasStartActivityCalled = true;
        }

        @Override
        public LoaderManager getLoaderManager() {
            return new MockLoaderManager() {

                @Override
                public <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> dLoaderCallbacks) {
                    sWasRestartLoaderCalled = true;
                    return null;
                }
            };
        }
    }

}