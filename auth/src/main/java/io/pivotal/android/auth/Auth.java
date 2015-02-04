/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

public class Auth {

    public static Response getAccessToken(final Context context) {
        final AuthClient auth = AuthClientFactory.get(context);
        return auth.requestAccessToken(context);
    }

    public static void getAccessToken(final Context context, final Listener listener) {
        final AuthClient auth = AuthClientFactory.get(context);
        auth.requestAccessToken(context, listener);
    }

    public static Response getAccessToken(final Context context, final Account account) {
        final AuthClient auth = AuthClientFactory.get(context);
        return auth.requestAccessToken(account);
    }

    public static void getAccessToken(final Context context, final Account account, final Listener listener) {
        final AuthClient auth = AuthClientFactory.get(context);
        auth.requestAccessToken(account, listener);
    }

    public static Response getAccessTokenWithUserPrompt(final Activity activity) {
        final AuthClient auth = AuthClientFactory.get(activity);
        return auth.requestAccessToken(activity);
    }

    public static void getAccessTokenWithUserPrompt(final Activity activity, final Listener listener) {
        final AuthClient auth = AuthClientFactory.get(activity);
        auth.requestAccessToken(activity, listener);
    }

    public static void invalidateAccessToken(final Context context) {
        final AccountsProxy proxy = AccountsProxyFactory.get(context);
        final Account account = Accounts.getLastUsedAccount(context);
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
