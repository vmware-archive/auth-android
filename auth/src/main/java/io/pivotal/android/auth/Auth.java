/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.text.TextUtils;

public class Auth {

    private static final String NO_ACCOUNT_FOUND = "No Account Found.";

    public static Response getAccessToken(final Context context) {
        return AuthClientFactory.get(context).requestAccessToken(context);
    }

    public static void getAccessToken(final Context context, final Listener listener) {
        AuthClientFactory.get(context).requestAccessToken(context, listener);
    }

    public static Response getAccessToken(final Context context, final String accountName) {
        final Account account = Accounts.getAccount(context, accountName);
        if (account != null) {
            return AuthClientFactory.get(context).requestAccessToken(context, account);
        } else {
            return getNoAccountErrorResponse();
        }
    }

    public static void getAccessToken(final Context context, final String accountName, final Listener listener) {
        final Account account = Accounts.getAccount(context, accountName);
        if (account != null) {
            AuthClientFactory.get(context).requestAccessToken(context, account, listener);
        } else {
            listener.onResponse(getNoAccountErrorResponse());
        }
    }

    private static Response getNoAccountErrorResponse() {
        final Exception exception = new RuntimeException(NO_ACCOUNT_FOUND);
        return new Response(new AuthError(exception));
    }

    public static void invalidateAccessToken(final Context context) {
        final AccountsProxy proxy = AccountsProxyFactory.get(context);
        final Account account = Accounts.getLastUsedAccount(context);
        final String accessToken = proxy.getAccessToken(account);
        proxy.invalidateAccessToken(accessToken);
    }

    public static void invalidateAccessToken(final Context context, final String accountName) {
        final AccountsProxy proxy = AccountsProxyFactory.get(context);
        final Account account = Accounts.getAccount(context, accountName);
        final String accessToken = proxy.getAccessToken(account);
        proxy.invalidateAccessToken(accessToken);
    }

    public static interface Listener {
        public void onResponse(Response response);
    }

    public static class Response {
        public String accessToken;
        public String accountName;
        public AuthError error;

        public Response(final AuthError error) {
            this.error = error;
        }

        public Response(final String accessToken, final String accountName) {
            this.accessToken = accessToken;
            this.accountName = accountName;
        }

        public boolean isTokenExpired() {
            return TextUtils.isEmpty(this.accessToken) || TokenUtil.isExpired(this.accessToken);
        }

        public boolean isSuccess() {
            return this.error == null;
        }

        public boolean isFailure() {
            return this.error != null;
        }
    }
}
