/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public abstract class BaseAuthorizationActivity extends Activity {

    private AuthorizationPreferencesProvider authorizationPreferencesProvider;
    private ApiProvider apiProvider;
    private AuthorizationEngine authorizationEngine;

    public abstract void onAuthorizationComplete();
    public abstract void onAuthorizationDenied();
    public abstract void onAuthorizationFailed(String reason);

    @Override
    protected void onResume() {
        super.onResume();

        setupRequirements();

        if (intentHasCallbackUrl(getIntent())) {
            try {
                // TODO - check state field in intent.data URI
                setupAuthorizationEngine();
                reenterAuthorizationEngine(getIntent());
            } catch (Exception e) {
                Logger.ex("Could not provide access code to Authorization Engine", e);
                notifyAuthorizationFailed("Could not provide access code to Authorization Engine :" + e.getLocalizedMessage());
            }
        }
    }

    private void setupRequirements() {
        if (authorizationPreferencesProvider == null) {
            // TODO - find a way to provide an alternate preferences provider in unit tests
            authorizationPreferencesProvider = new AuthorizationPreferencesProviderImpl(this);
        }
        if (apiProvider == null) {
            apiProvider = new ApiProviderImpl(this);
        }
    }

    private boolean intentHasCallbackUrl(Intent intent) {
        if (intent == null) {
            return false;
        }
        if (!intent.hasCategory(Intent.CATEGORY_BROWSABLE)) {
            return false;
        }
        if (intent.getData() == null) {
            return false;
        }
        return intent.getData().toString().toLowerCase().startsWith(authorizationPreferencesProvider.getRedirectUrl().toString().toLowerCase());
    }

    // TODO - find a way to get the state token in here
//    private boolean verifyCallbackState(Uri uri) {
//        return uri.getQueryParameter("state").equals(STATE_TOKEN);
//    }

    private void setupAuthorizationEngine() {
        if (authorizationEngine == null) {
            authorizationEngine = new AuthorizationEngine(apiProvider, authorizationPreferencesProvider);
        }
    }

    private void reenterAuthorizationEngine(Intent intent) throws Exception {
        final String authorizationCode = getAuthorizationCode(intent.getData());
        authorizationEngine.authorizationCodeReceived(this, authorizationCode);
    }

    private String getAuthorizationCode(Uri uri) {
        return uri.getQueryParameter("code");
    }

    public final void notifyAuthorizationComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAuthorizationComplete();
            }
        });
    }

    public final void notifyAuthorizationDenied() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAuthorizationDenied();
            }
        });
    }

    public final void notifyAuthorizationFailed(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAuthorizationFailed(reason);
            }
        });
    }
}
