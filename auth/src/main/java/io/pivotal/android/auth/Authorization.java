/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Activity;
import android.content.Context;

public class Authorization {

    private static Authorization instance;

    public static Authorization getInstance(final Context context) {
        if (instance == null) {
            instance = new Authorization(context);
        }
        return instance;
    }

    private final AuthorizationEngine engine;
    private final AuthorizedResourceClient client;

    private Authorization(final Context context) {
        final ApiProvider apiProvider = new ApiProviderImpl(context);
        final AuthorizationPreferencesProvider preferences = new AuthorizationPreferencesProviderImpl(context);
        this.engine = new AuthorizationEngine(apiProvider, preferences);
        this.client = new AuthorizedResourceClientImpl(apiProvider, preferences);
    }

    public void init(AuthorizationParams parameters) {
        engine.setParameters(parameters);
    }

    public void obtain(Activity activity) {
        assertCalledOnUIThread();
        engine.obtainAuthorization(activity);
    }

    public void clear() {
        assertCalledOnUIThread();
        engine.clearAuthorization();
    }

    public AuthorizedResourceClient getClient() {
        return client;
    }

    private void assertCalledOnUIThread() throws AuthorizationException {
        if (!ThreadUtil.isUIThread()) {
            throw new AuthorizationException("Must be called on the main thread");
        }
    }
}
