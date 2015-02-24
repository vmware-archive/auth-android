/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPasswordActivity extends LoginActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
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
        final String userName = getUserName();
        final String password = getPassword();

        if (onValidateCredentials(userName, password)) {
            onStartLoading();

            fetchTokenWithPasswordGrantType(userName, password);
        }
    }

    protected boolean onValidateCredentials(final String userName, final String password) {
        Logger.v("onValidateCredentials");
        final boolean userNameValid = userName != null && userName.length() > 0;
        final boolean passwordValid = password != null && password.length() > 0;

        if (!userNameValid || !passwordValid) {
            Toast.makeText(this, "Username/Password required.", Toast.LENGTH_SHORT).show();
        }

        return userNameValid && passwordValid;
    }

    protected void onStartLoading() {
        Logger.v("onStartLoading");
        final Button button = (Button) findViewById(R.id.login_submit);
        if (button != null) {
            button.setText("Loading...");
            button.setEnabled(false);
        }
    }

    @Override
    public void onAuthorizationFailed(final Error error) {
        final Button button = (Button) findViewById(R.id.login_submit);
        if (button != null) {
            button.setText("Submit");
            button.setEnabled(true);
        }
    }
}
