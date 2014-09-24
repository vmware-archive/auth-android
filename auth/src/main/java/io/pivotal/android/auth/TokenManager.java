/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/* package */ class TokenManager implements TokenProvider {

    private AccountManager mManager;

    public TokenManager(final Context context) {
        mManager = AccountManager.get(context);
    }

    @Override
    public String getAuthToken(final Account account) {
        return mManager.peekAuthToken(account, Pivotal.Property.TOKEN_TYPE);
    }

    @Override
    public String getRefreshToken(final Account account) {
        return mManager.getPassword(account);
    }
}
