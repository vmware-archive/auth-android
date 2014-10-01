package io.pivotal.android.auth;

import android.content.Context;

/* package */ class TokenProviderFactory {

    private static TokenProvider sProvider;

    public static void init(final TokenProvider provider) {
        sProvider = provider;
    }

    public static TokenProvider get(final Context context) {
        if (sProvider == null) {
            return new TokenProvider.Default(context);
        } else {
            return sProvider;
        }
    }
}
