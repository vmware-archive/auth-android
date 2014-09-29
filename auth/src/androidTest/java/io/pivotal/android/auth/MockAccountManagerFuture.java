package io.pivotal.android.auth;

import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MockAccountManagerFuture implements AccountManagerFuture<Bundle> {

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getResult() throws OperationCanceledException, IOException, AuthenticatorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getResult(final long timeout, final TimeUnit unit) throws OperationCanceledException, IOException, AuthenticatorException {
        throw new UnsupportedOperationException();
    }
}
