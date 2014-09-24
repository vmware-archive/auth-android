/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

/* package */  interface TokenListener {
    public void onAuthorizationComplete(final Token token);

    public void onAuthorizationFailed(final Error error);
}
