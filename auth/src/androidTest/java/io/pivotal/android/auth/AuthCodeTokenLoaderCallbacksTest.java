/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Loader;
import android.os.Bundle;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.TokenResponse;

import java.util.UUID;

public class AuthCodeTokenLoaderCallbacksTest extends AndroidTestCase {

    private static final String AUTH_CODE = UUID.randomUUID().toString();

    public void testCreateBundle() {
        final Bundle bundle = AuthCodeTokenLoaderCallbacks.createBundle(AUTH_CODE);

        assertEquals(AUTH_CODE, bundle.getString("auth_code"));
    }

    public void testOnCreateLoader() {
        final AuthCodeTokenLoaderCallbacks callbacks = new AuthCodeTokenLoaderCallbacks(mContext, null);
        final Loader<TokenResponse> loader = callbacks.onCreateLoader(0, Bundle.EMPTY);

        assertTrue(loader instanceof AuthCodeTokenLoader);
    }
}
