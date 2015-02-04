/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class AuthClientFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testWithInitialization() {
        final AuthClient custom = Mockito.mock(AuthClient.class);
        AuthClientFactory.init(custom);
        final AuthClient client = AuthClientFactory.get(mContext);
        assertEquals(custom, client);
    }

    public void testWithoutInitialization() {
        AuthClientFactory.init(null);
        final AuthClient instance = AuthClientFactory.get(mContext);
        final AuthClient.Default client = (AuthClient.Default) instance;
        assertNotNull(client);
    }
}
