package io.pivotal.android.auth;

/* package */ class RemoteAuthenticatorHolder {

    private static final Object LOCK = new Object();
    private static RemoteAuthenticator sAuthenticator;

    public static void init(final RemoteAuthenticator authenticator) {
        sAuthenticator = authenticator;
    }

    public static RemoteAuthenticator get() {
        if (sAuthenticator == null) {
            synchronized (LOCK) {
                sAuthenticator = new RemoteAuthenticator.Default();
            }
        }
        return sAuthenticator;
    }
}
