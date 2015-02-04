/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;

import java.util.UUID;

public class RemoteAuthenticatorTest extends AndroidTestCase {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();
    private static final String AUTH_CODE = UUID.randomUUID().toString();


    public void testCreateNewPasswordTokenRequest() throws Exception {
        final RemoteAuthenticator.Default provider = new RemoteAuthenticator.Default();
        final PasswordTokenRequest request = provider.newPasswordTokenRequest(USERNAME, PASSWORD);

        assertEquals(USERNAME, request.getUsername());
        assertEquals(PASSWORD, request.getPassword());
    }

    public void testCreateNewRefreshTokenRequest() throws Exception {
        final RemoteAuthenticator.Default provider = new RemoteAuthenticator.Default();
        final RefreshTokenRequest request = provider.newRefreshTokenRequest(REFRESH_TOKEN);

        assertEquals(REFRESH_TOKEN, request.getRefreshToken());
    }

    public void testCreateNewAuthorizationCodeTokenRequest() throws Exception {
        final RemoteAuthenticator provider = new RemoteAuthenticator.Default();
        final AuthorizationCodeTokenRequest request = provider.newAuthorizationCodeTokenRequest(AUTH_CODE);

        assertEquals(AUTH_CODE, request.getCode());
        assertEquals(Pivotal.getRedirectUrl(), request.getRedirectUri());
    }

    public void testCreateNewAuthorizationCodeRequestUrl() throws Exception {
        final RemoteAuthenticator provider = new RemoteAuthenticator.Default();
        final AuthorizationCodeRequestUrl url = provider.newAuthorizationCodeUrl();

        assertEquals(Pivotal.getRedirectUrl(), url.getRedirectUri());
    }
}