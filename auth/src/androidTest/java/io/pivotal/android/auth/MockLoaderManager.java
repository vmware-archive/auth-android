/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;

import java.io.FileDescriptor;
import java.io.PrintWriter;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MockLoaderManager extends LoaderManager {

    @Override
    public <D> Loader<D> initLoader(int i, Bundle bundle, LoaderCallbacks<D> dLoaderCallbacks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> dLoaderCallbacks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroyLoader(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <D> Loader<D> getLoader(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dump(String s, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strings) {
        throw new UnsupportedOperationException();
    }
}
