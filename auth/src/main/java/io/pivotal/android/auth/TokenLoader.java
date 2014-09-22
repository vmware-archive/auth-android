/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import com.google.api.client.auth.oauth2.TokenResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ abstract class TokenLoader extends AsyncTaskLoader<TokenResponse> {

    public TokenLoader(final Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /* package */ static final class ErrorResponse extends TokenResponse {

        public ErrorResponse(final Exception e) {
            final String message = e.getLocalizedMessage();
            put("error", message != null ? message : "Unknown error.");
        }
    }
}
