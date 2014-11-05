/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ class PasswordTokenLoader extends TokenLoader {

    private final AuthProvider mProvider;

    private final String mUsername;
    private final String mPassword;

    public PasswordTokenLoader(final Context context, final AuthProvider provider, final String username, final String password) {
        super(context);
        mProvider = provider;
        mUsername = username;
        mPassword = password;
    }

    @Override
    public TokenResponse loadInBackground() {
        try {
            return mProvider.newPasswordTokenRequest(mUsername, mPassword).execute();
        } catch (final Exception e) {
            Logger.ex(e);
            return new ErrorResponse(e);
        }
    }
}
