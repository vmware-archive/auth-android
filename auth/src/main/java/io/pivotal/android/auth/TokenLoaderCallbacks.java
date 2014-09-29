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

    private final Context mContext;
    private final TokenListener mListener;

    public TokenLoaderCallbacks(final Context context, final TokenListener listener) {
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
        if (data.containsKey("error")) {
            final String error = data.get("error").toString();
            mListener.onAuthorizationFailed(new Error(error));
        } else {
            final Token token = new Token(data);
            mListener.onAuthorizationComplete(token);
        }
    }
}
