/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Activity;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;

import java.net.URL;
import java.util.Map;

public class FakeAuthorizedApiRequest implements AuthorizedApiRequest {

    private final FakeApiProvider apiProvider;
    private final boolean shouldGetAccessTokenBeSuccessful;
    private final boolean shouldGetAccessTokenBeUnauthorized;
    private final boolean shouldAuthorizedApiRequestBeSuccessful;
    private final boolean shouldAuthorizedApiRequestBeUnauthorized;
    private final String resultContentData;
    private final String resultContentEncoding;
    private final String resultContentType;
    private final int returnedHttpStatusCode;
    private boolean didCallObtainAuthorization;
    private boolean didCallGetAccessToken;
    private TokenResponse tokenResponseToReturn;
    private TokenResponse savedTokenResponse;
    private Map<String, Object> requestHeaders;
    private String requestContentType;
    private String requestContentEncoding;
    private URL requestUrl;

    public FakeAuthorizedApiRequest(FakeApiProvider apiProvider,
                                    boolean shouldGetAccessTokenBeSuccessful,
                                    boolean shouldGetAccessTokenBeUnauthorized,
                                    boolean shouldAuthorizedApiRequestBeSuccessful,
                                    boolean shouldAuthorizedApiRequestBeUnauthorized,
                                    int resultHttpStatus,
                                    String resultContentType,
                                    String resultContentEncoding,
                                    String resultContentData,
                                    TokenResponse savedTokenResponse,
                                    TokenResponse tokenResponseToReturn) {

        this.apiProvider = apiProvider;
        this.shouldGetAccessTokenBeSuccessful = shouldGetAccessTokenBeSuccessful;
        this.shouldGetAccessTokenBeUnauthorized = shouldGetAccessTokenBeUnauthorized;
        this.shouldAuthorizedApiRequestBeSuccessful = shouldAuthorizedApiRequestBeSuccessful;
        this.shouldAuthorizedApiRequestBeUnauthorized = shouldAuthorizedApiRequestBeUnauthorized;
        this.returnedHttpStatusCode = resultHttpStatus;
        this.resultContentType = resultContentType;
        this.resultContentEncoding = resultContentEncoding;
        this.resultContentData = resultContentData;
        this.savedTokenResponse = savedTokenResponse;
        this.tokenResponseToReturn = tokenResponseToReturn;
    }

    public void obtainAuthorization(Activity activity) {
        didCallObtainAuthorization = true;
    }

    @Override
    public void getAccessToken(String authorizationCode, AuthorizationListener listener) {
        didCallGetAccessToken = true;
        if (shouldGetAccessTokenBeSuccessful) {
            savedTokenResponse = tokenResponseToReturn;
            listener.onSuccess(tokenResponseToReturn);
        } else if (shouldGetAccessTokenBeUnauthorized) {
            listener.onAuthorizationDenied();
        } else {
            listener.onFailure("Fake request failed fakely.");
        }
    }

    @Override
    public void executeHttpRequest(String method,
                                   URL url,
                                   Map<String, Object> headers,
                                   String contentType,
                                   String contentEncoding,
                                   byte[] contentData,
                                   Credential credential,
                                   AuthorizationPreferencesProvider authorizationPreferencesProvider,
                                   HttpOperationListener listener) {

        this.requestUrl = url;
        this.requestHeaders = headers;
        this.requestContentType = contentType;
        this.requestContentEncoding = contentEncoding;
        if (shouldAuthorizedApiRequestBeSuccessful) {
            listener.onSuccess(returnedHttpStatusCode, resultContentType, resultContentEncoding, StreamUtil.getInputStream(resultContentData));
        } else if (shouldAuthorizedApiRequestBeUnauthorized) {
            listener.onUnauthorized();
        } else {
            listener.onFailure("Fake request failed fakely.");
        }
    }

    @Override
    public void loadCredential(LoadCredentialListener listener) {
        listener.onCredentialLoaded(apiProvider.getCredential());
    }

    @Override
    public void clearSavedCredentialAsynchronously(ClearSavedCredentialListener listener) {
        clearSavedCredentialSynchronously();
        if (listener != null) {
            listener.onSavedCredentialCleared();
        }
    }

    @Override
    public void clearSavedCredentialSynchronously() {
        savedTokenResponse = null;
        apiProvider.setCredential(null);
    }

    public TokenResponse getSavedTokenResponse() {
        return savedTokenResponse;
    }

    public boolean didCallObtainAuthorization() {
        return didCallObtainAuthorization;
    }

    public boolean didCallGetAccessToken() {
        return didCallGetAccessToken;
    }

    public Map<String, Object> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public String getRequestContentEncoding() {
        return requestContentEncoding;
    }

    public int getReturnedHttpStatusCode() {
        return returnedHttpStatusCode;
    }

    public URL getRequestUrl() {
        return requestUrl;
    }
}
