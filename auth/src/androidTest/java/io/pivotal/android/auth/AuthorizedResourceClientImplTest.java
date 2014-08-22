/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import junit.framework.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AuthorizedResourceClientImplTest extends AbstractAuthorizedClientTest<AuthorizedResourceClientImpl> {

    private static final String HTTP_GET = "GET";
    private static final String TEST_HTTP_GET_URL = "http://test.get.url";
    private static final String TEST_CONTENT_TYPE = "application/json";
    private static final String TEST_CONTENT_ENCODING = "utf-8";
    private static final String TEST_CONTENT_DATA = "TEST CONTENT DATA";
    private static final String TEST_HEADER_NAME = "Test Header Name";
    private static final String TEST_HEADER_VALUE = "Test Header Value";
    private static final int TEST_HTTP_STATUS_CODE = 200;

    private URL url;
    private Map<String, Object> headers;
    private AuthorizedResourceClientImpl.Listener listener;
    private boolean shouldSuccessListenerBeCalled;
    private boolean shouldRequestBeSuccessful;
    private boolean shouldUnauthorizedListenerBeCalled;
    private int expectedHttpStatusCode;
    private String expectedContentType;
    private String expectedContentData;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        url = new URL(TEST_HTTP_GET_URL);
        headers = new HashMap<String, Object>();
        headers.put(TEST_HEADER_NAME, TEST_HEADER_VALUE);
        listener = new AuthorizedResourceClientImpl.Listener() {

            @Override
            public void onSuccess(int httpStatusCode, String contentType, String contentEncoding, InputStream result) {
                assertTrue(shouldSuccessListenerBeCalled);
                assertEquals(expectedHttpStatusCode, httpStatusCode);
                assertEquals(expectedContentType, contentType);
                try {
                    Assert.assertEquals(expectedContentData, StreamUtil.readInput(result));
                } catch (IOException e) {
                    fail();
                }
                semaphore.release();
            }

            @Override
            public void onUnauthorized() {
                assertTrue(shouldUnauthorizedListenerBeCalled);
                assertFalse(shouldSuccessListenerBeCalled);
                semaphore.release();
            }

            @Override
            public void onFailure(String reason) {
                assertFalse(shouldSuccessListenerBeCalled);
                semaphore.release();
            }
        };
    }

    @Override
    protected AuthorizedResourceClientImpl construct(AuthorizationPreferencesProvider preferencesProvider,
                                                 ApiProvider apiProvider) {

        return new AuthorizedResourceClientImpl(apiProvider, preferencesProvider);
    }

    private AuthorizedResourceClientImpl getClient() {
        return new AuthorizedResourceClientImpl(apiProvider, preferences);
    }

    public void testRequiresMethod() throws Exception {
        baseTestRequires(null, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, parameters, listener);
    }

    public void testRequiresUrl() throws Exception {
        baseTestRequires(HTTP_GET, null, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, parameters, listener);
    }

    public void testRequiresContentType() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, null, TEST_CONTENT_ENCODING, parameters, listener);
    }

    public void testRequiresContentEncoding() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, null, parameters, listener);
    }

    public void testRequiresListener() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, parameters, null);
    }

    public void testRequiresParameters() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
    }

    public void testRequiresNotNullClientId() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams(null, TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL, TEST_REDIRECT_URL), listener);
    }

    public void testRequiresNotEmptyClientId() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams("", TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL, TEST_REDIRECT_URL), listener);
    }

    public void testRequiresNotNullClientSecret() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams(TEST_CLIENT_ID, null, TEST_AUTHORIZATION_URL, TEST_REDIRECT_URL), listener);
    }

    public void testRequiresNotEmptyClientSecret() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams(TEST_CLIENT_ID, "", TEST_AUTHORIZATION_URL, TEST_REDIRECT_URL), listener);
    }

    public void testRequiresAuthorizationUrl() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams(TEST_CLIENT_ID, TEST_CLIENT_SECRET, null, TEST_REDIRECT_URL), listener);
    }

    public void testRequiresNotNullRedirectUrl() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL, null), listener);
    }

    public void testRequiresNotEmptyRedirectUrl() throws Exception {
        baseTestRequires(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, new AuthorizationParams(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_AUTHORIZATION_URL, ""), listener);
    }

    public void testRequiresAuthorizationParameters() throws Exception {
        try {
            saveCredential();
            getClient().executeHttpRequest(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
            fail();
        } catch (AuthorizationException e) {
            // success
        }
    }

    public void testRequiresSavedCredential() throws Exception {
        shouldSuccessListenerBeCalled = false;
        shouldRequestBeSuccessful = false;
        savePreferences();
        getClient().executeHttpRequest(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
        semaphore.acquire();
    }

    public void testDoesNotRequiresHeaders() throws Exception {
        setupSuccessfulRequestResults(TEST_HTTP_STATUS_CODE, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, TEST_CONTENT_DATA);
        getClient().executeHttpRequest(HTTP_GET, url, null, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
        semaphore.acquire();
        assertEquals(1, apiProvider.getApiRequests().size());
        assertNull(apiProvider.getApiRequests().get(0).getRequestHeaders());
    }

    public void testSuccessfulGet() throws Exception {
        setupSuccessfulRequestResults(TEST_HTTP_STATUS_CODE, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, TEST_CONTENT_DATA);
        apiProvider.setHttpRequestResults(TEST_HTTP_STATUS_CODE, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, TEST_CONTENT_DATA);
        getClient().executeHttpRequest(HTTP_GET, url, new HashMap<String, Object>(headers), TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
        semaphore.acquire();
        assertEquals(1, apiProvider.getApiRequests().size());
        assertEquals(headers, apiProvider.getApiRequests().get(0).getRequestHeaders());
        assertEquals(TEST_HEADER_VALUE, apiProvider.getApiRequests().get(0).getRequestHeaders().get(TEST_HEADER_NAME));
        assertEquals(url, apiProvider.getApiRequests().get(0).getRequestUrl());
        assertEquals(TEST_CONTENT_TYPE, apiProvider.getApiRequests().get(0).getRequestContentType());
        assertEquals(TEST_CONTENT_ENCODING, apiProvider.getApiRequests().get(0).getRequestContentEncoding());
        assertEquals(TEST_HTTP_STATUS_CODE, apiProvider.getApiRequests().get(0).getReturnedHttpStatusCode());
    }

    public void testFailedGet() throws Exception {
        setupFailedRequestResults();
        getClient().executeHttpRequest(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
        semaphore.acquire();
        assertEquals(1, apiProvider.getApiRequests().size());
    }

    public void testFailedGet404() throws Exception {
        setupSuccessfulRequestResultsWithFailedHttpStatus(404, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, TEST_CONTENT_DATA);
        getClient().executeHttpRequest(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
        semaphore.acquire();
        assertEquals(1, apiProvider.getApiRequests().size());
    }

    public void testUnauthorized() throws Exception {
        setupFailedUnauthorizedResults();
        getClient().executeHttpRequest(HTTP_GET, url, headers, TEST_CONTENT_TYPE, TEST_CONTENT_ENCODING, null, listener);
        semaphore.acquire();
        assertEquals(1, apiProvider.getApiRequests().size());
        assertCredentialEquals(null, apiProvider);
    }

    private void setupSuccessfulRequestResults(int httpStatusCode, String contentType, String contentEncoding, String contentData) {
        savePreferences();
        saveCredential();
        shouldSuccessListenerBeCalled = true;
        shouldRequestBeSuccessful = true;
        apiProvider.setShouldAuthorizedApiRequestBeSuccessful(shouldRequestBeSuccessful);
        setupHttpRequestResults(httpStatusCode, contentType, contentEncoding, contentData);
    }

    private void setupFailedRequestResults() {
        savePreferences();
        saveCredential();
        shouldSuccessListenerBeCalled = false;
        shouldRequestBeSuccessful = false;
        apiProvider.setShouldAuthorizedApiRequestBeSuccessful(shouldRequestBeSuccessful);
    }

    private void setupFailedUnauthorizedResults() {
        savePreferences();
        saveCredential();
        shouldSuccessListenerBeCalled = false;
        shouldRequestBeSuccessful = false;
        shouldUnauthorizedListenerBeCalled = true;
        apiProvider.setShouldAuthorizedApiRequestBeSuccessful(shouldRequestBeSuccessful);
        apiProvider.setShouldAuthorizedApiRequestBeUnauthorized(shouldUnauthorizedListenerBeCalled);
    }

    private void setupSuccessfulRequestResultsWithFailedHttpStatus(int httpStatusCode, String contentType, String contentEncoding, String contentData) {
        savePreferences();
        saveCredential();
        shouldSuccessListenerBeCalled = false;
        shouldRequestBeSuccessful = true;
        apiProvider.setShouldAuthorizedApiRequestBeSuccessful(shouldRequestBeSuccessful);
        setupHttpRequestResults(httpStatusCode, contentType, contentEncoding, contentData);
    }

    private void setupHttpRequestResults(int httpStatusCode, String contentType, String contentEncoding, String contentData) {
        expectedHttpStatusCode = httpStatusCode;
        expectedContentType = contentType;
        expectedContentData = contentData;
        apiProvider.setHttpRequestResults(httpStatusCode, contentType, contentEncoding, contentData);
    }

    private void baseTestRequires(String method, final URL url,
                                  final Map<String, Object> headers,
                                  String contentType,
                                  String contentEncoding,
                                  AuthorizationParams parameters,
                                  final AuthorizedResourceClientImpl.Listener listener) throws Exception {
        try {
            if (parameters != null) {
                getClient().setParameters(parameters);
            }
            getClient().executeHttpRequest(method, url, headers, contentType, contentEncoding, null, listener);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        } catch (AuthorizationException e) {
            // success
        }
    }
}
