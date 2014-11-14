/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;

import org.mockito.Mockito;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginAuthCodeActivityTest extends ActivityUnitTestCase<LoginAuthCodeActivity> {

    private static final String AUTH_CODE = UUID.randomUUID().toString();

    public LoginAuthCodeActivityTest() {
        super(LoginAuthCodeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Context context = getInstrumentation().getContext();
        System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath());
    }

    public void testOnStartInvokesOnHandleRedirect() {
        final Intent intent = new Intent();
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(intent, null, null));

        Mockito.doReturn(true).when(activity).intentHasCallbackUrl(intent);
        Mockito.doNothing().when(activity).onHandleRedirect(intent);

        activity.onStart();

        Mockito.verify(activity).intentHasCallbackUrl(intent);
        Mockito.verify(activity).onHandleRedirect(intent);
    }

    public void testOnStartInvokesAuthorize() {
        final Intent intent = new Intent();
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(intent, null, null));

        Mockito.doReturn(false).when(activity).intentHasCallbackUrl(intent);
        Mockito.doNothing().when(activity).authorize();

        activity.onStart();

        Mockito.verify(activity).intentHasCallbackUrl(intent);
        Mockito.verify(activity).authorize();
    }

    public void testOnCreateInvokesSetContentView() {
        final AuthProvider provider = Mockito.mock(AuthProvider.class);
        final AuthorizationCodeRequestUrl requestUrl = Mockito.mock(AuthorizationCodeRequestUrl.class);

        Mockito.when(provider.newAuthorizationCodeUrl()).thenReturn(requestUrl);

        AuthProviderFactory.init(provider);

        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.doNothing().when(activity).setContentView(R.layout.activity_login_auth_code);

        activity.onCreate(null);

        Mockito.verify(activity).setContentView(R.layout.activity_login_auth_code);
    }

    public void testGetUserNameDefaultValue() {
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        assertEquals("Account", activity.getUserName());
    }

    public void testIntentHasCallbackUrlReturnsTrue() {
        final Uri uri = Uri.parse(Pivotal.getRedirectUrl());
        final Intent intent = Mockito.mock(Intent.class);
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.when(intent.hasCategory(Intent.CATEGORY_BROWSABLE)).thenReturn(true);
        Mockito.when(intent.getData()).thenReturn(uri);

        assertTrue(activity.intentHasCallbackUrl(intent));

        Mockito.verify(intent).hasCategory(Intent.CATEGORY_BROWSABLE);
        Mockito.verify(intent, Mockito.times(2)).getData();
    }

    public void testIntentHasCallbackUrlWithoutCategoryBrowsable() {
        final Intent intent = Mockito.mock(Intent.class);
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.when(intent.hasCategory(Intent.CATEGORY_BROWSABLE)).thenReturn(false);

        assertFalse(activity.intentHasCallbackUrl(intent));

        Mockito.verify(intent).hasCategory(Intent.CATEGORY_BROWSABLE);
    }

    public void testIntentHasCallbackUrlWithoutData() {
        final Intent intent = Mockito.mock(Intent.class);
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.when(intent.hasCategory(Intent.CATEGORY_BROWSABLE)).thenReturn(true);

        assertFalse(activity.intentHasCallbackUrl(intent));

        Mockito.verify(intent).hasCategory(Intent.CATEGORY_BROWSABLE);
        Mockito.verify(intent).getData();
    }

    public void testIntentHasCallbackUrlWithoutRedirectUrl() {
        final Uri uri = Mockito.mock(Uri.class);
        final Intent intent = Mockito.mock(Intent.class);
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.when(intent.hasCategory(Intent.CATEGORY_BROWSABLE)).thenReturn(true);
        Mockito.when(intent.getData()).thenReturn(uri);

        assertFalse(activity.intentHasCallbackUrl(intent));

        Mockito.verify(intent).hasCategory(Intent.CATEGORY_BROWSABLE);
        Mockito.verify(intent, Mockito.times(2)).getData();
    }

    public void testIntentHasCallbackUrlWithoutIntent() {
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        assertFalse(activity.intentHasCallbackUrl(null));
    }

    public void testOnHandleRedirectInvokesRestartLoader() {
        final Uri uri = Mockito.mock(Uri.class);
        final Intent intent = Mockito.mock(Intent.class);
        final LoaderManager manager = Mockito.mock(LoaderManager.class);
        final LoginAuthCodeActivity activity = Mockito.spy(startActivity(new Intent(), null, null));

        Mockito.when(intent.getData()).thenReturn(uri);
        Mockito.when(uri.getQueryParameter("code")).thenReturn(AUTH_CODE);
        Mockito.doReturn(manager).when(activity).getLoaderManager();
        Mockito.when(manager.restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.any(AuthCodeTokenLoaderCallbacks.class))).thenReturn(null);

        activity.onHandleRedirect(intent);

        Mockito.verify(intent).getData();
        Mockito.verify(uri).getQueryParameter("code");
        Mockito.verify(activity).getLoaderManager();
        Mockito.verify(manager).restartLoader(Mockito.anyInt(), Mockito.any(Bundle.class), Mockito.any(AuthCodeTokenLoaderCallbacks.class));
    }
}