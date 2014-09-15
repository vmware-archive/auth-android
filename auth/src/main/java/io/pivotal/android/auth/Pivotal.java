/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* package */ class Pivotal {

    public static final class Property {
        public static final String CLIENT_ID = Pivotal.property("pivotal.auth.clientId");
        public static final String CLIENT_SECRET = Pivotal.property("pivotal.auth.clientSecret");
        public static final String SERVICE_URL = Pivotal.property("pivotal.auth.serviceUrl");
        public static final String REDIRECT_URL = Pivotal.property("pivotal.auth.redirectUrl");
        public static final String ACCOUNT_TYPE = Pivotal.property("pivotal.auth.accountType");
        public static final String TOKEN_TYPE = Pivotal.property("pivotal.auth.tokenType");
    }

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
            } catch (IOException e) {
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

    private static String property(final String key) {
        final String value = getProperties().getProperty(key);
        if (TextUtils.isEmpty(value)) {
            throw new IllegalStateException("'" + key + "' not found in pivotal.properties");
        }
        return value;
    }
}
