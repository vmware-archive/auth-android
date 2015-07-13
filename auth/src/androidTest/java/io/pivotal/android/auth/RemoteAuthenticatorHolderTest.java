/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.Properties;
import java.util.UUID;

public class RemoteAuthenticatorHolderTest extends AndroidTestCase {

    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final String CLIENT_SECRET = UUID.randomUUID().toString();
    private static final String TOKEN_URL = "http://" + UUID.randomUUID().toString() + ".com";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        Pivotal.setProperties(null);
        RemoteAuthenticatorHolder.init(null);
    }

    public void testWithInitialization() {
        final RemoteAuthenticator custom = Mockito.mock(RemoteAuthenticator.class);
        RemoteAuthenticatorHolder.init(custom);
        final RemoteAuthenticator authenticator = RemoteAuthenticatorHolder.get(mContext);
        assertEquals(custom, authenticator);
    }

    public void testWithoutInitialization() {
        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.clientId", CLIENT_ID);
        properties.setProperty("pivotal.auth.clientSecret", CLIENT_SECRET);
        properties.setProperty("pivotal.auth.tokenUrl", TOKEN_URL);
        Pivotal.setProperties(properties);

        RemoteAuthenticatorHolder.init(null);
        final RemoteAuthenticator instance = RemoteAuthenticatorHolder.get(mContext);
        final RemoteAuthenticator.Default authenticator = (RemoteAuthenticator.Default) instance;
        assertNotNull(authenticator);
    }
}
