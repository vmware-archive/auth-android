/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Build;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ abstract class TokenLoaderCallbacks implements LoaderManager.LoaderCallbacks<TokenResponse> {

    private static final String ERROR = "error";

    private final Context mContext;
    private final TokenLoader.Listener mListener;

    public TokenLoaderCallbacks(final Context context, final TokenLoader.Listener listener) {
        mContext = context;
        mListener = listener;
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public final void onLoaderReset(final Loader<TokenResponse> loader) {
        // do nothing
    }

    @Override
    public final void onLoadFinished(final Loader<TokenResponse> loader, final TokenResponse data) {
        if (data.containsKey(ERROR)) {
            final String error = data.get(ERROR).toString();
            mListener.onAuthorizationFailed(new Error(error));
        } else {
            final Token token = new Token(data.getAccessToken(), data.getRefreshToken());
            mListener.onAuthorizationComplete(token);
        }
    }
}
