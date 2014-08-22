/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import java.net.URL;
import java.util.Map;

public class FakeAuthorizedResourceClient implements AuthorizedResourceClient {

    private boolean isSuccessful;
    private int httpStatusCode;
    private String returnedContentType;
    private String returnedContentEncoding;
    private String returnedContentData;
    private byte[] requestContentData;

    public void setupSuccessfulRequestResults(String contentType, String contentEncoding, String contentData) {
        this.httpStatusCode = 200;
        this.isSuccessful = true;
        this.returnedContentType = contentType;
        this.returnedContentEncoding = contentEncoding;
        this.returnedContentData = contentData;
    }

    public void setupUnauthorizedHttpStatusCode() {
        this.httpStatusCode = 401;
        this.isSuccessful = true;
        this.returnedContentType = "application/json";
        this.returnedContentEncoding = "utf-8";
        this.returnedContentData = "{}";
    }

    public void setupFailedHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
        this.isSuccessful = true;
        this.returnedContentType = "application/json";
        this.returnedContentEncoding = "utf-8";
        this.returnedContentData = "{}";
    }

    @Override
    public void executeHttpRequest(String method,
                                   URL url,
                                   Map<String, Object> headers,
                                   String contentType,
                                   String contentEncoding,
                                   byte[] contentData,
                                   Listener listener) throws AuthorizationException {

        this.requestContentData = contentData;

        if (isSuccessful) {
            if (httpStatusCode != 401) {
                listener.onSuccess(httpStatusCode, returnedContentType, returnedContentEncoding, StreamUtil.getInputStream(returnedContentData));
            } else {
                listener.onUnauthorized();
            }
        }
    }

    public byte[] getRequestContentData() {
        return requestContentData;
    }

}
