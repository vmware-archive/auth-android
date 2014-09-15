/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

public interface TokenListener {
    public void onAuthorizationComplete(final String accessToken);

    public void onAuthorizationFailed(final Error error);
}
