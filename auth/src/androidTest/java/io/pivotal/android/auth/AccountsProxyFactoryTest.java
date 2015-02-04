/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.test.AndroidTestCase;

import org.mockito.Mockito;

public class AccountsProxyFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testWithInitialization() {
        final AccountsProxy custom = Mockito.mock(AccountsProxy.class);
        AccountsProxyFactory.init(custom);
        final AccountsProxy proxy = AccountsProxyFactory.get(mContext);
        assertEquals(custom, proxy);
    }

    public void testWithoutInitialization() {
        AccountsProxyFactory.init(null);
        final AccountsProxy instance = AccountsProxyFactory.get(mContext);
        final AccountsProxy.Default proxy = (AccountsProxy.Default) instance;
        assertNotNull(proxy);
    }
}
