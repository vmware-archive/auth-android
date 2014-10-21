/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;

/* package */ class ListenerCallback implements AccountManagerCallback<Bundle> {

    private final Authorization.Listener mListener;

    public ListenerCallback(final Authorization.Listener listener) {
        mListener = listener;
    }

    @Override
    public void run(final AccountManagerFuture<Bundle> future) {
        try {

            final Bundle bundle = future.getResult();
            final String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

            Logger.i("getAuthToken new token: " + token);

            mListener.onAuthorizationComplete(token);

        } catch (final Exception e) {
            final Error error = new Error(e.getLocalizedMessage(), e);
            Logger.i("getAuthToken error: " + error.getLocalizedMessage());

            mListener.onAuthorizationFailure(error);
        }
    }
}
