/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Application;
import android.content.Context;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class ApiProviderImpl implements ApiProvider {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final Context context;

    public ApiProviderImpl(Context context) {
        this.context = getApplicationContext(context);
    }

    private Context getApplicationContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context may not be null");
        }
        if (context instanceof Application) {
            return context;
        } else {
            return context.getApplicationContext();
        }
    }

    @Override
    public HttpTransport getTransport() {
        return HTTP_TRANSPORT;
    }

    @Override
    public HttpRequestFactory getFactory(Credential credential) {
        return HTTP_TRANSPORT.createRequestFactory(credential);
    }

    @Override
    public AuthorizedApiRequest getAuthorizedApiRequest(AuthorizationPreferencesProvider authorizationPreferencesProvider) throws AuthorizationException {
        return new AuthorizedApiRequestImpl(context, authorizationPreferencesProvider, this);
    }
}
