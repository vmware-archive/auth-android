/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;

public class Accounts {

    public static boolean addAccount(final Context context, final String name, final Token token) {
        Logger.i("addAccount: " + name + ", token: " + token.getAccessToken());
        final Account oldAccount = getAccount(context);

        if (oldAccount == null || oldAccount.name.equals(name)) {
            final AccountsProxy proxy = AccountsProxyHolder.get(context);
            final Account account = new Account(name, Pivotal.getAccountType());
            proxy.addAccount(account, token.getRefreshToken());
            proxy.setAccessToken(account, token.getAccessToken());
            return true;
        }

        return false;
    }

    public static void removeAccount(final Context context) {
        Logger.i("removeAccount");
        final AccountsProxy proxy = AccountsProxyHolder.get(context);
        final Account account = getAccount(context);
        if (account != null) {
            proxy.removeAccount(account);
        }
    }

    public static Account getAccount(final Context context) {
        final Account[] accounts = getAccounts(context);
        if (accounts.length == 1) {
            return accounts[0];
        } else {
            final String name = AuthPreferences.getAccountName(context);
            return getAccount(context, name);
        }
    }

    private static Account[] getAccounts(final Context context) {
        Logger.i("getAccounts");
        return AccountsProxyHolder.get(context).getAccounts();
    }

    private static Account getAccount(final Context context, final String name) {
        Logger.i("getAccount: " + name);
        final Account[] accounts = getAccounts(context);
        for (final Account account : accounts) {
            if (name == null || name.equals(account.name)) {
                return account;
            }
        }
        return null;
    }
}
