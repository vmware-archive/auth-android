/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

/* package */ class TokenManager implements TokenProvider {

    private AccountManager mManager;

    public TokenManager(final Context context) {
        mManager = AccountManager.get(context);
    }

    @Override
    public String getRefreshToken(final Account account) {
        return mManager.getPassword(account);
    }

    @Override
    public String getAuthToken(final Account account) {
        return mManager.peekAuthToken(account, Pivotal.get(Pivotal.PROP_TOKEN_TYPE));
    }

    @Override
    public String getAuthTokenOrThrow(final Account account) throws Exception {
        return mManager.blockingGetAuthToken(account, Pivotal.get(Pivotal.PROP_TOKEN_TYPE), false);
    }

    @Override
    public void getAuthToken(final Activity activity, final Authorization.Listener listener) {
        final ListenerExpirationCallback callback = new ListenerExpirationCallback(activity, listener);
        mManager.getAuthTokenByFeatures(Pivotal.get(Pivotal.PROP_ACCOUNT_TYPE), Pivotal.get(Pivotal.PROP_TOKEN_TYPE), null, activity, null, null, callback, null);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void getAuthToken(final Account account, final Authorization.Listener listener) {
        final ListenerCallback callback = new ListenerCallback(listener);
        mManager.getAuthToken(account, Pivotal.get(Pivotal.PROP_TOKEN_TYPE), null, false, callback, null);
    }

    @Override
    public void setAuthToken(final Account account, final String accessToken) {
        mManager.setAuthToken(account, Pivotal.get(Pivotal.PROP_TOKEN_TYPE), accessToken);
    }

    @Override
    public void invalidateAuthToken(final String token) {
        mManager.invalidateAuthToken(Pivotal.get(Pivotal.PROP_ACCOUNT_TYPE), token);
    }

    @Override
    public void addAccount(final Account account, final String refreshToken) {
        mManager.addAccountExplicitly(account, refreshToken, null);
    }

    @Override
    public void removeAccount(final Account account) {
        mManager.removeAccount(account, null, null);
    }

    @Override
    public Account[] getAccounts() {
        return mManager.getAccountsByType(Pivotal.get(Pivotal.PROP_ACCOUNT_TYPE));
    }
}
