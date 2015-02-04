package io.pivotal.android.auth;

import android.content.Context;

/* package */ class AccountsProxyFactory {

    private static AccountsProxy sProxy;

    public static void init(final AccountsProxy proxy) {
        sProxy = proxy;
    }

    public static AccountsProxy get(final Context context) {
        if (sProxy == null) {
            return new AccountsProxy.Default(context);
        } else {
            return sProxy;
        }
    }
}
