/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;

/* package */ class ListenerCallback implements AccountManagerCallback<Bundle> {

    private final Auth.Listener mListener;

    public ListenerCallback(final Auth.Listener listener) {
        mListener = listener;
    }

    @Override
    public void run(final AccountManagerFuture<Bundle> future) {
        try {

            final Bundle bundle = future.getResult();
            final String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            final String name = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);

            if (token == null) {
                throw new IllegalArgumentException("Auth token not found.");
            }

            Logger.i("getAccessToken new token: " + token);

            mListener.onComplete(token, name);

        } catch (final Exception e) {
            final Error error = new Error(e.getLocalizedMessage(), e);
            Logger.i("getAccessToken error: " + error.getLocalizedMessage());

            mListener.onFailure(error);
        }
    }
}
