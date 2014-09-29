/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.test.AndroidTestCase;

public class ListenerCallbackTest extends AndroidTestCase {

    public void testCallbackWithNullListenerThrowsException() {
        try {
            final ListenerCallback callback = new ListenerCallback(null);
            callback.run(new MockAccountManagerFuture());
            fail();
        } catch (final NullPointerException e) {
            assertNotNull(e);
        }
    }

    public void testAuthorizationFailsWhenExceptionIsThrown() {
        final AssertionLatch latch = new AssertionLatch(1);
        final ListenerCallback callback = new ListenerCallback(new Authorization.Listener() {
            @Override
            public void onAuthorizationFailure(final Error error) {
                latch.countDown();
                assertEquals("error", error.getMessage());
            }

            @Override
            public void onAuthorizationComplete(final String token) {
                fail();
            }
        });
        callback.run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                throw new RuntimeException("error");
            }
        });
        latch.assertComplete();
    }

    public void testAuthorizationSucceedsWithTokenFromResultBundle() {
        final AssertionLatch latch = new AssertionLatch(1);
        final ListenerCallback callback = new ListenerCallback(new Authorization.Listener() {
            @Override
            public void onAuthorizationFailure(final Error error) {
                fail();
            }

            @Override
            public void onAuthorizationComplete(final String token) {
                latch.countDown();
                assertEquals("token", token);
            }
        });
        callback.run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                final Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_AUTHTOKEN, "token");
                return bundle;
            }
        });
        latch.assertComplete();
    }

}
