/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;

public class KeyStoreFactory {

    private final Context mContext;

    public KeyStoreFactory(final Context context) {
        mContext = context;
    }

    public KeyStore getKeyStore() throws Exception {
        final String defaultType = KeyStore.getDefaultType();
        final KeyStore keyStore = KeyStore.getInstance(defaultType);
        keyStore.load(null, null);

        loadCertificates(keyStore);

        return keyStore;
    }

    private void loadCertificates(final KeyStore keyStore) throws Exception {
        final CertificateFactory certificateFactory = getCertificateFactory();

        final List<String> certificateNames = Pivotal.getPinnedSslCertificateNames();
        for (final String certificateName : certificateNames) {

            loadCertificate(keyStore, certificateFactory, certificateName);
        }
    }

    private CertificateFactory getCertificateFactory() throws Exception {
        return CertificateFactory.getInstance("X.509");
    }

    private void loadCertificate(final KeyStore keyStore, final CertificateFactory certificateFactory, final String certificateName) throws Exception {
        final InputStream inputStream = mContext.getAssets().open(certificateName);
        try {
            final Certificate certificate = certificateFactory.generateCertificate(inputStream);
            keyStore.setCertificateEntry(certificateName, certificate);
        } catch (final Exception e) {
            Logger.ex(e);
        } finally {
            inputStream.close();
        }
    }

}
