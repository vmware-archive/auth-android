/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class RemoteAuthenticatorFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testWithInitialization() {
        final RemoteAuthenticator custom = Mockito.mock(RemoteAuthenticator.class);
        RemoteAuthenticatorFactory.init(custom);
        final RemoteAuthenticator authenticator = RemoteAuthenticatorFactory.get();
        assertEquals(custom, authenticator);
    }

    public void testWithoutInitialization() {
        RemoteAuthenticatorFactory.init(null);
        final RemoteAuthenticator instance = RemoteAuthenticatorFactory.get();
        final RemoteAuthenticator.Default authenticator = (RemoteAuthenticator.Default) instance;
        assertNotNull(authenticator);
    }
}
