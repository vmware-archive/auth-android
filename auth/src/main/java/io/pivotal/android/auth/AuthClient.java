/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

/* package */ interface AuthClient {

    public Auth.Response requestAccessToken(Activity activity);

    public void requestAccessToken(Activity activity, Auth.Listener listener);

    public Auth.Response requestAccessToken(Context context);

    public void requestAccessToken(Context context, Auth.Listener listener);

    public Auth.Response requestAccessToken(Account account);

    public void requestAccessToken(Account account, Auth.Listener listener);


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    /* package */ class Default implements AuthClient {

        private static final String NO_TOKEN_FOUND = "No access token found.";

        private final AccountsProxy mProxy;

        public Default(final Context context) {
            mProxy = AccountsProxyFactory.get(context);
        }

        public Default(final AccountsProxy proxy) {
            mProxy = proxy;
        }

        @Override
        public Auth.Response requestAccessToken(final Activity activity) {
            final AccountManagerFuture<Bundle> future = mProxy.getAuthTokenByFeatures(activity, null);
            return handleFuture(activity, future);
        }

        @Override
        public void requestAccessToken(final Activity activity, final Auth.Listener listener) {
            mProxy.getAuthTokenByFeatures(activity, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    final Auth.Response response = handleFuture(activity, future);
                    listener.onResponse(response);
                }
            });
        }

        @Override
        public Auth.Response requestAccessToken(final Context context) {

            final Account account = getLastUsedAccount(context);
            if (account == null) {
                return getFailureAuthResponse(new Exception(NO_TOKEN_FOUND));
            }

            final AccountManagerFuture<Bundle> future = mProxy.getAuthToken(account, null);
            return handleFuture(context, future);
        }

        @Override
        public void requestAccessToken(final Context context, final Auth.Listener listener) {

            final Account account = getLastUsedAccount(context);
            if (account == null) {
                listener.onResponse(getFailureAuthResponse(new Exception(NO_TOKEN_FOUND)));
                return;
            }

            mProxy.getAuthToken(account, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    final Auth.Response response = handleFuture(context, future);
                    listener.onResponse(response);
                }
            });
        }

        @Override
        public Auth.Response requestAccessToken(final Account account) {
            final AccountManagerFuture<Bundle> future = mProxy.getAuthToken(account, null);
            return validateAccountBundle(future);
        }

        @Override
        public void requestAccessToken(final Account account, final Auth.Listener listener) {
            mProxy.getAuthToken(account, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    final Auth.Response response = validateAccountBundle(future);
                    listener.onResponse(response);
                }
            });
        }

        protected Auth.Response handleFuture(final Context context, final AccountManagerFuture<Bundle> future) {
            final Auth.Response response = validateAccountBundle(future);

            if (response.isSuccess()) {
                setLastUsedAccountName(context, response.accountName);
            }

            if (response.isSuccess() && response.isTokenExpired()) {
                Logger.i("requestAccessToken expired.");
                mProxy.invalidateAccessToken(response.accessToken);

                final Account account = getAccount(context, response.accountName);
                return requestAccessToken(account);
            } else {
                return response;
            }
        }

        protected Auth.Response validateAccountBundle(final AccountManagerFuture<Bundle> future) {
            try {

                final Bundle bundle = future.getResult();
                final String accessToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                Logger.i("requestAccessToken accountName: " + accountName);
                Logger.i("requestAccessToken accessToken: " + accessToken);

                if (accessToken == null) {
                    return getFailureAuthResponse(new Exception(NO_TOKEN_FOUND));

                } else {
                    return new Auth.Response(accessToken, accountName);
                }

            } catch (final Exception e) {
                return getFailureAuthResponse(e);
            }
        }

        protected Account getLastUsedAccount(final Context context) {
            return Accounts.getLastUsedAccount(context);
        }

        protected Account getAccount(final Context context, final String name) {
            return Accounts.getAccount(context, name);
        }

        protected void setLastUsedAccountName(final Context context, final String name) {
            AuthPreferences.setLastUsedAccountName(context, name);
        }

        protected Auth.Response getFailureAuthResponse(final Exception e) {
            Logger.i("requestAccessToken error: " + e.getCause());

            return new Auth.Response(new AuthError(e));
        }

    }
}
