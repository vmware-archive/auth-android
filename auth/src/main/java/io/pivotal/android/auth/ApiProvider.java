/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;

public interface ApiProvider {

    public HttpTransport getTransport();
    public HttpRequestFactory getFactory(Credential credential);
    public AuthorizedApiRequest getAuthorizedApiRequest(AuthorizationPreferencesProvider authorizationPreferencesProvider) throws AuthorizationException;

}
