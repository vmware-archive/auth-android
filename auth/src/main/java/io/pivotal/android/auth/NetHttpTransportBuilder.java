/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.SslUtils;

import org.apache.http.conn.ssl.StrictHostnameVerifier;

import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class NetHttpTransportBuilder {

    private final NetHttpTransport.Builder mBuilder;

    public NetHttpTransportBuilder() {
        mBuilder = new NetHttpTransport.Builder();
    }

    public NetHttpTransportBuilder doNotValidateCertificate() throws GeneralSecurityException {
        mBuilder.doNotValidateCertificate();
        return this;
    }

    public NetHttpTransportBuilder trustCertificates(final KeyStore keyStore) throws GeneralSecurityException {
        mBuilder.setSslSocketFactory(getSslSocketFactory(keyStore));
        mBuilder.setHostnameVerifier(new StrictHostnameVerifier());
        return this;
    }

    private SSLSocketFactory getSslSocketFactory(final KeyStore keyStore) throws GeneralSecurityException {
        final SSLContext sslContext = SslUtils.getTlsSslContext();
        final TrustManagerFactory trustFactory = SslUtils.getDefaultTrustManagerFactory();
        SslUtils.initSslContext(sslContext, keyStore, trustFactory);
        return sslContext.getSocketFactory();
    }

    public NetHttpTransport build() {
        return mBuilder.build();
    }
}
