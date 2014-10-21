/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

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
    }

    public void testCreateNewRefreshTokenRequest() throws Exception {
        final AuthorizationProvider.Default provider = new AuthorizationProvider.Default();
        final RefreshTokenRequest request = provider.newRefreshTokenRequest("refresh");

        assertEquals("refresh", request.getRefreshToken());
    }

    public void testCreateNewAuthorizationCodeTokenRequest() throws Exception {
        final AuthorizationProvider provider = new AuthorizationProvider.Default();
        final AuthorizationCodeTokenRequest request = provider.newAuthorizationCodeTokenRequest("");

        assertEquals(Pivotal.getRedirectUrl(), request.getRedirectUri());
    }

    public void testCreateNewAuthorizationCodeRequestUrl() throws Exception {
        final AuthorizationProvider provider = new AuthorizationProvider.Default();
        final AuthorizationCodeRequestUrl url = provider.newAuthorizationCodeUrl();

        assertEquals(Pivotal.getRedirectUrl(), url.getRedirectUri());
    }
}