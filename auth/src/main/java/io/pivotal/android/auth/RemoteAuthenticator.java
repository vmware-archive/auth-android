/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.UUID;

public interface RemoteAuthenticator {

    public PasswordTokenRequest newPasswordTokenRequest(String username, String password);

    public RefreshTokenRequest newRefreshTokenRequest(String refreshToken);

    public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(String authorizationCode);

    public AuthorizationCodeRequestUrl newAuthorizationCodeUrl();

    public static class Default implements RemoteAuthenticator {

        private static final Object LOCK = new Object();
        private static final JsonFactory JSON_FACTORY = new JacksonFactory();
        private static final GenericUrl TOKEN_URL = new GenericUrl(Pivotal.getTokenUrl());
        private static final HttpExecuteInterceptor INTERCEPTOR = new BasicAuthentication(Pivotal.getClientId(), Pivotal.getClientSecret());
        private static final Credential.AccessMethod METHOD = BearerToken.authorizationHeaderAccessMethod();

        private static HttpTransport sTransport;
        private final Context mContext;

        public Default(final Context context) {
            mContext = context;
        }

        protected HttpTransport getHttpTransport() {
            if (sTransport == null) {
                synchronized (LOCK) {
                    sTransport = createHttpTransport();
                }
            }
            return sTransport;
        }

        protected NetHttpTransport createHttpTransport() {
            try {
                final NetHttpTransportBuilder builder = createNetHttpTransportBuilder();

                if (Pivotal.trustAllSslCertificates()) {
                    builder.doNotValidateCertificate();
                }

                if (Pivotal.getPinnedSslCertificateNames().size() > 0) {
                    builder.trustCertificates(createKeyStore());
                }

                return builder.build();
            } catch (final Exception e) {
                throw new IllegalStateException(e);
            }
        }

        protected NetHttpTransportBuilder createNetHttpTransportBuilder() {
            return new NetHttpTransportBuilder();
        }

        protected KeyStore createKeyStore() throws Exception {
            return new KeyStoreFactory(mContext).getKeyStore();
        }

        @Override
        public PasswordTokenRequest newPasswordTokenRequest(final String username, final String password) {
            final PasswordTokenRequest request = new PasswordTokenRequest(getHttpTransport(), JSON_FACTORY, TOKEN_URL, username, password);
            request.set("client_id", Pivotal.getClientId());
            request.set("client_secret", Pivotal.getClientSecret());
            request.setClientAuthentication(INTERCEPTOR);
            request.setScopes(Arrays.asList(Pivotal.getScopes().split(" ")));
            return request;
        }

        @Override
        public RefreshTokenRequest newRefreshTokenRequest(final String refreshToken) {
            final RefreshTokenRequest request = new RefreshTokenRequest(getHttpTransport(), JSON_FACTORY, TOKEN_URL, refreshToken);
            request.set("client_id", Pivotal.getClientId());
            request.set("client_secret", Pivotal.getClientSecret());
            request.setClientAuthentication(INTERCEPTOR);
            return request;
        }

        @Override
        public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(final String authorizationCode) {
            final DefaultAuthorizationCodeFlow flow = new DefaultAuthorizationCodeFlow();
            final AuthorizationCodeTokenRequest request = flow.newTokenRequest(authorizationCode);
            request.set("client_id", Pivotal.getClientId());
            request.set("client_secret", Pivotal.getClientSecret());
            return request;
        }

        @Override
        public AuthorizationCodeRequestUrl newAuthorizationCodeUrl() {
            final DefaultAuthorizationCodeFlow flow = new DefaultAuthorizationCodeFlow();
            return flow.newAuthorizationUrl();
        }

        private final class DefaultAuthorizationCodeFlow extends AuthorizationCodeFlow {

            public DefaultAuthorizationCodeFlow() {
                super(new Builder(METHOD, getHttpTransport(), JSON_FACTORY, TOKEN_URL, INTERCEPTOR, Pivotal.getClientId(), Pivotal.getAuthorizeUrl()).setScopes(Arrays.asList(Pivotal.getScopes().split(" "))));
            }

            @Override
            public AuthorizationCodeTokenRequest newTokenRequest(final String authorizationCode) {
                final AuthorizationCodeTokenRequest request = super.newTokenRequest(authorizationCode);
                request.setRedirectUri(Pivotal.getRedirectUrl());
                return request;
            }

            @Override
            public AuthorizationCodeRequestUrl newAuthorizationUrl() {
                final AuthorizationCodeRequestUrl requestUrl = super.newAuthorizationUrl();
                requestUrl.setRedirectUri(Pivotal.getRedirectUrl());
                requestUrl.setState(UUID.randomUUID().toString());
                requestUrl.set("access_type", "offline");
                return requestUrl;
            }
        }
    }
}
