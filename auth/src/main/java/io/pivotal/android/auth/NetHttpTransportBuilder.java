/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import com.google.api.client.http.javanet.NetHttpTransport;

import java.security.GeneralSecurityException;
import java.security.KeyStore;

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
        mBuilder.trustCertificates(keyStore);
        return this;
    }

    public NetHttpTransport build() {
        return mBuilder.build();
    }
}
