package io.pivotal.android.auth;

/* package */ class RemoteAuthenticatorFactory {

    private static RemoteAuthenticator sAuthenticator;

    public static void init(final RemoteAuthenticator authenticator) {
        sAuthenticator = authenticator;
    }

    public static RemoteAuthenticator get() {
        if (sAuthenticator == null) {
            return new RemoteAuthenticator.Default();
        } else {
            return sAuthenticator;
        }
    }
}
