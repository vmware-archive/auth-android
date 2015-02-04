/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;

import org.mockito.Mockito;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PasswordTokenLoaderTest extends AndroidTestCase {

    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String PASSWORD = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testLoadInBackgroundSucceedsWithTokenResponse() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final TokenResponse response = Mockito.mock(TokenResponse.class);
        final RemoteAuthenticator authenticator = Mockito.mock(RemoteAuthenticator.class);
        final PasswordTokenRequest request = Mockito.mock(PasswordTokenRequest.class);
        final PasswordTokenLoader loader = new PasswordTokenLoader(context, USERNAME, PASSWORD);

        RemoteAuthenticatorFactory.init(authenticator);

        Mockito.when(authenticator.newPasswordTokenRequest(USERNAME, PASSWORD)).thenReturn(request);
        Mockito.when(request.execute()).thenReturn(response);

        assertEquals(response, loader.loadInBackground());

        Mockito.verify(authenticator).newPasswordTokenRequest(USERNAME, PASSWORD);
        Mockito.verify(request).execute();
    }

    public void testLoadInBackgroundFailsWithErrorResponse() throws Exception {
        final Context context = Mockito.mock(Context.class);
        final RemoteAuthenticator authenticator = Mockito.mock(RemoteAuthenticator.class);
        final PasswordTokenRequest request = Mockito.mock(PasswordTokenRequest.class);
        final PasswordTokenLoader loader = new PasswordTokenLoader(context, USERNAME, PASSWORD);

        RemoteAuthenticatorFactory.init(authenticator);

        Mockito.when(authenticator.newPasswordTokenRequest(USERNAME, PASSWORD)).thenReturn(request);
        Mockito.doThrow(new RuntimeException()).when(request).execute();

        final TokenResponse response = loader.loadInBackground();

        assertTrue(response instanceof TokenLoader.ErrorResponse);

        Mockito.verify(authenticator).newPasswordTokenRequest(USERNAME, PASSWORD);
        Mockito.verify(request).execute();
    }
}
