/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Intent;
import android.test.ServiceTestCase;

public class AccountAuthenticatorServiceTest extends ServiceTestCase<AccountAuthenticatorService> {

    public AccountAuthenticatorServiceTest() {
        super(AccountAuthenticatorService.class);
    }

    public void testBindService() {
        assertNull(getService());
        assertNotNull(bindService(new Intent()));
        assertNotNull(getService());
    }
}
