/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/* package */ class TokenUtil {

    public static boolean isExpired(final String token) {
        try {
            final DecodedToken decoded = getDecodedToken(token);
            final long timeDifference = getTimeDifference(decoded.exp);
            Logger.v("Token expires in " + (timeDifference / 60) + " minutes");
            return timeDifference < 30; // expired if valid for less than 30 seconds
        } catch (final Exception e) {
            Logger.ex(e);
            return false;
        }
    }

    private static DecodedToken getDecodedToken(final String token) throws Exception {
        final String[] parts = token.split("\\.");
        final byte[] bytes = Base64.decode(parts[1], Base64.DEFAULT);
        return new ObjectMapper().readValue(bytes, DecodedToken.class);
    }

    private static long getTimeDifference(final String expiration) {
        final long expirationTime = Long.parseLong(expiration);
        final long currentTime = System.currentTimeMillis() / 1000;
        return expirationTime - currentTime;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class DecodedToken {
        public String exp;
    }
}
