/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class AccountsProxyHolderTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testWithInitialization() {
        final AccountsProxy custom = Mockito.mock(AccountsProxy.class);
        AccountsProxyHolder.init(custom);
        final AccountsProxy proxy = AccountsProxyHolder.get(mContext);
        assertEquals(custom, proxy);
    }

    public void testWithoutInitialization() {
        AccountsProxyHolder.init(null);
        final AccountsProxy instance = AccountsProxyHolder.get(mContext);
        final AccountsProxy.Default proxy = (AccountsProxy.Default) instance;
        assertNotNull(proxy);
    }
}
