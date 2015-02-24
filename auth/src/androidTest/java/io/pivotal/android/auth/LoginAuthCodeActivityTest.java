/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.AndroidTestCase;
import android.view.View;
import android.webkit.WebView;

import org.mockito.Mockito;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginAuthCodeActivityTest extends AndroidTestCase {

    private static final String AUTH_CODE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testGetUserNameReturnsDefaultValue() {
        final LoginAuthCodeActivity activity = Mockito.spy(new LoginAuthCodeActivity());

        assertEquals("Account", activity.getUserName());
    }

    public void testHandleRedirectUrlWithCorrectUrl() {
        final WebView webView = Mockito.mock(WebView.class);
        final LoginActivity activity = Mockito.spy(new LoginAuthCodeActivity());

        Mockito.doNothing().when(activity).fetchTokenWithAuthCodeGrantType(Mockito.anyString());

        final String redirectUrl = Pivotal.getRedirectUrl();
        final String redirectUrlWithCode = String.format("%s?code=%s", redirectUrl, AUTH_CODE);

        assertTrue(activity.handleRedirectUrl(webView, redirectUrlWithCode));

        Mockito.verify(activity).fetchTokenWithAuthCodeGrantType(AUTH_CODE);
        Mockito.verify(webView).setVisibility(View.GONE);
    }
}