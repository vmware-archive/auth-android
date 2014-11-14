/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class TokenProviderFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testWithInitialization() {
        final TokenProvider custom = Mockito.mock(TokenProvider.class);
        TokenProviderFactory.init(custom);
        final TokenProvider provider = TokenProviderFactory.get(mContext);
        assertEquals(custom, provider);
    }

    public void testWithoutInitialization() {
        TokenProviderFactory.init(null);
        final TokenProvider instance = TokenProviderFactory.get(mContext);
        final TokenProvider.Default provider = (TokenProvider.Default) instance;
        assertNotNull(provider);
    }
}
