/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* package */ class Pivotal {

    public static final String PROP_CLIENT_ID = "pivotal.auth.clientId";
    public static final String PROP_CLIENT_SECRET = "pivotal.auth.clientSecret";
    public static final String PROP_AUTHORIZE_URL = "pivotal.auth.authorizeUrl";
    public static final String PROP_TOKEN_URL = "pivotal.auth.tokenUrl";
    public static final String PROP_REDIRECT_URL = "pivotal.auth.redirectUrl";
    public static final String PROP_ACCOUNT_TYPE = "pivotal.auth.accountType";
    public static final String PROP_TOKEN_TYPE = "pivotal.auth.tokenType";


    private static String[] sLocations = {
            "assets/pivotal.properties", "res/raw/pivotal.properties"
    };

    private static Properties sProperties;

    private static Properties getProperties() {
        if (sProperties == null) {
            sProperties = loadProperties();
        }
        return sProperties;
    }

    private static Properties loadProperties() {
        for (final String path : sLocations) {
            try {
                return loadProperties(path);
            } catch (final Exception e) {
                Logger.ex(e);
            }
        }
        throw new IllegalStateException("Could not find pivotal.properties file.");
    }

    private static Properties loadProperties(final String path) throws IOException {
        final Properties properties = new Properties();
        properties.load(getInputStream(path));
        return properties;
    }

    private static InputStream getInputStream(final String path) {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader loader = currentThread.getContextClassLoader();
        return loader.getResourceAsStream(path);
    }

    public static String get(final String key) {
        final String value = getProperties().getProperty(key);
        if (TextUtils.isEmpty(value)) {
            throw new IllegalStateException("'" + key + "' not found in pivotal.properties");
        }
        return value;
    }
}
