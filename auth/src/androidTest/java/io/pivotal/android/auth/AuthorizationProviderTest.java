/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;
import android.text.TextUtils;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;

public class AuthorizationProviderTest extends AndroidTestCase {

    public void testCreateNewPasswordTokenRequest() throws Exception {
        final AuthorizationProvider.Default provider = new AuthorizationProvider.Default();
        final PasswordTokenRequest request = provider.newPasswordTokenRequest("username", "password");

        assertEquals("username", request.getUsername());
        assertEquals("password", request.getPassword());
        assertEquals(provider.getTokenServerEncodedUrl(), request.getTokenServerUrl().build());
        assertEquals(provider.getTransport(), request.getTransport());
        assertEquals(provider.getJsonFactory(), request.getJsonFactory());
        assertEquals(provider.getClientAuthentication(), request.getClientAuthentication());
        assertEquals(provider.getRequestInitializer(), request.getRequestInitializer());
        assertEquals(TextUtils.join(" ", provider.getScopes()), request.getScopes());
    }

    public void testCreateNewRefreshTokenRequest() throws Exception {
        final AuthorizationProvider.Default provider = new AuthorizationProvider.Default();
        final RefreshTokenRequest request = provider.newRefreshTokenRequest("refresh");

        assertEquals("refresh", request.getRefreshToken());
        assertEquals(provider.getTokenServerEncodedUrl(), request.getTokenServerUrl().build());
        assertEquals(provider.getTransport(), request.getTransport());
        assertEquals(provider.getJsonFactory(), request.getJsonFactory());
        assertEquals(provider.getClientAuthentication(), request.getClientAuthentication());
        assertEquals(provider.getRequestInitializer(), request.getRequestInitializer());
        assertEquals(TextUtils.join(" ", provider.getScopes()), request.getScopes());
    }

    public void testCreateNewAuthorizationCodeTokenRequest() throws Exception {
        final AuthorizationProvider provider = new AuthorizationProvider.Default();
        final AuthorizationCodeTokenRequest request = provider.newTokenRequest("");

        assertEquals(Pivotal.get(Pivotal.PROP_REDIRECT_URL), request.getRedirectUri());
    }

    public void testCreateNewAuthorizationCodeRequestUrl() throws Exception {
        final AuthorizationProvider provider = new AuthorizationProvider.Default();
        final AuthorizationCodeRequestUrl url = provider.newAuthorizationUrl();

        assertEquals(Pivotal.get(Pivotal.PROP_REDIRECT_URL), url.getRedirectUri());
    }
}