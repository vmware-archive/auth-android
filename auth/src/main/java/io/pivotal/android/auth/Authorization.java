/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

public class Authorization {

    public static interface Listener {
        public void onAuthorizationFailure(String error);

        public void onAuthorizationComplete(String token);
    }

    public static void getAuthToken(final Activity activity, final Listener listener) {
        final AccountManager manager = AccountManager.get(activity);
        final ListenerCallback callback = new ListenerCallback(listener);
        manager.getAuthTokenByFeatures(Pivotal.Property.ACCOUNT_TYPE, Pivotal.Property.TOKEN_TYPE, null, activity, null, null, callback, null);
    }

    public static String getAuthToken(final Activity activity, final String accountName) {
        try {
            final Account account = getAccount(activity, accountName);
            final AccountManager manager = AccountManager.get(activity);
            return manager.blockingGetAuthToken(account, Pivotal.Property.TOKEN_TYPE, false);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    public static void invalidateAuthToken(final Activity activity, final String token) {
        final AccountManager manager = AccountManager.get(activity);
        manager.invalidateAuthToken(Pivotal.Property.ACCOUNT_TYPE, token);
    }

    public static void addAccount(final Activity activity, final String name, final String token) {
        final Account account = new Account(name, Pivotal.Property.ACCOUNT_TYPE);
        final AccountManager manager = AccountManager.get(activity);
        manager.addAccountExplicitly(account, null, null);
        manager.setAuthToken(account, Pivotal.Property.TOKEN_TYPE, token);
    }

    public static Account[] getAccounts(final Activity activity) {
        final AccountManager manager = AccountManager.get(activity);
        final Account[] accounts = manager.getAccountsByType(Pivotal.Property.ACCOUNT_TYPE);
        return accounts;
    }

    public static Account getAccount(final Activity activity, final String name) {
        final Account[] accounts = getAccounts(activity);
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
