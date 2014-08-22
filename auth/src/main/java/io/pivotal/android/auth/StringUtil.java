/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */

package io.pivotal.android.auth;

class StringUtil {

    public static String trimTrailingSlashes(String str) {
        if (str == null) {
            return null;
        }
        while(str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
