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
import com.google.api.client.auth.oauth2.TokenResponse;

import java.io.IOException;

public class Authenticator extends AbstractAccountAuthenticator {

	private final Context mContext;

	public Authenticator(final Context context) {
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


    /* package */ Token getExistingToken(final Account account) {
        final TokenProvider provider = TokenProviderFactory.get(mContext);
        return new Token(provider, account);
    }

    /* package */ Token getNewToken(final String refreshToken) throws IOException {
        final AuthorizationProvider provider = new AuthorizationProvider();
        final RefreshTokenRequest request = provider.newRefreshTokenRequest(refreshToken);
        final TokenResponse resp = request.execute();
        return new Token(resp);
    }

    private Bundle newAccountBundle(final AccountAuthenticatorResponse response) {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, newLoginIntent(response));
		return bundle;
	}

    private Intent newLoginIntent(final AccountAuthenticatorResponse response) {
        final Intent intent = new Intent(mContext, PackageHelper.getLoginActivityClass(mContext));
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    private Bundle newAuthTokenBundle(final Account account, final String authToken) {
		final Bundle result = new Bundle();
		result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
		result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
		result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
		return result;
	}

    private Bundle newResultBundle(final boolean result) {
        final Bundle bundle = new Bundle();
        bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, result);
        return bundle;
    }

    private Bundle newAuthTokenBundle(final AccountAuthenticatorResponse response, final Account account) {
        final Token existingToken = getExistingToken(account);
        final String accessToken = existingToken.getAccessToken();

        Logger.v("newAuthTokenBundle accessToken: " + accessToken);

        if (!TextUtils.isEmpty(accessToken) && !existingToken.isExpired()) {
            return newAuthTokenBundle(account, accessToken);
        }

        Logger.v("newAuthTokenBundle auth token " + (TextUtils.isEmpty(accessToken) ? "empty." : "expired."));

        final String refreshToken = existingToken.getRefreshToken();

        Logger.v("newAuthTokenBundle refreshToken: " + refreshToken);

        if (!TextUtils.isEmpty(refreshToken)) {
            try {
                final Token newToken = getNewToken(refreshToken);
                final String newAccessToken = newToken.getAccessToken();
                Logger.v("newAuthTokenBundle new accessToken: " + newAccessToken);
                return newAuthTokenBundle(account, newAccessToken);
            } catch (final Exception e) {
                Logger.ex(e);
            }
        }

        return newAccountBundle(response);
    }

}