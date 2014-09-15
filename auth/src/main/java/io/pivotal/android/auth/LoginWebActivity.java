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
import android.widget.Toast;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginWebActivity extends AccountAuthenticatorActivity implements TokenListener {

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
        setContentView(R.layout.activity_login);
    }

    protected String getUserName() {
       return "Account";
    }

    public void authorize() {
        final AuthorizationProvider provider = new AuthorizationProvider();
        final AuthorizationCodeRequestUrl authorizationUrl = provider.newAuthorizationUrl();
        final Uri uri = Uri.parse(authorizationUrl.build());
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private boolean intentHasCallbackUrl(final Intent intent) {
        if (intent != null && intent.hasCategory(Intent.CATEGORY_BROWSABLE) && intent.getData() != null) {
            final String redirectUrl = Pivotal.Property.REDIRECT_URL.toLowerCase();
            return intent.getData().toString().toLowerCase().startsWith(redirectUrl);
        } else {
            return false;
        }
    }

    private void onHandleRedirect(final Intent intent) {
        final String code = intent.getData().getQueryParameter("code");

        final Bundle bundle = TokenAuthCodeLoaderCallbacks.createBundle(code);
        final TokenAuthCodeLoaderCallbacks callback = new TokenAuthCodeLoaderCallbacks(this, this);

        getLoaderManager().restartLoader(2000, bundle, callback);
    }

    @Override
    public void onAuthorizationComplete(final String token) {
        final String username = getUserName();
        Authorization.addAccount(this, username, token);

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Pivotal.Property.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, token);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onAuthorizationFailed(final Error error) {
        Toast.makeText(this, "error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
