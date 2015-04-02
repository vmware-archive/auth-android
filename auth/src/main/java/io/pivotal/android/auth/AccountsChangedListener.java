/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;

public class AccountsChangedListener implements OnAccountsUpdateListener {

    static AccountsChangedListener sAccountsChangedListener;

    private boolean mIsLoggedIn;
    private LoginListener mLoginListener;
    private LogoutListener mLogoutListener;
    private final Context mContext;

    private AccountsChangedListener(final Context context) {
        mContext = context;
    }

    public static synchronized AccountsChangedListener getInstance(final Context context) {
        if (sAccountsChangedListener == null) {
            sAccountsChangedListener = new AccountsChangedListener(context);
        }

        return sAccountsChangedListener;
    }

    @Override
    public void onAccountsUpdated(Account[] accounts)  {
        boolean isLoggedIn = isLoggedIn();

        Logger.i("Accounts changed, isLoggedIn: " + isLoggedIn);

        if (!mIsLoggedIn && isLoggedIn) {
            Logger.i("Accounts onLogin");

            if (mLoginListener != null) {
                mLoginListener.onLogin(mContext);
            }
            AccountsProxyHolder.get(mContext).removeOnAccountsUpdatedListener(this);
        } else if (mIsLoggedIn && !isLoggedIn) {
            Logger.i("Accounts onLogout");

            if (mLogoutListener != null) {
                mLogoutListener.onLogout(mContext);
            }
            AccountsProxyHolder.get(mContext).removeOnAccountsUpdatedListener(this);
        } else {
            Logger.w("Accounts no login state change");
        }


        mIsLoggedIn = isLoggedIn;
    }

    void registerLoginListener(final LoginListener listener) {
        mLoginListener = listener;
        mIsLoggedIn = isLoggedIn();
    }

    void registerLogoutListener(final LogoutListener listener) {
        mLogoutListener = listener;
        mIsLoggedIn = isLoggedIn();
    }

    void setIsLoggedIn(final boolean loggedIn) {
        mIsLoggedIn = loggedIn;
    }

    private boolean isLoggedIn() {
        final Account account = Accounts.getAccount(mContext);
        return account != null;
    }
}
