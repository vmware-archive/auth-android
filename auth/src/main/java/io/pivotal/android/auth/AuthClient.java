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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

/* package */ interface AuthClient {

    public Response requestAccessToken(Context context);

    public void requestAccessToken(Context context, Auth.Listener listener);

    public Response requestAccessToken(Context context, Account account, boolean validate);

    public void requestAccessToken(Context context, Account account, boolean validate, Auth.Listener listener);

    public void setShouldShowUserPrompt(boolean enabled);


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    /* package */ class Default implements AuthClient {

        private static final String NO_TOKEN_FOUND = "No access token found.";

        private final AccountsProxy mProxy;
        private boolean mDisableUserPrompt;

        public Default(final Context context) {
            mProxy = AccountsProxyHolder.get(context);
        }

        public Default(final AccountsProxy proxy) {
            mProxy = proxy;
        }

        @Override
        public Response requestAccessToken(final Context context) {
            if (context instanceof Activity && !mDisableUserPrompt) {

                final AccountManagerFuture<Bundle> future = mProxy.getAuthTokenByFeatures((Activity) context);
                return validateTokenInFuture(context, future);

            } else {

                final Account account = getAccount(context);
                if (account == null) {
                    return getFailureAuthResponse(new Exception(NO_TOKEN_FOUND));
                }

                final AccountManagerFuture<Bundle> future = mProxy.getAuthToken(account);
                return validateTokenInFuture(context, future);
            }
        }

        @Override
        public void requestAccessToken(final Context context, final Auth.Listener listener) {
            new AsyncTask<Void, Void, Response>() {

                @Override
                protected Response doInBackground(final Void... params) {
                    return requestAccessToken(context);
                }

                @Override
                protected void onPostExecute(final Response response) {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                }

            }.execute();
        }

        @Override
        public Response requestAccessToken(final Context context, final Account account, final boolean validate) {
            final AccountManagerFuture<Bundle> future;

            if (context instanceof Activity && !mDisableUserPrompt) {
                future = mProxy.getAuthToken((Activity) context, account);
            } else {
                future = mProxy.getAuthToken(account);
            }

            if (validate) {
                return validateTokenInFuture(context, future);
            } else {
                return retrieveResponseFromFuture(future);
            }
        }

        @Override
        public void requestAccessToken(final Context context, final Account account, final boolean validate, final Auth.Listener listener) {
            new AsyncTask<Void, Void, Response>() {

                @Override
                protected Response doInBackground(final Void... params) {
                    return requestAccessToken(context, account, validate);
                }

                @Override
                protected void onPostExecute(final Response response) {
                    if (listener != null) {
                        listener.onResponse(response);
                    }
                }

            }.execute();
        }

        protected Response validateTokenInFuture(final Context context, final AccountManagerFuture<Bundle> future) {
            final Response response = retrieveResponseFromFuture(future);

            if (response.isSuccess()) {
                setAccountName(context, response.accountName);
            }

            if (response.isSuccess() && response.isTokenExpired()) {
                Logger.i("requested access token expired.");

                mProxy.invalidateAccessToken(response.accessToken);

                Logger.i("requested access token invalidated.");

                final Account account = getAccount(context);

                Logger.i("requested access token retry.");

                return requestAccessToken(context, account, false);
            } else {
                return response;
            }
        }

        protected Response retrieveResponseFromFuture(final AccountManagerFuture<Bundle> future) {
            try {

                final Bundle bundle = future.getResult();
                final String accessToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

                Logger.i("requested access token for account: " + accountName);

                Logger.i("requested access token: " + accessToken);

                if (accessToken == null) {
                    return getFailureAuthResponse(new Exception(NO_TOKEN_FOUND));

                } else {
                    return new Response(accessToken, accountName);
                }

            } catch (final Exception e) {
                return getFailureAuthResponse(e);
            }
        }

        public void setShouldShowUserPrompt(final boolean enabled) {
            mDisableUserPrompt = !enabled;
        }

        protected Account getAccount(final Context context) {
            return Accounts.getAccount(context);
        }

        protected void setAccountName(final Context context, final String name) {
            AuthPreferences.setAccountName(context, name);
        }

        protected Response getFailureAuthResponse(final Exception e) {
            Logger.i("requested access token error: " + e.getCause());

            return new Response(new AuthError(e));
        }

    }
}
