/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ class PasswordTokenLoaderCallbacks extends TokenLoaderCallbacks {

    private static interface Args {
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
    }

    public static Bundle createBundle(final String username, final String password) {
        final Bundle args = new Bundle();
        args.putString(Args.USERNAME, username);
        args.putString(Args.PASSWORD, password);
        return args;
    }

    public PasswordTokenLoaderCallbacks(final Context context, final TokenListener listener) {
        super(context, listener);
    }

    @Override
    public final Loader<TokenResponse> onCreateLoader(final int id, final Bundle args) {
        final String username = args.getString(Args.USERNAME);
        final String password = args.getString(Args.PASSWORD);
        return new PasswordTokenLoader(getContext(), username, password);
    }
}
