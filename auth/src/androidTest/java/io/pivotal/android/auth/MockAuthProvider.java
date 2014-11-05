/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;

public class MockAuthProvider implements AuthProvider {

    @Override
    public PasswordTokenRequest newPasswordTokenRequest(String username, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RefreshTokenRequest newRefreshTokenRequest(String refreshToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(String authorizationCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthorizationCodeRequestUrl newAuthorizationCodeUrl() {
        throw new UnsupportedOperationException();
    }
}
