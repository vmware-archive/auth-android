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
/* package */ class ListenerExpirationCheckCallback implements AccountManagerCallback<Bundle> {

    private final Activity mActivity;
    private final Authorization.Listener mListener;

    public ListenerExpirationCheckCallback(final Activity activity, final Authorization.Listener listener) {
        mActivity = activity;
        mListener = listener;
    }

    @Override
    public void run(final AccountManagerFuture<Bundle> future) {
        try {
            final Bundle bundle = future.getResult();
            final String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Logger.i("getAuthToken token: " + token);

            final String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
            Logger.i("getAuthToken accountName: " + accountName);

            final AccountManager manager = AccountManager.get(mActivity);
            final Account account = Authorization.getAccount(mActivity, accountName);
            final Token.Existing existingToken = new Token.Existing(manager, account);

            if (existingToken.isExpired()) {
                Logger.i("getAuthToken expired.");
                manager.invalidateAuthToken(Pivotal.Property.ACCOUNT_TYPE, token);

                Logger.i("getAuthToken invalidated.");
                final ListenerCallback callback = new ListenerCallback(mListener);
                manager.getAuthToken(account, Pivotal.Property.TOKEN_TYPE, null, false, callback, null);

            } else {
                mListener.onAuthorizationComplete(token);
            }

        } catch (final Exception e) {
            final String error = e.getLocalizedMessage();
            Logger.i("getAuthToken error: " + error);
            mListener.onAuthorizationFailure(error);
        }
    }
}
