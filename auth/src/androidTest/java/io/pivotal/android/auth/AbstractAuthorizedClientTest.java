/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;

import java.util.concurrent.Semaphore;

public abstract class AbstractAuthorizedClientTest<T extends AbstractAuthorizationClient> extends AndroidTestCase {

    protected static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";
    protected static final String TEST_CLIENT_SECRET_2 = "TEST_CLIENT_SECRET_2";
    protected static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    protected static final String TEST_CLIENT_ID_2 = "TEST_CLIENT_ID_2";

    protected static final String TEST_REDIRECT_URL = "https://test.redirect.url";
    protected static final String TEST_REDIRECT_URL_2 = "https://test.redirect.url.2";
    protected static final String TEST_AUTHORIZATION_URL = "https://test.authorization.url";
    protected static final String TEST_AUTHORIZATION_URL_2 = "https://test.authorization.url.2";

    protected FakeApiProvider apiProvider;
    protected FakeAuthorizationPreferences preferences;
    protected AuthorizationParams parameters;
    protected Semaphore semaphore;
    protected Credential credential;

    protected abstract T construct(AuthorizationPreferencesProvider preferencesProvider,
                                   ApiProvider apiProvider);

    @Override
    protected void setUp() throws Exception {
        preferences = new FakeAuthorizationPreferences();
        apiProvider = new FakeApiProvider();
        parameters = new AuthorizationParams(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL, TEST_REDIRECT_URL);
        credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
        semaphore = new Semaphore(0);
    }

    protected void savePreferences() {
        preferences.setClientId(TEST_CLIENT_ID);
        preferences.setClientSecret(TEST_CLIENT_SECRET);
        preferences.setAuthorizationUrl(TEST_AUTHORIZATION_URL);
        preferences.setRedirectUrl(TEST_REDIRECT_URL);
    }

    public void testRequiresAuthorizationPreferencesProvider() {
        try {
            construct(null, apiProvider);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testRequiresHttpRequestFactoryProvider() {
        try {
            construct(preferences, null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    protected void saveCredential() {
        apiProvider.setCredential(credential);
    }

    protected void assertCredentialEquals(final Credential expectedCredential, FakeApiProvider apiProvider) {
        assertEquals(expectedCredential, apiProvider.getCredential());
    }
}
