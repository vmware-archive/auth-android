/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class LoginAuthCodeActivity extends LoginActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth_code);

        final WebView webView = (WebView) findViewById(R.id.login_web_view);

        fetchTokenWithAuthCodeGrantType(webView);
    }

    @Override
    protected String getUserName() {
        return "Account";
    }

    @Override
    protected boolean handleRedirectUrl(final WebView view, final String url) {
        final boolean handled = super.handleRedirectUrl(view, url);
        if (handled) {
            view.setVisibility(View.GONE);
        }
        return handled;
    }
}
