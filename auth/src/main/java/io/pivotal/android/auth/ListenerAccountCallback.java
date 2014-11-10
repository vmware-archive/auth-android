/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;

/* package */ class ListenerAccountCallback implements AccountManagerCallback<Bundle> {

    private final Context mContext;
    private final Account mAccount;
    private final Auth.Listener mListener;

    public ListenerAccountCallback(final Context context, final Account account, final Auth.Listener listener) {
        mContext = context;
        mAccount = account;
        mListener = listener;
    }

    @Override
    public void run(final AccountManagerFuture<Bundle> future) {
        try {

            final Bundle bundle = future.getResult();
            final String accessToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

            Logger.i("getAccessToken accessToken: " + accessToken);

            if (Token.isExpired(accessToken)) {
                final TokenProvider provider = TokenProviderFactory.get(mContext);

                Logger.i("getAccessToken expired.");
                provider.invalidateAccessToken(accessToken);

                Logger.i("getAccessToken invalidated.");
                provider.getAccessToken(mAccount, false, mListener);

            } else {
                mListener.onComplete(accessToken, accountName);
            }

        } catch (final Exception e) {
            final Error error = new Error(e.getLocalizedMessage(), e);
            Logger.i("getAccessToken error: " + error.getLocalizedMessage());

            mListener.onFailure(error);
        }
    }
}
