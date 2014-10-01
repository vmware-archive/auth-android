/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import java.util.Properties;

public class PivotalTest extends AndroidTestCase {

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
        Pivotal.setProperties(null);
        assertEquals("test_client_id", Pivotal.getClientId());
    }

    public void testGetClientSecret() {
        Pivotal.setProperties(null);
        assertEquals("test_client_secret", Pivotal.getClientSecret());
    }

    public void testGetAuthorizeUrl() {
        Pivotal.setProperties(null);
        assertEquals("http://example.com/authorize", Pivotal.getAuthorizeUrl());
    }

    public void testGetTokenUrl() {
        Pivotal.setProperties(null);
        assertEquals("http://example.com/token", Pivotal.getTokenUrl());
    }

    public void testGetRedirectUrl() {
        Pivotal.setProperties(null);
        assertEquals("http://example.com/redirect", Pivotal.getRedirectUrl());
    }

    public void testAccountType() {
        Pivotal.setProperties(null);
        assertEquals("test_account_type", Pivotal.getAccountType());
    }

    public void testTokenType() {
        Pivotal.setProperties(null);
        assertEquals("test_token_type", Pivotal.getTokenType());
    }
}