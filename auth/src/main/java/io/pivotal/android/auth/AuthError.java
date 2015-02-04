/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

public class AuthError extends Error {

    public AuthError(final Exception e) {
        super(e.getLocalizedMessage(), e);
    }
}
