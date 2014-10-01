/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.os.Bundle;
import android.test.AndroidTestCase;

public class PasswordTokenLoaderCallbacksTest extends AndroidTestCase {

    public void testCreateBundle() {
        final Bundle bundle = PasswordTokenLoaderCallbacks.createBundle("username", "password");

        assertEquals("username", bundle.getString("username"));
        assertEquals("password", bundle.getString("password"));
    }

    public void testOnCreateLoader() {
        final PasswordTokenLoaderCallbacks callbacks = new PasswordTokenLoaderCallbacks(mContext, null);
        final PasswordTokenLoader loader = (PasswordTokenLoader) callbacks.onCreateLoader(0, Bundle.EMPTY);

        assertNotNull(loader);
    }

}
