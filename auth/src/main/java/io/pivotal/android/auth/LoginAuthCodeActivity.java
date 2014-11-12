/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginAuthCodeActivity extends LoginActivity {

    @Override
     protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateContentView(savedInstanceState);

        final Intent intent = getIntent();

        if (intentHasCallbackUrl(intent)) {
            onHandleRedirect(intent);
        } else {
            authorize();
        }
    }

    protected void onCreateContentView(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_auth_code);
    }

    @Override
    protected String getUserName() {
       return "Account";
    }

    public void authorize() {
        final String authorizationUrl = getAuthorizationUrl();
        final Uri uri = Uri.parse(authorizationUrl);
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private String getAuthorizationUrl() {
        final AuthProvider provider = AuthProviderFactory.get();
        return provider.newAuthorizationCodeUrl().build();
    }

    protected boolean intentHasCallbackUrl(final Intent intent) {
        if (intent != null && intent.hasCategory(Intent.CATEGORY_BROWSABLE) && intent.getData() != null) {
            final String redirectUrl = Pivotal.getRedirectUrl().toLowerCase();
            return intent.getData().toString().toLowerCase().startsWith(redirectUrl);
        } else {
            return false;
        }
    }

    protected void onHandleRedirect(final Intent intent) {
        final String code = intent.getData().getQueryParameter("code");

        final Bundle bundle = AuthCodeTokenLoaderCallbacks.createBundle(code);
        final AuthCodeTokenLoaderCallbacks callback = new AuthCodeTokenLoaderCallbacks(this, this);

        getLoaderManager().restartLoader(2000, bundle, callback);
    }
}
