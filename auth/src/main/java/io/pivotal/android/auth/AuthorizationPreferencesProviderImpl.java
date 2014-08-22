/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AuthorizationPreferencesProviderImpl implements AuthorizationPreferencesProvider {

    public static final String TAG_NAME = "PivotalCFMSDataSDK";

    private static final String PROPERTY_CLIENT_ID = "client_id";
    private static final String PROPERTY_CLIENT_SECRET = "client_secret";
    private static final String PROPERTY_AUTHORIZATION_URL = "authorization_url";
    private static final String PROPERTY_REDIRECT_URL = "redirect_url";

    private Context context;

    public AuthorizationPreferencesProviderImpl(Context context) {
        verifyArguments(context);
        saveArguments(context);
    }

    private void saveArguments(Context context) {
        if (!(context instanceof Application)) {
            this.context = context.getApplicationContext();
        } else {
            this.context = context;
        }
    }

    private void verifyArguments(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
    }

    @Override
    public String getClientId() {
        return getSharedPreferences().getString(PROPERTY_CLIENT_ID, null);
    }

    @Override
    public void setClientId(String clientId) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_CLIENT_ID, clientId);
        editor.commit();
    }

    @Override
    public String getClientSecret() {
        return getSharedPreferences().getString(PROPERTY_CLIENT_SECRET, null);
    }

    @Override
    public void setClientSecret(String clientSecret) {
        final SharedPreferences prefs = getSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_CLIENT_SECRET, clientSecret);
        editor.commit();
    }

    @Override
    public String getAuthorizationUrl() {
        return getSharedPreferences().getString(PROPERTY_AUTHORIZATION_URL, null);
    }

    @Override
    public void setAuthorizationUrl(String authorizationUrl) {
        final SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_AUTHORIZATION_URL, authorizationUrl);
        editor.commit();
    }

    @Override
    public String getRedirectUrl() {
        return getSharedPreferences().getString(PROPERTY_REDIRECT_URL, null);
    }

    @Override
    public void setRedirectUrl(String redirectUrl) {
        final SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REDIRECT_URL, redirectUrl);
        editor.commit();
    }

    public void clear() {
        getSharedPreferences().edit().clear().commit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(TAG_NAME, Context.MODE_PRIVATE);
    }
}
