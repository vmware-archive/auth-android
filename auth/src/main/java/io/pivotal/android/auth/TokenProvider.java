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

/* package */ interface TokenProvider {
    public String getRefreshToken(Account account);

    public String getAuthToken(Account account);

    public String getAuthTokenOrThrow(Account account) throws Exception;

    public void getAuthToken(Activity activity, Authorization.Listener listener);

    public void getAuthToken(Account account, Authorization.Listener listener);

    public void setAuthToken(Account account, String accessToken);

    public void invalidateAuthToken(String accessToken);

    public void addAccount(Account account, String refreshToken);

    public void removeAccount(Account account);

    public Account[] getAccounts();

    /* package */ class Default implements TokenProvider {

        private AccountManager mManager;

        public Default(final Context context) {
            mManager = AccountManager.get(context);
        }

        @Override
        public String getRefreshToken(final Account account) {
            return mManager.getPassword(account);
        }

        @Override
        public String getAuthToken(final Account account) {
            return mManager.peekAuthToken(account, Pivotal.getTokenType());
        }

        @Override
        public String getAuthTokenOrThrow(final Account account) throws Exception {
            return mManager.blockingGetAuthToken(account, Pivotal.getTokenType(), false);
        }

        @Override
        public void getAuthToken(final Activity activity, final Authorization.Listener listener) {
            final ListenerExpirationCallback callback = new ListenerExpirationCallback(activity, listener);
            mManager.getAuthTokenByFeatures(Pivotal.getAccountType(), Pivotal.getTokenType(), null, activity, null, null, callback, null);
        }

        @Override
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void getAuthToken(final Account account, final Authorization.Listener listener) {
            final ListenerCallback callback = new ListenerCallback(listener);
            mManager.getAuthToken(account, Pivotal.getTokenType(), null, false, callback, null);
        }

        @Override
        public void setAuthToken(final Account account, final String accessToken) {
            mManager.setAuthToken(account, Pivotal.getTokenType(), accessToken);
        }

        @Override
        public void invalidateAuthToken(final String token) {
            mManager.invalidateAuthToken(Pivotal.getAccountType(), token);
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
            return mManager.getAccountsByType(Pivotal.getAccountType());
        }
    }
}
