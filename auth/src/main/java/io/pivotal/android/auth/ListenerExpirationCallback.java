/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;

/* package */ class ListenerExpirationCallback implements AccountManagerCallback<Bundle> {

    private final Activity mActivity;
    private final Auth.Listener mListener;

    public ListenerExpirationCallback(final Activity activity, final Auth.Listener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public void run(final AccountManagerFuture<Bundle> future) {
        try {

            final Bundle bundle = future.getResult();
            final String accessToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

            AuthPreferences.setLastUsedAccountName(mActivity, accountName);

            Logger.i("getAccessToken accountName: " + accountName);
            Logger.i("getAccessToken accessToken: " + accessToken);

            if (Token.isExpired(accessToken)) {
                final TokenProvider provider = TokenProviderFactory.get(mActivity);
                final Account account = Auth.getAccount(mActivity, accountName);

                Logger.i("getAccessToken expired.");
                provider.invalidateAccessToken(accessToken);

                Logger.i("getAccessToken invalidated.");
                provider.getAccessToken(mActivity, account, mListener);

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
