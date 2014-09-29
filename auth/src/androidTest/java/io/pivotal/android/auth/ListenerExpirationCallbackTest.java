/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Base64;

public class ListenerExpirationCallbackTest extends AndroidTestCase {

    public void testCallbackWithNullListenerThrowsException() {
        try {
            new ListenerExpirationCallback(null, null).run(new MockAccountManagerFuture());
            fail();
        } catch (final NullPointerException e) {
            assertNotNull(e);
        }
    }

    public void testAuthorizationFailsWhenExceptionIsThrown() {
        final AssertionLatch latch = new AssertionLatch(1);
        final ListenerExpirationCallback callback = new ListenerExpirationCallback(null, new Authorization.Listener() {
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

    public void testAuthorizationWithExpiredTokenInvalidatesTokenAndRequestsANewOne() {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new ExpiredTokenProvider() {
            @Override
            public void invalidateAuthToken(final String accessToken) {
                latch1.countDown();
            }

            @Override
            public void getAuthToken(final Account account, final Authorization.Listener listener) {
                latch2.countDown();
            }
        });
        new ListenerExpirationCallback(null, null).run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                return Bundle.EMPTY;
            }
        });

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testAuthorizationWithValidTokenSucceeds() {
        final AssertionLatch latch = new AssertionLatch(1);

        TokenProviderFactory.init(new ValidTokenProvider());

        final ListenerExpirationCallback callback = new ListenerExpirationCallback(null, new Authorization.Listener() {
            @Override
            public void onAuthorizationFailure(final Error error) {
                fail();
            }

            @Override
            public void onAuthorizationComplete(final String token) {
                latch.countDown();
            }
        });
        callback.run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                return Bundle.EMPTY;
            }
        });

        latch.assertComplete();
    }

    private static class ExpiredTokenProvider extends MockTokenProvider {

        @Override
        public Account[] getAccounts() {
            return new Account[0];
        }

        @Override
        public String getAuthToken(final Account account) {
            return "expired";
        }

        @Override
        public String getRefreshToken(final Account account) {
            return "refresh";
        }
    }

    private static class ValidTokenProvider extends MockTokenProvider {

        @Override
        public Account[] getAccounts() {
            return new Account[0];
        }

        @Override
        public String getAuthToken(final Account account) {
            final long timeInFuture = System.currentTimeMillis() + 60 * 1000;
            final String expirationComponent = "{ \"exp\": \"" + timeInFuture + "\" }";
            return "." + Base64.encodeToString(expirationComponent.getBytes(), Base64.DEFAULT);
        }

        @Override
        public String getRefreshToken(final Account account) {
            return "refresh";
        }
    }
}
