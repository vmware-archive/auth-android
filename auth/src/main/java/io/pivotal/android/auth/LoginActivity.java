/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class LoginActivity extends AccountAuthenticatorActivity implements TokenLoader.Listener {

    protected abstract String getUserName();


    protected String getAuthorizationUrl() {
        final RemoteAuthenticator authenticator = RemoteAuthenticatorHolder.get();
        return authenticator.newAuthorizationCodeUrl().build();
    }

    protected boolean handleRedirectUrl(final WebView webView, final String url) {
        if (url.startsWith(Pivotal.getRedirectUrl().toLowerCase())) {

            final Uri uri = Uri.parse(url);
            final String authCode = uri.getQueryParameter("code");

            fetchTokenWithAuthCodeGrantType(authCode);

            return true;
        } else {
            return false;
        }
    }

    public void fetchTokenWithAuthCodeGrantType(final WebView webView) {
        Logger.v("fetchTokenWithAuthCodeGrantType");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                return handleRedirectUrl(view, url) || super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.loadUrl(getAuthorizationUrl());
    }

    public void fetchTokenWithAuthCodeGrantType(final String authCode) {
        Logger.v("fetchTokenWithAuthCodeGrantType");

        final Bundle bundle = AuthCodeTokenLoaderCallbacks.createBundle(authCode);
        final AuthCodeTokenLoaderCallbacks callback = new AuthCodeTokenLoaderCallbacks(this, this);

        getLoaderManager().restartLoader(1000, bundle, callback);
    }

    public void fetchTokenWithPasswordGrantType(final String userName, final String password) {
        Logger.v("fetchTokenWithPasswordGrantType");

        final Bundle bundle = PasswordTokenLoaderCallbacks.createBundle(userName, password);
        final PasswordTokenLoaderCallbacks callback = new PasswordTokenLoaderCallbacks(this, this);

        getLoaderManager().restartLoader(2000, bundle, callback);
    }

    @Override
    public void onAuthorizationFailed(final Error error) {
        final String message = error.getLocalizedMessage();
        Toast.makeText(this, "ERROR: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthorizationComplete(final Token token) {
        final String username = getUserName();
        if (Accounts.addAccount(this, username, token)) {
            setResultIntent(token, username);
            finish();
        } else {
            final Error error = new Error("Account already exists with a different name.");
            onAuthorizationFailed(error);
        }
    }

    protected void setResultIntent(final Token token, final String username) {
        final Intent intent = getResultIntent(token, username);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
    }

    protected Intent getResultIntent(final Token token, final String username) {
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Pivotal.getAccountType());
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        return intent;
    }
}
