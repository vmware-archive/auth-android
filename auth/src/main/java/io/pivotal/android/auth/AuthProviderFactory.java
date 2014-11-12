package io.pivotal.android.auth;

/* package */ class AuthProviderFactory {

    private static AuthProvider sProvider;

    public static void init(final AuthProvider provider) {
        sProvider = provider;
    }

    public static AuthProvider get() {
        if (sProvider == null) {
            return new AuthProvider.Default();
        } else {
            return sProvider;
        }
    }
}
