/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ class TokenAuthCodeLoader extends TokenLoader {

    private final String mAuthCode;

    public TokenAuthCodeLoader(final Context context, final String authCode) {
        super(context);
        mAuthCode = authCode;
    }

    @Override
    public TokenResponse loadInBackground() {
        try {
            final AuthorizationProvider provider = new AuthorizationProvider();
            return provider.newTokenRequest(mAuthCode).execute();
        } catch (final Exception e) {
            Logger.ex(e);
            return new ErrorTokenResponse(e);
        }
    }
}
