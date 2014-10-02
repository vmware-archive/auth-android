package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;

import java.lang.reflect.Field;

public class LoginActivityTest extends ActivityUnitTestCase<LoginActivityTest.TestLoginActivity> {


    public LoginActivityTest() {
        super(TestLoginActivity.class);
    }

    public void testAuthorizationCompleteAddsAccountAndSetsAuthToken() {
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

        final TestLoginActivity activity = startActivity(new Intent(), null, null);
        final Token token = new Token("access_token", "refresh_token");
        activity.onAuthorizationComplete(token);

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testAuthorizationCompleteBundle() {
        TokenProviderFactory.init(new MockTokenProvider() {
            @Override
            public void addAccount(final Account account, final String refreshToken) {}

            @Override
            public void setAuthToken(final Account account, final String accessToken) {}
        });

        final TestLoginActivity activity = startActivity(new Intent(), null, null);
        final Token token = new Token("access_token", "refresh_token");
        activity.onAuthorizationComplete(token);

        final Bundle result = activity.getResult();
        assertEquals(Pivotal.getAccountType(), result.getString(AccountManager.KEY_ACCOUNT_TYPE));
        assertEquals(token.getAccessToken(), result.getString(AccountManager.KEY_AUTHTOKEN));
        assertEquals(activity.getUserName(), result.getString(AccountManager.KEY_ACCOUNT_NAME));
    }

    public static class TestLoginActivity extends LoginActivity {

        @Override
        protected String getUserName() {
            return "test";
        }


        public Bundle getResult() {
            try {
                final Field f = getField("mResultData");
                final Intent result = (Intent) f.get(this);
                return result.getExtras();
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        private Field getField(final String name) throws NoSuchFieldException {
            final Field field = Activity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
    }
}