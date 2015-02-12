/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.Context;

public class Accounts {

    public static void addAccount(final Context context, final String name, final Token token) {
        Logger.i("addAccount: " + name + ", token: " + token.getAccessToken());
        final AccountsProxy proxy = AccountsProxyFactory.get(context);
        final Account account = new Account(name, Pivotal.getAccountType());
        proxy.addAccount(account, token.getRefreshToken());
        proxy.setAccessToken(account, token.getAccessToken());
    }

    public static Account[] getAccounts(final Context context) {
        Logger.i("getAccounts");
        return AccountsProxyFactory.get(context).getAccounts();
    }

    public static Account getAccount(final Context context, final String name) {
        Logger.i("getAccount: " + name);
        final Account[] accounts = getAccounts(context);
        for (final Account account : accounts) {
            if (name == null || name.equals(account.name)) {
                return account;
            }
        }
        return null;
    }

    public static void removeAccount(final Context context, final String name) {
        Logger.i("removeAccount: " + name);
        final AccountsProxy proxy = AccountsProxyFactory.get(context);
        final Account account = new Account(name, Pivotal.getAccountType());
        proxy.removeAccount(account);
    }

    public static void removeAllAccounts(final Context context) {
        Logger.i("removeAllAccounts");
        final AccountsProxy proxy = AccountsProxyFactory.get(context);
        final Account[] accounts = getAccounts(context);
        for (final Account account : accounts) {
            proxy.removeAccount(account);
        }
    }

    public static Account getLastUsedAccount(final Context context) {
        final Account[] accounts = getAccounts(context);
        if (accounts.length == 1) {
            return accounts[0];
        } else {
            final String accountName = AuthPreferences.getLastUsedAccountName(context);
            final Account account = getAccount(context, accountName);
            return account;
        }
    }
}
