/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;
import android.test.AndroidTestCase;

import io.pivotal.android.auth.Authorization.Listener;

public class AuthorizationTest extends AndroidTestCase {

    public void testGetAuthTokenInvokesProvider() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public void getAuthToken(final Activity activity, final Listener listener) {
                latch.countDown();
            }
        });

        Authorization.getAuthToken(null, (Listener) null);
        latch.assertComplete();
    }

    public void testGetAuthTokenByNameInvokesProviderAndReturnsToken() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAuthTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                return "token";
            }
        });

        assertEquals("token", Authorization.getAuthToken(null, (String) null));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAuthTokenByNameInvokesProviderAndReturnsNull() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAuthTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                return null;
            }
        });

        assertNull(Authorization.getAuthToken(null, (String) null));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAuthTokenByNameInvokesProviderThrowsAndReturnsNull() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAuthTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                throw new RuntimeException();
            }
        });

        assertNull(Authorization.getAuthToken(null, (String) null));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAuthTokenOrThrowInvokesProviderAndThrows() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAuthTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                throw new RuntimeException();
            }
        });

        try {
            Authorization.getAuthTokenOrThrow(null, null);
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testInvalidateAuthTokenInvokesProvider() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public void invalidateAuthToken(final String accessToken) {
                latch.countDown();
            }
        });

        Authorization.invalidateAuthToken(null, null);
        latch.assertComplete();
    }

    public void testAddAccountThrowsWithNullAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Authorization.addAccount(null, null, new Token("", ""));
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }
    }

    public void testAddAccountThrowsWithEmptyAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Authorization.addAccount(null, "", new Token("", ""));
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }
    }

    public void testAddAccountInvokesProvider() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {

            @Override
            public void addAccount(final Account account, final String refreshToken) {
                latch1.countDown();
            }

            @Override
            public void setAuthToken(final Account account, final String accessToken) {
                latch2.countDown();
            }
        });

        Authorization.addAccount(null, "account", new Token("access", "refresh"));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAccountsInvokesProvider() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch.countDown();
                return new Account[0];
            }
        });

        Authorization.getAccounts(null);

        latch.assertComplete();
    }

    public void testGetAccountWithNullNameReturnsNull() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch.countDown();
                return new Account[0];
            }
        });

        assertNull(Authorization.getAccount(null, null));

        latch.assertComplete();
    }

    public void testGetAccountWithEmptyNameReturnsNull() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch.countDown();
                return new Account[0];
            }
        });

        assertNull(Authorization.getAccount(null, ""));

        latch.assertComplete();
    }

    public void testGetAccountWithNonMatchingNameReturnsNull() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch.countDown();

                final Account[] accounts = new Account[2];
                accounts[0] = new Account("account0", "type");
                accounts[1] = new Account("account1", "type");
                return accounts;
            }
        });

        assertNull(Authorization.getAccount(null, "account"));

        latch.assertComplete();
    }

    public void testGetAccountWithMatchingNameReturnsAccount() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch.countDown();

                final Account[] accounts = new Account[2];
                accounts[0] = new Account("account0", "type");
                accounts[1] = new Account("account1", "type");
                return accounts;
            }
        });

        assertEquals("account1", Authorization.getAccount(null, "account1").name);

        latch.assertComplete();
    }

    public void testRemoveAccountThrowsWithNullAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Authorization.removeAccount(null, null);
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }
    }

    public void testRemoveAccountThrowsWithEmptyAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Authorization.removeAccount(null, "");
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }
    }

    public void testRemoveAccountInvokesProvider() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public void removeAccount(final Account account) {
                latch.countDown();
            }
        });

        Authorization.removeAccount(null, "account");

        latch.assertComplete();
    }

    public void testRemoveAllAccountsInvokesProvider() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(2);
        TokenProviderFactory.init(new MockTokenProvider() {

            @Override
            public Account[] getAccounts() {
                latch1.countDown();

                final Account[] accounts = new Account[2];
                accounts[0] = new Account("account0", "type");
                accounts[1] = new Account("account1", "type");
                return accounts;
            }

            @Override
            public void removeAccount(final Account account) {
                latch2.countDown();
            }
        });

        Authorization.removeAllAccounts(null);

        latch1.assertComplete();
        latch2.assertComplete();
    }
}
