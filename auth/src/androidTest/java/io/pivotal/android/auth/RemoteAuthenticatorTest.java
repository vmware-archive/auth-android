/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;

import java.util.Properties;
import java.util.UUID;

public class RemoteAuthenticatorTest extends AndroidTestCase {

    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final String CLIENT_SECRET = UUID.randomUUID().toString();
    private static final String TOKEN_URL = "http://" + UUID.randomUUID().toString() + ".com";
    private static final String AUTHORIZE_URL = "http://" + UUID.randomUUID().toString() + ".com";
    private static final String REDIRECT_URL = "http://" + UUID.randomUUID().toString() + ".com";
    private static final String SCOPE = UUID.randomUUID().toString();

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();
    private static final String AUTH_CODE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());

        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.clientId", CLIENT_ID);
        properties.setProperty("pivotal.auth.clientSecret", CLIENT_SECRET);
        properties.setProperty("pivotal.auth.tokenUrl", TOKEN_URL);
        properties.setProperty("pivotal.auth.authorizeUrl", AUTHORIZE_URL);
        properties.setProperty("pivotal.auth.redirectUrl", REDIRECT_URL);
        properties.setProperty("pivotal.auth.scopes", SCOPE);

        Pivotal.setProperties(properties);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        Pivotal.setProperties(null);
    }

    public void testCreateNewPasswordTokenRequest() throws Exception {
        final RemoteAuthenticator.Default provider = new RemoteAuthenticator.Default();
        final PasswordTokenRequest request = provider.newPasswordTokenRequest(USERNAME, PASSWORD);

        assertEquals(CLIENT_ID, request.get("client_id"));
        assertEquals(CLIENT_SECRET, request.get("client_secret"));

        assertEquals(USERNAME, request.getUsername());
        assertEquals(PASSWORD, request.getPassword());
        assertEquals(SCOPE, request.getScopes());
    }

    public void testCreateNewRefreshTokenRequest() throws Exception {
        final RemoteAuthenticator.Default provider = new RemoteAuthenticator.Default();
        final RefreshTokenRequest request = provider.newRefreshTokenRequest(REFRESH_TOKEN);

        assertEquals(CLIENT_ID, request.get("client_id"));
        assertEquals(CLIENT_SECRET, request.get("client_secret"));

        assertEquals(REFRESH_TOKEN, request.getRefreshToken());
    }

    public void testCreateNewAuthorizationCodeTokenRequest() throws Exception {
        final RemoteAuthenticator provider = new RemoteAuthenticator.Default();
        final AuthorizationCodeTokenRequest request = provider.newAuthorizationCodeTokenRequest(AUTH_CODE);

        assertEquals(CLIENT_ID, request.get("client_id"));
        assertEquals(CLIENT_SECRET, request.get("client_secret"));

        assertEquals(AUTH_CODE, request.getCode());
        assertEquals(REDIRECT_URL, request.getRedirectUri());
    }

    public void testCreateNewAuthorizationCodeRequestUrl() throws Exception {
        final RemoteAuthenticator provider = new RemoteAuthenticator.Default();
        final AuthorizationCodeRequestUrl url = provider.newAuthorizationCodeUrl();

        assertEquals(REDIRECT_URL, url.getRedirectUri());
        assertEquals(SCOPE, url.getScopes());
        assertEquals("offline", url.get("access_type"));
    }
}