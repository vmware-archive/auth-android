package io.pivotal.android.auth;

import android.content.Context;

/* package */ class AccountsProxyHolder {

    private static final Object LOCK = new Object();
    private static AccountsProxy sProxy;

    public static void init(final AccountsProxy proxy) {
        sProxy = proxy;
    }

    public static AccountsProxy get(final Context context) {
        if (sProxy == null) {
            synchronized (LOCK) {
                sProxy = new AccountsProxy.Default(context);
            }
        }
        return sProxy;
    }
}
