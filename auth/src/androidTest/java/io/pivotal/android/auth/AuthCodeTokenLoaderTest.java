/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.AndroidTestCase;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

import io.pivotal.android.auth.TokenLoader.ErrorResponse;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AuthCodeTokenLoaderTest extends AndroidTestCase {

    public void testLoadInBackgroundSucceedsWithTokenResponse() {
        final AssertionLatch latch = new AssertionLatch(1);
        final TokenResponse response = new TokenResponse();
        final MockAuthorizationProvider provider = new MockAuthorizationProvider() {

            @Override
            public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(String authorizationCode) {
                latch.countDown();
                return new TestAuthorizationCodeTokenRequest(response);
            }
        };
        final AuthCodeTokenLoader loader = new AuthCodeTokenLoader(mContext, provider, null);
        assertEquals(response, loader.loadInBackground());
        latch.assertComplete();
    }

    public void testLoadInBackgroundFailsWithErrorResponse() {
        final AssertionLatch latch = new AssertionLatch(1);
        final MockAuthorizationProvider provider = new MockAuthorizationProvider() {

            @Override
            public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(String authorizationCode) {
                latch.countDown();
                throw new RuntimeException();
            }
        };
        final AuthCodeTokenLoader loader = new AuthCodeTokenLoader(mContext, provider, null);
        final ErrorResponse errorResponse = (ErrorResponse) loader.loadInBackground();
        assertNotNull(errorResponse);
        latch.assertComplete();
    }


    private static class TestAuthorizationCodeTokenRequest extends AuthorizationCodeTokenRequest {

        private static final HttpTransport TEST_TRANSPORT = new NetHttpTransport();
        private static final JsonFactory TEST_JSON_FACTORY = new JacksonFactory();
        private static final GenericUrl TEST_URL = new GenericUrl("http://example.com");

        private TokenResponse mTokenResponse;

        public TestAuthorizationCodeTokenRequest(final TokenResponse response) {
            super(TEST_TRANSPORT, TEST_JSON_FACTORY, TEST_URL, "");
            mTokenResponse = response;
        }

        @Override
        public TokenResponse execute() throws IOException {
            return mTokenResponse;
        }
    }
}
