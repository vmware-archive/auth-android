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

    public Auth.Response requestAccessToken(Context context);

    public void requestAccessToken(Context context, Auth.Listener listener);

    public Auth.Response requestAccessToken(Context context, Account account);

    public void requestAccessToken(Context context, Account account, Auth.Listener listener);


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
        public Auth.Response requestAccessToken(final Context context) {
            if (context instanceof Activity) {
                return requestAccessTokenFromActivity((Activity) context);
            } else {
                return requestAccessTokenFromContext(context);
            }
        }

        @Override
        public void requestAccessToken(final Context context, final Auth.Listener listener) {
            if (context instanceof Activity) {
                requestAccessTokenFromActivity((Activity) context, listener);
            } else {
                requestAccessTokenFromContext(context, listener);
            }
        }

        @Override
        public Auth.Response requestAccessToken(final Context context, final Account account) {
            if (context instanceof Activity) {
                return retrieveAccessTokenFromActivity((Activity) context, account);
            } else {
                return retrieveAccessTokenFromContext(context, account);
            }
        }

        @Override
        public void requestAccessToken(final Context context, final Account account, final Auth.Listener listener) {
            if (context instanceof Activity) {
                retrieveAccessTokenFromActivity((Activity) context, account, listener);
            } else {
                retrieveAccessTokenFromContext(context, account, listener);
            }
        }

        private Auth.Response requestAccessTokenFromActivity(final Activity activity) {
            final AccountManagerFuture<Bundle> future = mProxy.getAuthTokenByFeatures(activity, null);
            return validateTokenInFuture(activity, future);
        }

        private Auth.Response retrieveAccessTokenFromActivity(final Activity activity, final Account account) {
            final AccountManagerFuture<Bundle> future = mProxy.getAuthToken(activity, account, null);
            return validateTokenInFuture(activity, future);
        }

        private Auth.Response requestAccessTokenFromContext(final Context context) {
            final Account account = getLastUsedAccount(context);
            if (account == null) {
                return getFailureAuthResponse(new Exception(NO_TOKEN_FOUND));
            }

            final AccountManagerFuture<Bundle> future = mProxy.getAuthToken(account, null);
            return validateTokenInFuture(context, future);
        }

        private Auth.Response retrieveAccessTokenFromContext(final Context context, final Account account) {
            final AccountManagerFuture<Bundle> future = mProxy.getAuthToken(account, null);
            return validateTokenInFuture(context, future);
        }




        private void requestAccessTokenFromActivity(final Activity activity, final Auth.Listener listener) {
            mProxy.getAuthTokenByFeatures(activity, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    validateTokenInFuture(activity, future, listener);
                }
            });
        }

        private void retrieveAccessTokenFromActivity(final Activity activity, final Account account, final Auth.Listener listener) {
            mProxy.getAuthToken(activity, account, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    validateTokenInFuture(activity, future, listener);
                }
            });
        }

        private void requestAccessTokenFromContext(final Context context, final Auth.Listener listener) {
            final Account account = getLastUsedAccount(context);
            if (account == null) {
                listener.onResponse(getFailureAuthResponse(new Exception(NO_TOKEN_FOUND)));
                return;
            }

            mProxy.getAuthToken(account, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    validateTokenInFuture(context, future, listener);
                }
            });
        }

        private void retrieveAccessTokenFromContext(final Context context, final Account account, final Auth.Listener listener) {
            mProxy.getAuthToken(account, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(final AccountManagerFuture<Bundle> future) {
                    validateTokenInFuture(context, future, listener);
                }
            });
        }


        protected Auth.Response validateTokenInFuture(final Context context, final AccountManagerFuture<Bundle> future) {
            final Auth.Response response = retrieveResponseFromFuture(future);

            if (response.isSuccess()) {
                setLastUsedAccountName(context, response.accountName);
            }

            if (response.isSuccess() && response.isTokenExpired()) {
                Logger.i("requestAccessToken expired.");
                mProxy.invalidateAccessToken(response.accessToken);

                final Account account = getAccount(context, response.accountName);
                return requestAccessToken(context, account);
            } else {
                return response;
            }
        }

        protected void validateTokenInFuture(final Context context, final AccountManagerFuture<Bundle> future, final Auth.Listener listener) {
            final Auth.Response response = retrieveResponseFromFuture(future);

            if (response.isSuccess()) {
                setLastUsedAccountName(context, response.accountName);
            }

            if (response.isSuccess() && response.isTokenExpired()) {
                Logger.i("requestAccessToken expired.");
                mProxy.invalidateAccessToken(response.accessToken);

                final Account account = getAccount(context, response.accountName);
                requestAccessToken(context, account, listener);
            } else {
                listener.onResponse(response);
            }
        }

        protected Auth.Response retrieveResponseFromFuture(final AccountManagerFuture<Bundle> future) {
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
