/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.text.TextUtils;

public class Response {
    public String accessToken;
    public String accountName;
    public AuthError error;

    public Response(final AuthError error) {
        this.error = error;
    }

    public Response(final String accessToken, final String accountName) {
        this.accessToken = accessToken;
        this.accountName = accountName;
    }

    public boolean isTokenExpired() {
        return TextUtils.isEmpty(this.accessToken) || TokenUtil.isExpired(this.accessToken);
    }

    public boolean isSuccess() {
        return this.error == null;
    }

    public boolean isFailure() {
        return this.error != null;
    }
}
