/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Activity;

import com.google.api.client.auth.oauth2.TokenResponse;

public class AuthorizationEngine extends AbstractAuthorizationClient {

    public AuthorizationEngine(ApiProvider apiProvider,
                               AuthorizationPreferencesProvider authorizationPreferencesProvider) {

        super(apiProvider, authorizationPreferencesProvider);
    }


    /**
     * Starts the authorization process.
     *  @param activity   an already-running activity to use as the base of the authorization process.  May not be null.
     *                   This activity *MUST* have an intent filter in the AndroidManifest.xml file that captures the
     *                   redirect URL sent by the server.  e.g.:
     *
     *                   <intent-filter>
     *                      <action android:name="android.intent.action.VIEW" />
     *                      <category android:name="android.intent.category.DEFAULT" />
     *                      <category android:name="android.intent.category.BROWSABLE" />
     *
     *                      <data android:scheme="[YOUR.REDIRECT_URL.SCHEME]" />
     *                      <data android:host="[YOUR.REDIRECT_URL.HOST]" />
     *                      <data android:pathPrefix="[YOUR.REDIRECT_URL.PATH]" />
     *                   </intent-filter>
     *
     */
    // TODO - describe thrown exceptions
    public void obtainAuthorization(Activity activity) throws AuthorizationException {
        if (activity == null) {
            throw new IllegalArgumentException("activity may not be null");
        }
        checkIfAuthorizationPreferencesAreSaved();
        startAuthorization(activity);
    }


    private void startAuthorization(Activity activity) throws AuthorizationException {
        // Launches external browser to do complete authentication
        final AuthorizedApiRequest request = apiProvider.getAuthorizedApiRequest(authorizationPreferencesProvider);
        request.obtainAuthorization(activity);
    }

    /**
     * Re-entry point to the authorization engine after the user authorizes the application and the
     * server sends back an authorization code.  Calling this method will make the call to the identity
     * server to receive the access token (which is required before calling any protected APIs).
     * This method will fail if it has been called before obtainAuthorization.
     *
     * This method assumes that it is called on the main thread.
     *
     * @param activity          an already-running activity to use as the base of the authorization process.  This activity
     *                          *MUST* have an intent filter in the `AndroidManifest.xml` file that captures the redirect URL
     *                          sent by the server.  Note that the `AuthorizationEngine` will hold a reference to this activity
     *                          until the access token from the identity server has been received and one of the two callbacks
     *                          in the activity have been made.  May not be null.
     *
     * @param authorizationCode the authorization code received from the server.
     */
    // TODO - describe thrown exceptions
    public void authorizationCodeReceived(final BaseAuthorizationActivity activity, final String authorizationCode) throws AuthorizationException {

        Logger.fd("Received authorization code from identity server: '%s'.", authorizationCode);

        if (activity == null) {
            throw new IllegalArgumentException("activity may not be null");
        }

        checkIfAuthorizationPreferencesAreSaved();

        final AuthorizedApiRequest request = apiProvider.getAuthorizedApiRequest(authorizationPreferencesProvider);

        // If no authorization was returned then clear any saved credentials and return an error
        if (authorizationCode == null || authorizationCode.isEmpty()) {

            request.clearSavedCredentialAsynchronously(new AuthorizedApiRequest.ClearSavedCredentialListener() {

                @Override
                public void onSavedCredentialCleared() {
                    activity.notifyAuthorizationFailed("no authorization code was returned.");
                }
            });

        } else {

            // TODO - ensure that an authorization flow is already active
            request.getAccessToken(authorizationCode, new AuthorizedApiRequest.AuthorizationListener() {

                @Override
                public void onSuccess(TokenResponse tokenResponse) {
                    activity.notifyAuthorizationComplete();
                }

                @Override
                public void onAuthorizationDenied() {
                    request.clearSavedCredentialSynchronously();
                    activity.notifyAuthorizationDenied();
                }

                @Override
                public void onFailure(String reason) {
                    activity.notifyAuthorizationFailed(reason);
                }
            });
        }
    }

    // TODO - add Javadocs
    public void clearAuthorization() throws AuthorizationException {
        checkIfAuthorizationPreferencesAreSaved();
        final AuthorizedApiRequest request = apiProvider.getAuthorizedApiRequest(authorizationPreferencesProvider);
        request.clearSavedCredentialAsynchronously(null);
    }

}
