/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity extends AccountAuthenticatorActivity implements TokenListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateContentView(savedInstanceState);
    }

    protected void onCreateContentView(final Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    protected String getUserName() {
        final EditText editText = (EditText) findViewById(R.id.login_user_name);
        return editText.getText().toString();
    }

    protected String getPassword() {
        final EditText editText = (EditText) findViewById(R.id.login_password);
        return editText.getText().toString();
    }

    public void onLoginClicked(final View view) {
        final Bundle bundle = TokenPasswordLoaderCallbacks.createBundle(getUserName(), getPassword());
        final TokenPasswordLoaderCallbacks callback = new TokenPasswordLoaderCallbacks(this, this);

        getLoaderManager().restartLoader(1000, bundle, callback);
    }

    @Override
    public void onAuthorizationComplete(final Token token) {
        final String username = getUserName();
        Authorization.addAccount(this, username, token);

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Pivotal.Property.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onAuthorizationFailed(final Error error) {
        Toast.makeText(this, "login error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
