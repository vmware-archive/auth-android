/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ class AuthCodeTokenLoader extends TokenLoader {

    private final String mAuthCode;

    public AuthCodeTokenLoader(final Context context, final String authCode) {
        super(context);
        mAuthCode = authCode;
    }

    @Override
    public TokenResponse loadInBackground() {
        try {
            final RemoteAuthenticator authenticator = RemoteAuthenticatorHolder.get(getContext());
            return authenticator.newAuthorizationCodeTokenRequest(mAuthCode).execute();
        } catch (final Exception e) {
            Logger.ex(e);
            return new ErrorResponse(e);
        }
    }
}
