/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;

public class MockTokenProvider implements TokenProvider {

    @Override
    public String getRefreshToken(final Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAccessToken(final Account account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAccessTokenOrThrow(final Account account) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAccessToken(final Activity activity, final Auth.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAccessToken(final Activity activity, final Account account, final Auth.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAccessToken(final Context context, final Account account, final boolean promptUser, final Auth.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getAccessToken(final Account account, final boolean promptUser, final Auth.Listener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAccessToken(final Account account, final String accessToken) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void invalidateAccessToken(final String accessToken) {
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
