/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Loader;
import android.os.Bundle;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.TokenResponse;

import java.util.UUID;

public class PasswordTokenLoaderCallbacksTest extends AndroidTestCase {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();


    public void testCreateBundle() {
        final Bundle bundle = PasswordTokenLoaderCallbacks.createBundle(USERNAME, PASSWORD);

        assertEquals(USERNAME, bundle.getString("username"));
        assertEquals(PASSWORD, bundle.getString("password"));
    }

    public void testOnCreateLoader() {
        final PasswordTokenLoaderCallbacks callbacks = new PasswordTokenLoaderCallbacks(mContext, null);
        final Loader<TokenResponse> loader = callbacks.onCreateLoader(0, Bundle.EMPTY);

        assertTrue(loader instanceof PasswordTokenLoader);
    }

}
