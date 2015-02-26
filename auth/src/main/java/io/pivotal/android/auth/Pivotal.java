/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* package */ class Pivotal {

    private static final class Keys {
        private static final String CLIENT_ID = "pivotal.auth.clientId";
        private static final String CLIENT_SECRET = "pivotal.auth.clientSecret";
        private static final String AUTHORIZE_URL = "pivotal.auth.authorizeUrl";
        private static final String TOKEN_URL = "pivotal.auth.tokenUrl";
        private static final String REDIRECT_URL = "pivotal.auth.redirectUrl";
        private static final String ACCOUNT_TYPE = "pivotal.auth.accountType";
        private static final String TOKEN_TYPE = "pivotal.auth.tokenType";
    }


    private static final String[] LOCATIONS = {
            "assets/pivotal.properties", "res/raw/pivotal.properties"
    };

    private static Properties sProperties;

    /* package */ static Properties getProperties() {
        if (sProperties == null) {
            sProperties = loadProperties();
        }
        return sProperties;
    }

    /* package */ static void setProperties(final Properties properties) {
        sProperties = properties;
    }

    private static Properties loadProperties() {
        for (final String path : LOCATIONS) {
            try {
                return loadProperties(path);
            } catch (final Exception e) {
                Logger.ex(e);
            }
        }
        throw new IllegalStateException("Could not find pivotal.properties file.");
    }

    private static Properties loadProperties(final String path) throws IOException {
        Logger.e("Loading properties: " + path);
        final Properties properties = new Properties();
        properties.load(getInputStream(path));
        return properties;
    }

    private static InputStream getInputStream(final String path) {
        final Thread currentThread = Thread.currentThread();
        Logger.e("Current Thread: " + currentThread.getName());
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

    public static String getClientId() {
        return get(Keys.CLIENT_ID);
    }

    public static String getClientSecret() {
        return get(Keys.CLIENT_SECRET);
    }

    public static String getAuthorizeUrl() {
        return get(Keys.AUTHORIZE_URL);
    }

    public static String getTokenUrl() {
        return get(Keys.TOKEN_URL);
    }

    public static String getRedirectUrl() {
        return get(Keys.REDIRECT_URL);
    }

    public static String getAccountType() {
        return get(Keys.ACCOUNT_TYPE);
    }

    public static String getTokenType() {
        return get(Keys.TOKEN_TYPE);
    }
}
