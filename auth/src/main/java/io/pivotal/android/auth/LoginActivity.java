/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/* package */ abstract class LoginActivity extends AccountAuthenticatorActivity implements TokenListener {

    protected abstract String getUserName();

    @Override
    public void onAuthorizationComplete(final Token token) {
        final String username = getUserName();
        Authorization.addAccount(this, username, token);

        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Pivotal.get("pivotal.auth.accountType"));
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, token.getAccessToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onAuthorizationFailed(final Error error) {
        Toast.makeText(this, "error: " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
