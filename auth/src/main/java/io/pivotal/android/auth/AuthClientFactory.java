package io.pivotal.android.auth;

import android.content.Context;

/* package */ class AuthClientFactory {

    private static AuthClient sClient;

    public static void init(final AuthClient client) {
        sClient = client;
    }

    public static AuthClient get(final Context context) {
        if (sClient == null) {
            return new AuthClient.Default(context);
        } else {
            return sClient;
        }
    }
}
