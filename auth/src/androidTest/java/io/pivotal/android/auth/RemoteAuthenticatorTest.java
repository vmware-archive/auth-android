/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.mockito.Mockito;

import java.security.KeyStore;
import java.util.Properties;
import java.util.UUID;

public class RemoteAuthenticatorTest extends AndroidTestCase {

    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private static final String CLIENT_SECRET = UUID.randomUUID().toString();
    private static final String TOKEN_URL = "http://" + UUID.randomUUID().toString() + ".com";
    private static final String AUTHORIZE_URL = "http://" + UUID.randomUUID().toString() + ".com";
    private static final String REDIRECT_URL = "http://" + UUID.randomUUID().toString() + ".com";
    private static final String SCOPE = UUID.randomUUID().toString();

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();
    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();
    private static final String AUTH_CODE = UUID.randomUUID().toString();

    private Properties mProperties;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());

        mProperties = new Properties();
        mProperties.setProperty("pivotal.auth.clientId", CLIENT_ID);
        mProperties.setProperty("pivotal.auth.clientSecret", CLIENT_SECRET);
        mProperties.setProperty("pivotal.auth.tokenUrl", TOKEN_URL);
        mProperties.setProperty("pivotal.auth.authorizeUrl", AUTHORIZE_URL);
        mProperties.setProperty("pivotal.auth.redirectUrl", REDIRECT_URL);
        mProperties.setProperty("pivotal.auth.scopes", SCOPE);

        Pivotal.setProperties(mProperties);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        Pivotal.setProperties(null);
    }

    public void testCreateHttpTransportWithTrustAllSslCertificates() throws Exception {
        mProperties.setProperty("pivotal.auth.trustAllSslCertificates", "true");

        final NetHttpTransport transport = new NetHttpTransport();
        final NetHttpTransportBuilder builder = Mockito.mock(NetHttpTransportBuilder.class);
        final RemoteAuthenticator.Default authenticator = Mockito.spy(new RemoteAuthenticator.Default(mContext));

        Mockito.doReturn(builder).when(authenticator).createNetHttpTransportBuilder();
        Mockito.when(builder.build()).thenReturn(transport);

        assertEquals(transport, authenticator.createHttpTransport());

        Mockito.verify(builder).doNotValidateCertificate();
        Mockito.verify(builder).build();
    }

    public void testCreateHttpTransportWithPinnedSslCertificateNames() throws Exception {
        mProperties.setProperty("pivotal.auth.pinnedSslCertificateNames", "test1.cer");

        final KeyStore keyStore = Mockito.mock(KeyStore.class);
        final NetHttpTransport transport = new NetHttpTransport();
        final NetHttpTransportBuilder builder = Mockito.mock(NetHttpTransportBuilder.class);
        final RemoteAuthenticator.Default authenticator = Mockito.spy(new RemoteAuthenticator.Default(mContext));

        Mockito.doReturn(keyStore).when(authenticator).createKeyStore();
        Mockito.doReturn(builder).when(authenticator).createNetHttpTransportBuilder();
        Mockito.when(builder.build()).thenReturn(transport);

        assertEquals(transport, authenticator.createHttpTransport());

        Mockito.verify(builder).trustCertificates(keyStore);
        Mockito.verify(builder).build();
    }

    public void testCreateHttpTransportDefault() throws Exception {
        final NetHttpTransport transport = new NetHttpTransport();
        final NetHttpTransportBuilder builder = Mockito.mock(NetHttpTransportBuilder.class);
        final RemoteAuthenticator.Default authenticator = Mockito.spy(new RemoteAuthenticator.Default(mContext));

        Mockito.doReturn(builder).when(authenticator).createNetHttpTransportBuilder();
        Mockito.when(builder.build()).thenReturn(transport);

        assertEquals(transport, authenticator.createHttpTransport());

        Mockito.verify(builder, Mockito.never()).doNotValidateCertificate();
        Mockito.verify(builder, Mockito.never()).trustCertificates(Mockito.any(KeyStore.class));
        Mockito.verify(builder).build();
    }

    public void testGetHttpTransportReturnsAStaticInstance() {
        final RemoteAuthenticator.Default authenticator = Mockito.spy(new RemoteAuthenticator.Default(mContext));
        final RemoteAuthenticator.Default anotherAuthenticator = Mockito.spy(new RemoteAuthenticator.Default(mContext));

        assertEquals(authenticator.getHttpTransport(), anotherAuthenticator.getHttpTransport());
    }

    public void testCreateNewPasswordTokenRequest() throws Exception {
        final RemoteAuthenticator.Default provider = new RemoteAuthenticator.Default(mContext);
        final PasswordTokenRequest request = provider.newPasswordTokenRequest(USERNAME, PASSWORD);

        assertEquals(CLIENT_ID, request.get("client_id"));
        assertEquals(CLIENT_SECRET, request.get("client_secret"));

        assertEquals(USERNAME, request.getUsername());
        assertEquals(PASSWORD, request.getPassword());
        assertEquals(SCOPE, request.getScopes());
    }

    public void testCreateNewRefreshTokenRequest() throws Exception {
        final RemoteAuthenticator.Default provider = new RemoteAuthenticator.Default(mContext);
        final RefreshTokenRequest request = provider.newRefreshTokenRequest(REFRESH_TOKEN);

        assertEquals(CLIENT_ID, request.get("client_id"));
        assertEquals(CLIENT_SECRET, request.get("client_secret"));

        assertEquals(REFRESH_TOKEN, request.getRefreshToken());
    }

    public void testCreateNewAuthorizationCodeTokenRequest() throws Exception {
        final RemoteAuthenticator provider = new RemoteAuthenticator.Default(mContext);
        final AuthorizationCodeTokenRequest request = provider.newAuthorizationCodeTokenRequest(AUTH_CODE);

        assertEquals(CLIENT_ID, request.get("client_id"));
        assertEquals(CLIENT_SECRET, request.get("client_secret"));

        assertEquals(AUTH_CODE, request.getCode());
        assertEquals(REDIRECT_URL, request.getRedirectUri());
    }

    public void testCreateNewAuthorizationCodeRequestUrl() throws Exception {
        final RemoteAuthenticator provider = new RemoteAuthenticator.Default(mContext);
        final AuthorizationCodeRequestUrl url = provider.newAuthorizationCodeUrl();

        assertEquals(REDIRECT_URL, url.getRedirectUri());
        assertEquals(SCOPE, url.getScopes());
        assertEquals("offline", url.get("access_type"));
    }
}