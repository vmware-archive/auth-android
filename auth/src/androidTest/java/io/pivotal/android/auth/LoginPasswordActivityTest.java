/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.EditText;

public class LoginPasswordActivityTest extends ActivityUnitTestCase<LoginPasswordActivityTest.TestLoginPasswordActivity> {

    private TestLoginPasswordActivity mActivity;

    public LoginPasswordActivityTest() {
        super(TestLoginPasswordActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = startActivity(new Intent(), null, null);
    }

    public void testHasUserNameEntryField() {
        assertNotNull(getUserNameEditText());
    }

    public void testHasPasswordEntryField() {
        assertNotNull(getPasswordEditText());
    }

    public void testHasSubmitButton() {
        assertNotNull(getSubmitButton());
    }

    public void testSubmitButtonStartsEnabled() {
        assertTrue(getSubmitButton().isEnabled());
    }

    public void testSubmitButtonIsDisabledDuringAuthorization() {
        mActivity.onStartLoading();
        assertFalse(getSubmitButton().isEnabled());
    }

    public void testSubmitButtonIsEnabledAfterAuthorizationFails() {
        mActivity.onStartLoading();
        mActivity.onAuthorizationFailed(new Error("Some Error"));
        assertTrue(getSubmitButton().isEnabled());
    }

    public void testClickedLoginWithValidCredentials() {
        TestLoginPasswordActivity.sWasRestartLoaderCalled = false;
        getUserNameEditText().setText("username");
        getPasswordEditText().setText("password");
        mActivity.onLoginClicked(getSubmitButton());
        assertTrue(TestLoginPasswordActivity.sWasRestartLoaderCalled);
        assertFalse(getSubmitButton().isEnabled());
    }

    public void testClickedLoginWithInvalidCredentials() {
        TestLoginPasswordActivity.sWasRestartLoaderCalled = false;
        getUserNameEditText().setText("");
        getPasswordEditText().setText("");
        mActivity.onLoginClicked(getSubmitButton());
        assertFalse(TestLoginPasswordActivity.sWasRestartLoaderCalled);
        assertTrue(getSubmitButton().isEnabled());
    }

    public void testValidateCredentials() {
        assertAreCredentialsValid(false, null, null);
        assertAreCredentialsValid(false, null, "");
        assertAreCredentialsValid(false, "", null);
        assertAreCredentialsValid(false, "", "");
        assertAreCredentialsValid(false, "username", "");
        assertAreCredentialsValid(false, "", "password");
        assertAreCredentialsValid(true, "username", "password");
    }

    private void assertAreCredentialsValid(final boolean areValid, final String userName, final String password) {
        assertEquals(areValid, mActivity.onValidateCredentials(userName, password));
    }

    private EditText getUserNameEditText() {
        return (EditText) mActivity.findViewById(R.id.login_user_name);
    }

    private EditText getPasswordEditText() {
        return (EditText) mActivity.findViewById(R.id.login_password);
    }

    private Button getSubmitButton() {
        return (Button) mActivity.findViewById(R.id.login_submit);
    }

    public static class TestLoginPasswordActivity extends LoginPasswordActivity {

        private static boolean sWasRestartLoaderCalled;

        @Override
        public LoaderManager getLoaderManager() {
            return new MockLoaderManager() {

                @Override
                public <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> dLoaderCallbacks) {
                    sWasRestartLoaderCalled = true;
                    return null;
                }
            };
        }
    }

}