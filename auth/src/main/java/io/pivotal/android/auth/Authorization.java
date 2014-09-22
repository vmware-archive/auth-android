/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
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
        final AccountManager manager = AccountManager.get(activity);
        final ListenerExpirationCheckCallback callback = new ListenerExpirationCheckCallback(activity, listener);
        manager.getAuthTokenByFeatures(Pivotal.Property.ACCOUNT_TYPE, Pivotal.Property.TOKEN_TYPE, null, activity, null, null, callback, null);
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
        final Account account = getAccount(context, accountName);
        final AccountManager manager = AccountManager.get(context);
        return manager.blockingGetAuthToken(account, Pivotal.Property.TOKEN_TYPE, false);
    }

    public static void invalidateAuthToken(final Context context, final String token) {
        Logger.i("invalidateAuthToken");
        final AccountManager manager = AccountManager.get(context);
        manager.invalidateAuthToken(Pivotal.Property.ACCOUNT_TYPE, token);
    }

    public static void addAccount(final Context context, final String name, final Token token) {
        Logger.i("addAccount: " + name + ", token: " + token.getAccessToken());
        final Account account = new Account(name, Pivotal.Property.ACCOUNT_TYPE);
        final AccountManager manager = AccountManager.get(context);
        manager.addAccountExplicitly(account, null, null);
        manager.setAuthToken(account, Pivotal.Property.TOKEN_TYPE, token.getAccessToken());
        manager.setPassword(account, token.getRefreshToken());

        final String authToken = manager.peekAuthToken(account, Pivotal.Property.TOKEN_TYPE);
        final String refreshToken = manager.getPassword(account);
        Logger.i("addAccount peek authToken: " + authToken + ", refreshToken: " + refreshToken);
    }

    public static Account[] getAccounts(final Context context) {
        Logger.i("getAccounts");
        final AccountManager manager = AccountManager.get(context);
        final Account[] accounts = manager.getAccountsByType(Pivotal.Property.ACCOUNT_TYPE);
        return accounts;
    }

    public static Account getAccount(final Context context, final String name) {
        Logger.i("getAccount: " + name);
        final Account[] accounts = getAccounts(context);
        if (accounts != null) {
            for (int i = 0; i < accounts.length; i++) {
                if (name == null || name.equals(accounts[i].name)) {
                    return accounts[i];
                }
            }
        }
        return null;
    }

}
