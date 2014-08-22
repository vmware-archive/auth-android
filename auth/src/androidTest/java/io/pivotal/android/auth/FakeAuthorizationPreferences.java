/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

public class FakeAuthorizationPreferences implements AuthorizationPreferencesProvider {

    private String clientId;
    private String clientSecret;
    private String authorizationUrl;
    private String redirectUrl;

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    @Override
    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

}
