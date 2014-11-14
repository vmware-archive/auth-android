/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import org.mockito.Mockito;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginPasswordActivityTest extends ActivityUnitTestCase<LoginPasswordActivity> {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();

    public LoginPasswordActivityTest() {
        super(LoginPasswordActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Context context = getInstrumentation().getContext();
        System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath());
    }

    public void testOnCreateInvokesSetContentView() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.doNothing().when(activity).setContentView(R.layout.activity_login_password);

        activity.onCreate(null);

        Mockito.verify(activity).setContentView(R.layout.activity_login_password);
    }

    public void testGetUserNameReturnsValueFromEditText() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));
        final EditText editText = Mockito.mock(EditText.class);

        Mockito.doReturn(editText).when(activity).findViewById(R.id.login_user_name);
        Mockito.when(editText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(USERNAME));

        assertEquals(USERNAME, activity.getUserName());

        Mockito.verify(activity).findViewById(R.id.login_user_name);
        Mockito.verify(editText).getText();
    }

    public void testGetPasswordReturnsValueFromEditText() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));
        final EditText editText = Mockito.mock(EditText.class);

        Mockito.doReturn(editText).when(activity).findViewById(R.id.login_password);
        Mockito.when(editText.getText()).thenReturn(Editable.Factory.getInstance().newEditable(PASSWORD));

        assertEquals(PASSWORD, activity.getPassword());

        Mockito.verify(activity).findViewById(R.id.login_password);
        Mockito.verify(editText).getText();
    }

    public void testOnLoginClickedWithInValidCredentials() {
        final LoaderManager manager = Mockito.mock(LoaderManager.class);
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.doReturn(USERNAME).when(activity).getUserName();
        Mockito.doReturn(PASSWORD).when(activity).getPassword();
        Mockito.doReturn(false).when(activity).onValidateCredentials(USERNAME, PASSWORD);
        Mockito.doReturn(manager).when(activity).getLoaderManager();

        activity.onLoginClicked(null);

        Mockito.verify(activity).getUserName();
        Mockito.verify(activity).getPassword();
        Mockito.verify(activity).onValidateCredentials(USERNAME, PASSWORD);
        Mockito.verify(activity, Mockito.never()).getLoaderManager();
    }

    public void testOnLoginClickedWithValidCredentials() {
        final LoaderManager manager = Mockito.mock(LoaderManager.class);
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.doReturn(USERNAME).when(activity).getUserName();
        Mockito.doReturn(PASSWORD).when(activity).getPassword();
        Mockito.doReturn(true).when(activity).onValidateCredentials(USERNAME, PASSWORD);
        Mockito.doReturn(manager).when(activity).getLoaderManager();
        Mockito.when(manager.restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.any(PasswordTokenLoaderCallbacks.class))).thenReturn(null);
        Mockito.doNothing().when(activity).onStartLoading();

        activity.onLoginClicked(null);

        Mockito.verify(activity).getUserName();
        Mockito.verify(activity).getPassword();
        Mockito.verify(activity).onValidateCredentials(USERNAME, PASSWORD);
        Mockito.verify(activity).getLoaderManager();
        Mockito.verify(manager).restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.any(PasswordTokenLoaderCallbacks.class));
        Mockito.verify(activity).onStartLoading();
    }

    public void testOnValidateCredentialsSuccess() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        assertTrue(activity.onValidateCredentials(USERNAME, PASSWORD));
    }

    public void testOnValidateCredentialsWithEmptyValues() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        assertFalse(activity.onValidateCredentials("", ""));
    }

    public void testOnValidateCredentialsWithNullValues() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        assertFalse(activity.onValidateCredentials(null, null));
    }

    public void testOnStartLoadingChangesTextAndDisablesButton() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));
        final Button button = Mockito.mock(Button.class);

        Mockito.doReturn(button).when(activity).findViewById(R.id.login_submit);
        Mockito.doNothing().when(button).setEnabled(false);

        activity.onStartLoading();

        Mockito.verify(activity).findViewById(R.id.login_submit);
        Mockito.verify(button).setEnabled(false);
    }

    public void testOnStartLoadingChangesWhenButtonNotInLayout() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.doReturn(null).when(activity).findViewById(R.id.login_submit);

        activity.onStartLoading();

        Mockito.verify(activity).findViewById(R.id.login_submit);
    }

    public void testOnAuthorizationFailedChangesTextAndEnablesButton() {
        final LoginPasswordActivity activity = Mockito.spy(startActivity(new Intent(), null, null));
        final Button button = Mockito.mock(Button.class);

        Mockito.doReturn(button).when(activity).findViewById(R.id.login_submit);
        Mockito.doNothing().when(button).setEnabled(true);

        activity.onAuthorizationFailed(null);

        Mockito.verify(activity).findViewById(R.id.login_submit);
        Mockito.verify(button).setEnabled(true);
    }
}