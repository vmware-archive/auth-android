/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;

/* package */ interface TokenProvider {
    public String getRefreshToken(Account account);

    public String getAuthToken(Account account);

    public String getAuthTokenOrThrow(Account account) throws Exception;

    public void getAuthToken(Activity activity, Authorization.Listener listener);

    public void getAuthToken(Account account, Authorization.Listener listener);

    public void setAuthToken(Account account, String accessToken);

    public void invalidateAuthToken(String accessToken);

    public void addAccount(Account account, String refreshToken);

    public void removeAccount(Account account);

    public Account[] getAccounts();
}
