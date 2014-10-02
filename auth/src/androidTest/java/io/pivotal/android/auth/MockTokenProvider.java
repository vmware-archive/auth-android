/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;

public class MockTokenProvider implements TokenProvider {

    @Override
    public String getRefreshToken(final Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthToken(final Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenOrThrow(final Account account) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAuthToken(final Activity activity, final Authorization.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAuthToken(final Account account, final Authorization.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthToken(final Account account, final String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAuthToken(final String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addAccount(final Account account, final String refreshToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAccount(final Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Account[] getAccounts() {
        throw new UnsupportedOperationException();
    }
}
