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
/* package */ class AuthCodeTokenLoaderCallbacks extends TokenLoaderCallbacks {

    private static interface Args {
        public static final String AUTH_CODE = "auth_code";
    }

    public static Bundle createBundle(final String authCode) {
        final Bundle args = new Bundle();
        args.putString(Args.AUTH_CODE, authCode);
        return args;
    }

    public AuthCodeTokenLoaderCallbacks(final Context context, final TokenListener listener) {
        super(context, listener);
    }

    @Override
    public final Loader<TokenResponse> onCreateLoader(final int id, final Bundle args) {
        final String authCode = args.getString(Args.AUTH_CODE);
        final AuthProvider provider = new AuthProvider.Default();
        return new AuthCodeTokenLoader(getContext(), provider, authCode);
    }
}
