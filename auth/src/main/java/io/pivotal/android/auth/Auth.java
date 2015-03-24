/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.webkit.CookieManager;

public class Auth {

    public static Response getAccessToken(final Context context) {
        return AuthClientHolder.get(context).requestAccessToken(context);
    }

    public static void getAccessToken(final Context context, final Listener listener) {
        AuthClientHolder.get(context).requestAccessToken(context, listener);
    }

    public static void invalidateAccessToken(final Context context) {
        final AccountsProxy proxy = AccountsProxyHolder.get(context);
        final Account account = Accounts.getAccount(context);
        final String accessToken = proxy.getAccessToken(account);
        proxy.invalidateAccessToken(accessToken);
    }

    public static void logout(final Context context) {
        Accounts.removeAccount(context);
        CookieManager.getInstance().removeAllCookie();
    }

    public static void setShouldShowUserPrompt(final Context context, final boolean enabled) {
        AuthClientHolder.get(context).setShouldShowUserPrompt(enabled);
    }

    public static void registerLoginListener(final Context context, final LoginListener listener) {
        AccountsChangedReceiver.registerLoginListener(context, listener);
    }

    public static void registerLogoutListener(final Context context, final LogoutListener listener) {
        AccountsChangedReceiver.registerLogoutListener(context, listener);
    }

    public static interface Listener {
        public void onResponse(Response response);
    }

}
