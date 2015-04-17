/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;
import android.webkit.CookieManager;

public class Auth {

    public static Response getAccessToken(final Context context) {
        AccountsProxyHolder.get(context).removeOnAccountsUpdatedListener(AccountsChangedListener.getInstance(context));

        // only add listener if there isn't already a logged in user
        if (Accounts.getAccount(context) == null) {
            AccountsProxyHolder.get(context).addOnAccountsUpdatedListener(AccountsChangedListener.getInstance(context));
        }

        return AuthClientHolder.get(context).requestAccessToken(context);
    }

    public static void getAccessToken(final Context context, final Listener listener) {
        AccountsProxyHolder.get(context).removeOnAccountsUpdatedListener(AccountsChangedListener.getInstance(context));

        // only add listener if there isn't already a logged in user
        if (Accounts.getAccount(context) == null) {
            AccountsProxyHolder.get(context).addOnAccountsUpdatedListener(AccountsChangedListener.getInstance(context));
        }

        AuthClientHolder.get(context).requestAccessToken(context, listener);
    }

    public static void invalidateAccessToken(final Context context) {
        final AccountsProxy proxy = AccountsProxyHolder.get(context);
        final Account account = Accounts.getAccount(context);
        final String accessToken = proxy.getAccessToken(account);
        proxy.invalidateAccessToken(accessToken);
    }

    public static void logout(final Context context) throws AuthError {
        AccountsProxyHolder.get(context).removeOnAccountsUpdatedListener(AccountsChangedListener.getInstance(context));

        if (Accounts.getAccount(context) != null) {
            AccountsProxyHolder.get(context).addOnAccountsUpdatedListener(AccountsChangedListener.getInstance(context));
            Accounts.removeAccount(context);
            AccountsProxyHolder.get(context).clearCookies();
        } else {
            throw new AuthError(new Exception("Already logged out"));
        }
    }

    public static void setShouldShowUserPrompt(final Context context, final boolean enabled) {
        AuthClientHolder.get(context).setShouldShowUserPrompt(enabled);
    }

    public static void registerLoginListener(final Context context, final LoginListener listener) {
        AccountsChangedListener.getInstance(context).registerLoginListener(listener);
    }

    public static void registerLogoutListener(final Context context, final LogoutListener listener) {
        AccountsChangedListener.getInstance(context).registerLogoutListener(listener);
    }

    public static interface Listener {
        public void onResponse(Response response);
    }

}
