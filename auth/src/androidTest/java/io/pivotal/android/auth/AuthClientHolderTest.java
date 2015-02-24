/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class AuthClientHolderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        AccountsProxyHolder.init(null);
    }

    public void testWithInitialization() {
        final AuthClient custom = Mockito.mock(AuthClient.class);
        AuthClientHolder.init(custom);
        final AuthClient client = AuthClientHolder.get(mContext);
        assertEquals(custom, client);
    }

    public void testWithoutInitialization() {
        AuthClientHolder.init(null);
        final AuthClient instance = AuthClientHolder.get(mContext);
        final AuthClient.Default client = (AuthClient.Default) instance;
        assertNotNull(client);
    }
}
