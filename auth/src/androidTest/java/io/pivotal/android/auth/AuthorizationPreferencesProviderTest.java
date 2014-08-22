/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

public class AuthorizationPreferencesProviderTest extends AndroidTestCase {

    private static final String CLIENT_SECRET = "TEST_CLIENT_SECRET";
    private static final String CLIENT_ID = "TEST_CLIENT_ID";

    private static final String REDIRECT_URL = "https://test.redirect.url";
    private static final String AUTHORIZATION_URL = "https://test.authorization.url";
    private static final String TOKEN_URL = "https://test.token.url";

    @Override
    protected void setUp() throws Exception {
        final AuthorizationPreferencesProviderImpl prefs = getPrefs();
        prefs.clear();
    }

    public void testReset() {
        final AuthorizationPreferencesProviderImpl prefs = getPrefs();
        prefs.clear();
        assertNull(prefs.getClientId());
        assertNull(prefs.getAuthorizationUrl());
        assertNull(prefs.getRedirectUrl());
    }

    public void testSetClientId() {
        final AuthorizationPreferencesProvider prefs1 = getPrefs();
        prefs1.setClientId(CLIENT_ID);
        assertEquals(CLIENT_ID, prefs1.getClientId());
        final AuthorizationPreferencesProvider prefs2 = getPrefs();
        assertEquals(CLIENT_ID, prefs2.getClientId());
    }

    public void testSetAuthorizationUrl() throws Exception {
        final AuthorizationPreferencesProvider prefs1 = getPrefs();
        prefs1.setAuthorizationUrl(AUTHORIZATION_URL);
        assertEquals(AUTHORIZATION_URL, prefs1.getAuthorizationUrl());
        final AuthorizationPreferencesProvider prefs2 = getPrefs();
        assertEquals(AUTHORIZATION_URL, prefs2.getAuthorizationUrl());
    }

    public void testSetRedirectUrl() throws Exception {
        final AuthorizationPreferencesProvider prefs1 = getPrefs();
        prefs1.setRedirectUrl(REDIRECT_URL);
        assertEquals(REDIRECT_URL, prefs1.getRedirectUrl());
        final AuthorizationPreferencesProvider prefs2 = getPrefs();
        assertEquals(REDIRECT_URL, prefs2.getRedirectUrl());
    }

    private AuthorizationPreferencesProviderImpl getPrefs() {
        return new AuthorizationPreferencesProviderImpl(getContext());
    }
}
