/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.http.HttpResponseException;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

	private final Context mContext;

	public AccountAuthenticator(final Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType, final String authTokenType, final String[] requiredFeatures, final Bundle options) throws NetworkErrorException {
		return newAccountBundle(response);
	}

	@Override
	public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account, final Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
	}

	@Override
	public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle options) throws NetworkErrorException {
		return newAuthTokenBundle(response, account);
	}

	@Override
	public String getAuthTokenLabel(final String authTokenType) {
        return authTokenType;
	}

	@Override
	public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account, final String[] features) throws NetworkErrorException {
		return newResultBundle(false);
	}

	@Override
	public Bundle updateCredentials(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
	}
	
	
	// =============================================


    protected Class<?> getLoginActivityClass() {
        return PackageUtils.getLoginActivityClass(mContext);
    }

    protected Bundle newAccountBundle(final AccountAuthenticatorResponse response) {
        Logger.v("newAccountBundle");
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, newLoginIntent(response));
		return bundle;
	}

    protected Intent newLoginIntent(final AccountAuthenticatorResponse response) {
        Logger.v("newLoginIntent");
        final Intent intent = new Intent(mContext, getLoginActivityClass());
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    protected Bundle newAuthTokenBundle(final Account account, final String authToken) {
        Logger.v("newAuthTokenBundle: " + authToken);
		final Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
		return bundle;
	}

    protected Bundle newResultBundle(final boolean result) {
        Logger.v("newResultBundle: " + result);
        final Bundle bundle = new Bundle();
        bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, result);
        return bundle;
    }

    protected Bundle newErrorBundle(final Account account, final Throwable throwable) {
        final String message = throwable != null ? throwable.toString() : "Unknown";
        Logger.v("newErrorBundle: " + message);
        final Bundle bundle = new Bundle();
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        bundle.putString(AccountManager.KEY_ERROR_MESSAGE, message);
        bundle.putInt(AccountManager.KEY_ERROR_CODE, 1);
        return bundle;
    }

    protected Bundle newAuthTokenBundle(final AccountAuthenticatorResponse response, final Account account) {

        final AccountsProxy proxy = AccountsProxyHolder.get(mContext);
        final String accessToken = proxy.getAccessToken(account);

        Logger.v("newAuthTokenBundle accessToken: " + accessToken);

        if (!TextUtils.isEmpty(accessToken) && !TokenUtil.isExpired(accessToken)) {
            return newAuthTokenBundle(account, accessToken);

        } else {
            final String refreshToken = proxy.getRefreshToken(account);

            Logger.v("newAuthTokenBundle accessToken " + (TextUtils.isEmpty(accessToken) ? "empty." : "expired."));
            Logger.v("newAuthTokenBundle refreshToken: " + refreshToken);

            if (!TextUtils.isEmpty(refreshToken)) {
                return newAuthTokenBundle(response, account, refreshToken);
            } else {
                return newAccountBundle(response);
            }
        }
    }

    protected Bundle newAuthTokenBundle(final AccountAuthenticatorResponse response, final Account account, final String refreshToken) {
        try {
            final RemoteAuthenticator authenticator = RemoteAuthenticatorHolder.get();
            final RefreshTokenRequest request = authenticator.newRefreshTokenRequest(refreshToken);
            final String accessToken = request.execute().getAccessToken();

            Logger.v("newAuthTokenBundle new accessToken: " + accessToken);

            return newAuthTokenBundle(account, accessToken);

        } catch (final HttpResponseException e) {
            Logger.ex(e);

            if (e.getStatusCode() == 401) {
                return newAccountBundle(response);
            } else {
                return newErrorBundle(account, e.getCause());
            }

        } catch (final Exception e) {
            Logger.ex(e);

            return newErrorBundle(account, e.getCause());
        }
    }
}