package io.pivotal.android.auth;

import android.content.Context;

/* package */ class RemoteAuthenticatorHolder {

    private static final Object LOCK = new Object();
    private static RemoteAuthenticator sAuthenticator;

    public static void init(final RemoteAuthenticator authenticator) {
        sAuthenticator = authenticator;
    }

    public static RemoteAuthenticator get(final Context context) {
        if (sAuthenticator == null) {
            synchronized (LOCK) {
                sAuthenticator = new RemoteAuthenticator.Default(context);
            }
        }
        return sAuthenticator;
    }
}
