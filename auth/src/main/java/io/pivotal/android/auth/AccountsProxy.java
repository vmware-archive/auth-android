/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;

/* package */ interface AccountsProxy {

    public String getRefreshToken(Account account);

    public String getAccessToken(Account account);

    public void setAccessToken(Account account, String accessToken);

    public void invalidateAccessToken(String accessToken);

    public void addAccount(Account account, String refreshToken);

    public void removeAccount(Account account);

    public Account[] getAccounts();

    public AccountManagerFuture<Bundle> getAuthTokenByFeatures(Activity activity);

    public AccountManagerFuture<Bundle> getAuthToken(Activity activity, Account account);

    public AccountManagerFuture<Bundle> getAuthToken(Account account);

    public void addOnAccountsUpdatedListener(AccountsChangedListener listener);

    public void removeOnAccountsUpdatedListener(AccountsChangedListener listener);

    public void clearCookies();

    /* package */ class Default implements AccountsProxy {

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

        @Override
        public AccountManagerFuture<Bundle> getAuthTokenByFeatures(final Activity activity) {
            return mManager.getAuthTokenByFeatures(Pivotal.getAccountType(), Pivotal.getTokenType(), null, activity, null, null, null, null);
        }

        @Override
        public AccountManagerFuture<Bundle> getAuthToken(final Activity activity, final Account account) {
            return mManager.getAuthToken(account, Pivotal.getTokenType(), null, activity, null, null);
        }

        @Override
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public AccountManagerFuture<Bundle> getAuthToken(final Account account) {
            return mManager.getAuthToken(account, Pivotal.getTokenType(), null, false, null, null);
        }

        @Override
        public void addOnAccountsUpdatedListener(AccountsChangedListener listener) {
            mManager.addOnAccountsUpdatedListener(listener, null, true);
        }

        @Override
        public void removeOnAccountsUpdatedListener(AccountsChangedListener listener) {
            mManager.removeOnAccountsUpdatedListener(listener);
        }

        @Override
        @SuppressWarnings("deprecation")
        public void clearCookies() {
            CookieManager.getInstance().removeAllCookie();
        }
    }
}
