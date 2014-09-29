/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

public class TokenProviderFactoryTest extends AndroidTestCase {

    public void testWithInitialization() {
        final TokenProvider custom = new MockTokenProvider();
        TokenProviderFactory.init(custom);
        final TokenProvider provider = TokenProviderFactory.get(mContext);
        assertEquals(custom, provider);
    }

    public void testWithoutInitialization() {
        TokenProviderFactory.init(null);
        final TokenProvider instance = TokenProviderFactory.get(mContext);
        final TokenManager manager = (TokenManager) instance;
        assertNotNull(manager);
    }
}
