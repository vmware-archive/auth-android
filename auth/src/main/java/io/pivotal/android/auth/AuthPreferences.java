/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;
import android.content.SharedPreferences;

/* package */ class AuthPreferences {

    public static final String AUTH = "io.pivotal.android.auth";

    public static final class Keys {
        public static final String LAST_USED_ACCOUNT = "last_used_account";
    }

    public static void setLastUsedAccountName(final Context context, final String name) {
        if (context != null) {
            final SharedPreferences preferences = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
            preferences.edit().putString(Keys.LAST_USED_ACCOUNT, name).apply();
        }
    }

    public static String getLastUsedAccountName(final Context context) {
        if (context != null) {
            final SharedPreferences preferences = context.getSharedPreferences(AUTH, Context.MODE_PRIVATE);
            return preferences.getString(Keys.LAST_USED_ACCOUNT, "");
        } else {
            return "";
        }
    }
}