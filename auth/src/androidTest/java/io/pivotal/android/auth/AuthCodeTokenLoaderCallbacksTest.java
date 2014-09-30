/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.os.Bundle;
import android.test.AndroidTestCase;

public class AuthCodeTokenLoaderCallbacksTest extends AndroidTestCase {

    public void testCreateBundle() {
        final Bundle bundle = AuthCodeTokenLoaderCallbacks.createBundle("code");

        assertEquals("code", bundle.getString("auth_code"));
    }

    public void test() {
        final AuthCodeTokenLoaderCallbacks callbacks = new AuthCodeTokenLoaderCallbacks(mContext, null);
        final AuthCodeTokenLoader loader = (AuthCodeTokenLoader) callbacks.onCreateLoader(0, Bundle.EMPTY);

        assertNotNull(loader);
    }

}
