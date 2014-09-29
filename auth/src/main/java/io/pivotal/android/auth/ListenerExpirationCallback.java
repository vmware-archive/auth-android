/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
/* package */ class ListenerExpirationCallback implements AccountManagerCallback<Bundle> {

    private final Activity mActivity;
    private final Authorization.Listener mListener;

    public ListenerExpirationCallback(final Activity activity, final Authorization.Listener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public void run(final AccountManagerFuture<Bundle> future) {
        try {

            final Bundle bundle = future.getResult();
            final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
            Logger.i("getAuthToken accountName: " + accountName);

            final TokenProvider provider = TokenProviderFactory.get(mActivity);
            final Account account = Authorization.getAccount(mActivity, accountName);
            final Token existingToken = new Token(provider, account);

            final String token = existingToken.getAccessToken();
            Logger.i("getAuthToken token: " + token);

            if (existingToken.isExpired()) {
                Logger.i("getAuthToken expired.");
                provider.invalidateAuthToken(token);

                Logger.i("getAuthToken invalidated.");
                provider.getAuthToken(account, mListener);

            } else {
                mListener.onAuthorizationComplete(token);
            }

        } catch (final Exception e) {
            final Error error = new Error(e.getLocalizedMessage(), e);
            Logger.i("getAuthToken error: " + error);

            mListener.onAuthorizationFailure(error);
        }
    }
}
