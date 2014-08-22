/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */

package io.pivotal.android.auth;

import android.test.AndroidTestCase;

public class StringUtilTest extends AndroidTestCase {

    public void testTrimTrailingCharacters() {
        assertNull(StringUtil.trimTrailingSlashes(null));
        assertEquals("", StringUtil.trimTrailingSlashes(""));
        assertEquals("", StringUtil.trimTrailingSlashes("/"));
        assertEquals("", StringUtil.trimTrailingSlashes("//"));
        assertEquals("A", StringUtil.trimTrailingSlashes("A"));
        assertEquals("A", StringUtil.trimTrailingSlashes("A/"));
        assertEquals("A", StringUtil.trimTrailingSlashes("A//"));
        assertEquals("AA", StringUtil.trimTrailingSlashes("AA"));
        assertEquals("AA", StringUtil.trimTrailingSlashes("AA/"));
        assertEquals("AA", StringUtil.trimTrailingSlashes("AA//"));
    }
}
