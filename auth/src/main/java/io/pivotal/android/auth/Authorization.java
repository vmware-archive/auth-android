/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Authorization {

    public static interface Listener {
        public void onAuthorizationFailure(String error);

        public void onAuthorizationComplete(String token);
    }

    public static void getAuthToken(final Activity activity, final Listener listener) {
        Logger.i("getAuthToken");
        final TokenProvider provider = TokenProviderFactory.get(activity);
        provider.getAuthToken(activity, listener);
    }

    public static String getAuthToken(final Context context, final String accountName) {
        try {
            return getAuthTokenOrThrow(context, accountName);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    public static String getAuthTokenOrThrow(final Context context, final String accountName) throws Exception {
        Logger.i("getAuthToken: " + accountName);
        final TokenProvider provider = TokenProviderFactory.get(context);
        final Account account = getAccount(context, accountName);
        return provider.getAuthTokenOrThrow(account);
    }

    public static void invalidateAuthToken(final Context context, final String token) {
        Logger.i("invalidateAuthToken");
        final TokenProvider provider = TokenProviderFactory.get(context);
        provider.invalidateAuthToken(token);
    }

    public static void addAccount(final Context context, final String name, final Token token) {
        Logger.i("addAccount: " + name + ", token: " + token.getAccessToken());
        final TokenProvider provider = TokenProviderFactory.get(context);
        final Account account = new Account(name, Pivotal.get(Pivotal.PROP_ACCOUNT_TYPE));
        provider.addAccount(account, token.getRefreshToken());
        provider.setAuthToken(account, token.getAccessToken());
    }

    public static Account[] getAccounts(final Context context) {
        Logger.i("getAccounts");
        final TokenProvider provider = TokenProviderFactory.get(context);
        return provider.getAccounts();
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
        final TokenProvider provider = TokenProviderFactory.get(context);
        final Account account = new Account(name, Pivotal.get(Pivotal.PROP_ACCOUNT_TYPE));
        provider.removeAccount(account);
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
