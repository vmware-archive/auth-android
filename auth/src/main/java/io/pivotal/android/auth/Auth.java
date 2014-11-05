/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;

public class Auth {

    public static interface Listener {
        public void onComplete(String token, String account);

        public void onFailure(Error error);
    }

    public static void getAccessToken(final Activity activity, final Listener listener) {
        Logger.i("getAccessToken");
        final TokenProvider provider = TokenProviderFactory.get(activity);
        provider.getAccessToken(activity, listener);
    }

    public static void getAccessToken(final Context context, final boolean promptUser, final Listener listener) {
        Logger.i("getLastUsedAccessToken");
        final TokenProvider provider = TokenProviderFactory.get(context);
        final Account[] accounts = getAccounts(context);

        if (accounts.length == 1) {
            provider.getAccessToken(accounts[0], promptUser, listener);
        } else {

            final String accountName = AuthPreferences.getLastUsedAccountName(context);
            final Account account = getAccount(context, accountName);
            if (account != null) {
                provider.getAccessToken(account, promptUser, listener);
            } else {
                listener.onFailure(new Error("Could not determine last used account."));
            }
        }
    }

    public static String getAccessToken(final Context context, final String accountName) {
        try {
            return getAccessTokenOrThrow(context, accountName);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    public static String getAccessTokenOrThrow(final Context context, final String accountName) throws Exception {
        Logger.i("getAccessToken: " + accountName);
        final Account account = getAccount(context, accountName);
        return TokenProviderFactory.get(context).getAccessTokenOrThrow(account);
    }

    public static void invalidateAccessToken(final Context context, final String token) {
        Logger.i("invalidateAccessToken");
        TokenProviderFactory.get(context).invalidateAccessToken(token);
    }

    public static void addAccount(final Context context, final String name, final Token token) {
        Logger.i("addAccount: " + name + ", token: " + token.getAccessToken());
        final TokenProvider provider = TokenProviderFactory.get(context);
        final Account account = new Account(name, Pivotal.getAccountType());
        provider.addAccount(account, token.getRefreshToken());
        provider.setAccessToken(account, token.getAccessToken());
    }

    public static Account[] getAccounts(final Context context) {
        Logger.i("getAccounts");
        return TokenProviderFactory.get(context).getAccounts();
    }

    public static Account getAccount(final Context context, final String name) {
        Logger.i("getAccount: " + name);
        final Account[] accounts = getAccounts(context);
        for (int i = 0; i < accounts.length; i++) {
            if (name == null || name.equals(accounts[i].name)) {
                return accounts[i];
            }
        }
        return null;
    }

    public static void removeAccount(final Context context, final String name) {
        Logger.i("removeAccount: " + name);
        final Account account = new Account(name, Pivotal.getAccountType());
        TokenProviderFactory.get(context).removeAccount(account);
    }

    public static void removeAllAccounts(final Context context) {
        Logger.i("removeAllAccounts");
        final TokenProvider provider = TokenProviderFactory.get(context);
        final Account[] accounts = getAccounts(context);
        for (int i = 0; i < accounts.length; i++) {
            provider.removeAccount(accounts[i]);
        }
    }
}
