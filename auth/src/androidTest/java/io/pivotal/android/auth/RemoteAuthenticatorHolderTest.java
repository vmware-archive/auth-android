/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class RemoteAuthenticatorHolderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        RemoteAuthenticatorHolder.init(null);
    }

    public void testWithInitialization() {
        final RemoteAuthenticator custom = Mockito.mock(RemoteAuthenticator.class);
        RemoteAuthenticatorHolder.init(custom);
        final RemoteAuthenticator authenticator = RemoteAuthenticatorHolder.get();
        assertEquals(custom, authenticator);
    }

    public void testWithoutInitialization() {
        RemoteAuthenticatorHolder.init(null);
        final RemoteAuthenticator instance = RemoteAuthenticatorHolder.get();
        final RemoteAuthenticator.Default authenticator = (RemoteAuthenticator.Default) instance;
        assertNotNull(authenticator);
    }
}
