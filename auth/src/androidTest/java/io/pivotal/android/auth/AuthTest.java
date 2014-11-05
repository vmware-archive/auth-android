/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.app.Activity;
import android.test.AndroidTestCase;

import io.pivotal.android.auth.Auth.Listener;

public class AuthTest extends AndroidTestCase {

    public void testGetAccessTokenInvokesProvider() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public void getAccessToken(final Activity activity, final Listener listener) {
                latch.countDown();
            }
        });

        Auth.getAccessToken(null, (Listener) null);
        latch.assertComplete();
    }

    public void testGetAccessTokenByNameInvokesProviderAndReturnsToken() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAccessTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                return "token";
            }
        });

        assertEquals("token", Auth.getAccessToken(null, (String) null));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAccessTokenByNameInvokesProviderAndReturnsNull() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAccessTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                return null;
            }
        });

        assertNull(Auth.getAccessToken(null, (String) null));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAccessTokenByNameInvokesProviderThrowsAndReturnsNull() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAccessTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                throw new RuntimeException();
            }
        });

        assertNull(Auth.getAccessToken(null, (String) null));

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testGetAccessTokenOrThrowInvokesProviderAndThrows() throws Exception {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public Account[] getAccounts() {
                latch1.countDown();
                return new Account[0];
            }

            @Override
            public String getAccessTokenOrThrow(final Account account) throws Exception {
                latch2.countDown();
                throw new RuntimeException();
            }
        });

        try {
            Auth.getAccessTokenOrThrow(null, null);
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testInvalidateAccessTokenInvokesProvider() throws Exception {
        final AssertionLatch latch = new AssertionLatch(1);
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public void invalidateAccessToken(final String accessToken) {
                latch.countDown();
            }
        });

        Auth.invalidateAccessToken(null, null);
        latch.assertComplete();
    }

    public void testAddAccountThrowsWithNullAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Auth.addAccount(null, null, new Token("", ""));
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }
    }

    public void testAddAccountThrowsWithEmptyAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Auth.addAccount(null, "", new Token("", ""));
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
            public void setAccessToken(final Account account, final String accessToken) {
                latch2.countDown();
            }
        });

        Auth.addAccount(null, "account", new Token("access", "refresh"));

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

        Auth.getAccounts(null);

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

        assertNull(Auth.getAccount(null, null));

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

        assertNull(Auth.getAccount(null, ""));

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

        assertNull(Auth.getAccount(null, "account"));

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

        assertEquals("account1", Auth.getAccount(null, "account1").name);

        latch.assertComplete();
    }

    public void testRemoveAccountThrowsWithNullAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Auth.removeAccount(null, null);
            fail();
        } catch (final Exception e) {
            assertNotNull(e);
        }
    }

    public void testRemoveAccountThrowsWithEmptyAccountName() throws Exception {
        try {
            TokenProviderFactory.init(new MockTokenProvider());
            Auth.removeAccount(null, "");
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

        Auth.removeAccount(null, "account");

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

        Auth.removeAllAccounts(null);

        latch1.assertComplete();
        latch2.assertComplete();
    }
}
