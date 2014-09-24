/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;

/* package */ interface TokenProvider {
    public String getAuthToken(final Account account);

    public String getRefreshToken(final Account account);
}
