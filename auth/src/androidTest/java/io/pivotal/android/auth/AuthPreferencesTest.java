/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.UUID;

public class AuthPreferencesTest extends AndroidTestCase {

    private static final String VALUE = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testGetInvokesSharedPreferences() {
        final Context context = Mockito.mock(Context.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);

        Mockito.when(context.getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE)).thenReturn(preferences);
        Mockito.when(preferences.getString(AuthPreferences.Keys.LAST_USED_ACCOUNT, "")).thenReturn(VALUE);

        assertEquals(VALUE, AuthPreferences.getAccountName(context));

        Mockito.verify(context).getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE);
        Mockito.verify(preferences).getString(AuthPreferences.Keys.LAST_USED_ACCOUNT, "");
    }

    public void testGetReturnsEmptyStringWithNullContext() {
        assertEquals("", AuthPreferences.getAccountName(null));
    }

    public void testSetInvokesSharedPreferences() {
        final Context context = Mockito.mock(Context.class);
        final SharedPreferences preferences = Mockito.mock(SharedPreferences.class);
        final SharedPreferences.Editor editor = Mockito.mock(SharedPreferences.Editor.class);

        Mockito.when(context.getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE)).thenReturn(preferences);
        Mockito.when(preferences.edit()).thenReturn(editor);
        Mockito.when(editor.putString(AuthPreferences.Keys.LAST_USED_ACCOUNT, VALUE)).thenReturn(editor);
        Mockito.when(editor.commit()).thenReturn(true);

        AuthPreferences.setAccountName(context, VALUE);

        Mockito.verify(context).getSharedPreferences(AuthPreferences.AUTH, Context.MODE_PRIVATE);
        Mockito.verify(preferences).edit();
        Mockito.verify(editor).putString(AuthPreferences.Keys.LAST_USED_ACCOUNT, VALUE);
        Mockito.verify(editor).apply();
    }

    public void testSetDoesNothingWithNullContext() {
        AuthPreferences.setAccountName(null, null);
    }
}
