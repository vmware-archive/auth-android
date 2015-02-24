/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.AndroidTestCase;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import org.mockito.Mockito;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginPasswordActivityTest extends AndroidTestCase {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testGetUserNameReturnsValueFromEditText() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());
        final EditText editText = Mockito.mock(EditText.class);

        Mockito.doReturn(editText).when(activity).findViewById(R.id.login_user_name);
        Mockito.when(editText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(USERNAME));

        assertEquals(USERNAME, activity.getUserName());

        Mockito.verify(activity).findViewById(R.id.login_user_name);
        Mockito.verify(editText).getText();
    }

    public void testGetPasswordReturnsValueFromEditText() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());
        final EditText editText = Mockito.mock(EditText.class);

        Mockito.doReturn(editText).when(activity).findViewById(R.id.login_password);
        Mockito.when(editText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(PASSWORD));

        assertEquals(PASSWORD, activity.getPassword());

        Mockito.verify(activity).findViewById(R.id.login_password);
        Mockito.verify(editText).getText();
    }

    public void testOnLoginClickedWithInValidCredentials() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());

        Mockito.doReturn(USERNAME).when(activity).getUserName();
        Mockito.doReturn(PASSWORD).when(activity).getPassword();
        Mockito.doReturn(false).when(activity).onValidateCredentials(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(activity).fetchTokenWithPasswordGrantType(Mockito.anyString(), Mockito.anyString());

        activity.onLoginClicked(null);

        Mockito.verify(activity).getUserName();
        Mockito.verify(activity).getPassword();
        Mockito.verify(activity).onValidateCredentials(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(activity, Mockito.never()).fetchTokenWithPasswordGrantType(Mockito.anyString(), Mockito.anyString());
    }

    public void testOnLoginClickedWithValidCredentials() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());

        Mockito.doReturn(USERNAME).when(activity).getUserName();
        Mockito.doReturn(PASSWORD).when(activity).getPassword();
        Mockito.doReturn(true).when(activity).onValidateCredentials(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(activity).fetchTokenWithPasswordGrantType(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(activity).onStartLoading();

        activity.onLoginClicked(null);

        Mockito.verify(activity).getUserName();
        Mockito.verify(activity).getPassword();
        Mockito.verify(activity).onValidateCredentials(USERNAME, PASSWORD);
        Mockito.verify(activity).onStartLoading();
        Mockito.verify(activity).fetchTokenWithPasswordGrantType(USERNAME, PASSWORD);
    }

    public void testOnValidateCredentialsSuccess() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());

        assertTrue(activity.onValidateCredentials(USERNAME, PASSWORD));
    }

    public void testOnStartLoadingChangesTextAndDisablesButton() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());
        final Button button = Mockito.mock(Button.class);

        Mockito.doReturn(button).when(activity).findViewById(R.id.login_submit);
        Mockito.doNothing().when(button).setEnabled(false);

        activity.onStartLoading();

        Mockito.verify(activity).findViewById(R.id.login_submit);
        Mockito.verify(button).setEnabled(false);
    }

    public void testOnStartLoadingChangesWhenButtonNotInLayout() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());

        Mockito.doReturn(null).when(activity).findViewById(R.id.login_submit);

        activity.onStartLoading();

        Mockito.verify(activity).findViewById(R.id.login_submit);
    }

    public void testOnAuthorizationFailedChangesTextAndEnablesButton() {
        final LoginPasswordActivity activity = Mockito.spy(new LoginPasswordActivity());
        final Button button = Mockito.mock(Button.class);

        Mockito.doReturn(button).when(activity).findViewById(R.id.login_submit);
        Mockito.doNothing().when(button).setEnabled(true);

        activity.onAuthorizationFailed(null);

        Mockito.verify(activity).findViewById(R.id.login_submit);
        Mockito.verify(button).setEnabled(true);
    }
}