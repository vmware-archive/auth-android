/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String detailMessage) {
        super(detailMessage);
    }
}
