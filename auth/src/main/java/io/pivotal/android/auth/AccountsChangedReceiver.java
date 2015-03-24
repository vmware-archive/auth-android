/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AccountsChangedReceiver extends BroadcastReceiver {

    private static boolean sIsLoggedIn;
    private static LoginListener sLoginListener;
    private static LogoutListener sLogoutListener;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final boolean isLoggedIn = isLoggedIn(context);

        Logger.i("Accounts changed, isLoggedIn: " + isLoggedIn);

        if (!sIsLoggedIn && isLoggedIn) {
            Logger.i("Accounts onLogin");

            if (sLoginListener != null) {
                sLoginListener.onLogin(context);
            }

        } else if (sIsLoggedIn && !isLoggedIn) {
            Logger.i("Accounts onLogout");

            if (sLogoutListener != null) {
                sLogoutListener.onLogout(context);
            }
        }

        sIsLoggedIn = isLoggedIn;
    }

    static void registerLoginListener(final Context context, final LoginListener listener) {
        sLoginListener = listener;
        sIsLoggedIn = isLoggedIn(context);
    }

    static void registerLogoutListener(final Context context, final LogoutListener listener) {
        sLogoutListener = listener;
        sIsLoggedIn = isLoggedIn(context);
    }

    static void setIsLoggedIn(final boolean loggedIn) {
        sIsLoggedIn = loggedIn;
    }

    private static boolean isLoggedIn(final Context context) {
        final Account account = Accounts.getAccount(context);
        return account != null;
    }
}
