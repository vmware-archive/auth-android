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

    public String getAccessToken(Account account);

    public String getAccessTokenOrThrow(Account account) throws Exception;

    public void getAccessToken(Activity activity, Auth.Listener listener);

    public void getAccessToken(Activity activity, Account account, Auth.Listener listener);

    public void getAccessToken(Account account, boolean promptUser, Auth.Listener listener);

    public void setAccessToken(Account account, String accessToken);

    public void invalidateAccessToken(String accessToken);

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
        public String getAccessToken(final Account account) {
            return mManager.peekAuthToken(account, Pivotal.getTokenType());
        }

        @Override
        public String getAccessTokenOrThrow(final Account account) throws Exception {
            return mManager.blockingGetAuthToken(account, Pivotal.getTokenType(), false);
        }

        @Override
        public void getAccessToken(final Activity activity, final Auth.Listener listener) {
            final ListenerExpirationCallback callback = new ListenerExpirationCallback(activity, listener);
            mManager.getAuthTokenByFeatures(Pivotal.getAccountType(), Pivotal.getTokenType(), null, activity, null, null, callback, null);
        }

        @Override
        public void getAccessToken(final Activity activity, final Account account, final Auth.Listener listener) {
            final ListenerCallback callback = new ListenerCallback(listener);
            mManager.getAuthToken(account, Pivotal.getTokenType(), null, activity, callback, null);
        }

        @Override
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public void getAccessToken(final Account account, final boolean promptUser, final Auth.Listener listener) {
            final ListenerCallback callback = new ListenerCallback(listener);
            mManager.getAuthToken(account, Pivotal.getTokenType(), null, promptUser, callback, null);
        }

        @Override
        public void setAccessToken(final Account account, final String token) {
            mManager.setAuthToken(account, Pivotal.getTokenType(), token);
        }

        @Override
        public void invalidateAccessToken(final String token) {
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
