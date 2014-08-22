/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */

package io.pivotal.android.auth;

import android.test.AndroidTestCase;

public class AuthorizationParamsTest extends AndroidTestCase {

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";
    private static final String TEST_AUTHORIZATION_URL = "http://test.authorization.url";
    private static final String TEST_REDIRECT_URL = "http://test.redirect.url";

    public void testAllParametersNull() {
        final AuthorizationParams parameters = new AuthorizationParams(null, null, null, null);
        assertNull(parameters.getClientId());
        assertNull(parameters.getClientSecret());
        assertNull(parameters.getAuthorizationUrl());
        assertNull(parameters.getRedirectUrl());
    }

    public void testAllParametersSet() {
        final AuthorizationParams parameters = new AuthorizationParams(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL, TEST_REDIRECT_URL);
        assertEquals(TEST_CLIENT_ID, parameters.getClientId());
        assertEquals(TEST_CLIENT_SECRET, parameters.getClientSecret());
        assertEquals(TEST_AUTHORIZATION_URL, parameters.getAuthorizationUrl());
        assertEquals(TEST_REDIRECT_URL, parameters.getRedirectUrl());
    }

    public void testWhitespaceTrimmed() {
        final AuthorizationParams parameters = new AuthorizationParams(" " + TEST_CLIENT_ID + " ", "\t" + TEST_CLIENT_SECRET + "\t", "\n" + TEST_AUTHORIZATION_URL + "\n", "  " + TEST_REDIRECT_URL + "\r\n");
        assertEquals(TEST_CLIENT_ID, parameters.getClientId());
        assertEquals(TEST_CLIENT_SECRET, parameters.getClientSecret());
        assertEquals(TEST_AUTHORIZATION_URL, parameters.getAuthorizationUrl());
        assertEquals(TEST_REDIRECT_URL, parameters.getRedirectUrl());
    }

    public void testTrailingSlashesStrippedFromPaths() {
        final AuthorizationParams parameters = new AuthorizationParams(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL + "/", TEST_REDIRECT_URL + "/" + "/");
        assertEquals(TEST_AUTHORIZATION_URL, parameters.getAuthorizationUrl());
        assertEquals(TEST_REDIRECT_URL, parameters.getRedirectUrl());
    }
}
