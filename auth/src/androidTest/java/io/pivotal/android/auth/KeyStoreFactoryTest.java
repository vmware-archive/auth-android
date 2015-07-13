/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Properties;

public class KeyStoreFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        Pivotal.setProperties(null);
    }

    public void testKeyStoreLoadsCorrectCertificates() throws Exception {
        final String certName1 = "test1.cer";
        final String certName2 = "test2.cer";
        final Properties properties = new Properties();
        properties.setProperty("pivotal.auth.pinnedSslCertificateNames", certName1 + " " + certName2);

        Pivotal.setProperties(properties);

        final KeyStoreFactory factory = new KeyStoreFactory(mContext);
        final KeyStore keyStore = factory.getKeyStore();
        final Certificate certificate1 = keyStore.getCertificate(certName1);
        final Certificate certificate2 = keyStore.getCertificate(certName2);

        final InputStream inputStream1 = getContext().getAssets().open(certName1);
        final CertificateFactory certificateFactory1 = CertificateFactory.getInstance("X.509");
        final Certificate expectedCertificate1 = certificateFactory1.generateCertificate(inputStream1);
        inputStream1.close();

        final InputStream inputStream2 = getContext().getAssets().open(certName2);
        final CertificateFactory certificateFactory2 = CertificateFactory.getInstance("X.509");
        final Certificate expectedCertificate2 = certificateFactory2.generateCertificate(inputStream2);
        inputStream2.close();

        assertEquals(expectedCertificate1, certificate1);
        assertEquals(expectedCertificate2, certificate2);
    }
}
