/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import java.util.Properties;
import java.util.UUID;

public class PivotalTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());

        Pivotal.setProperties(null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        Pivotal.setProperties(null);
    }

    public void testGetSucceeds() {
        final String key = "key";
        final String value = "value";

        final Properties properties = new Properties();
        properties.setProperty(key, value);

        Pivotal.setProperties(properties);
        assertEquals(value, Pivotal.get(key));
    }

    public void testGetFails() {
        final String key = "key";
        final String value = "value";

        final Properties properties = new Properties();

        Pivotal.setProperties(properties);

        try {
            assertEquals(value, Pivotal.get(key));
            fail();
        } catch (final IllegalStateException e) {
            assertNotNull(e);
        }
    }

    public void testGetClientId() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.clientId", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getClientId());
    }

    public void testGetClientSecret() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.clientSecret", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getClientSecret());
    }

    public void testGetAuthorizeUrl() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.authorizeUrl", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getAuthorizeUrl());
    }

    public void testGetTokenUrl() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.tokenUrl", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getTokenUrl());
    }

    public void testGetRedirectUrl() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.redirectUrl", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getRedirectUrl());
    }

    public void testAccountType() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.accountType", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getAccountType());
    }

    public void testTokenType() {
        final String value = UUID.randomUUID().toString();

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.tokenType", value);
        Pivotal.setProperties(properties);

        assertEquals(value, Pivotal.getTokenType());
    }
}