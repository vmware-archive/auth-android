/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Activity;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public interface AuthorizedApiRequest {

    public interface AuthorizationListener {
        public void onSuccess(TokenResponse tokenResponse);
        public void onAuthorizationDenied();
        public void onFailure(String reason);
    }

    public interface HttpOperationListener {
        public void onSuccess(int httpStatusCode, String contentType, String contentEncoding, InputStream result);
        public void onUnauthorized();
        public void onFailure(String reason);
    }

    public interface LoadCredentialListener {
        public void onCredentialLoaded(Credential credential);
    }

    public interface ClearSavedCredentialListener {
        public void onSavedCredentialCleared();
    }

    public void obtainAuthorization(Activity activity);

    public void getAccessToken(String authorizationCode, AuthorizationListener listener);

    public void executeHttpRequest(String method,
                                   URL url,
                                   Map<String, Object> headers,
                                   String contentType,
                                   String contentEncoding,
                                   byte[] contentData,
                                   Credential credential,
                                   AuthorizationPreferencesProvider authorizationPreferencesProvider,
                                   HttpOperationListener listener);

    public void loadCredential(LoadCredentialListener listener);

    public void clearSavedCredentialAsynchronously(ClearSavedCredentialListener listener);

    public void clearSavedCredentialSynchronously();
}
