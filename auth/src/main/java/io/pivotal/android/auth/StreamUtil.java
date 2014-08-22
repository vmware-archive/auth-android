/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class StreamUtil {

    public static String readInput(InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];

        while (true) {
            final int numberBytesRead = inputStream.read(buffer);
            if (numberBytesRead < 0) {
                break;
            }
            byteArrayOutputStream.write(buffer, 0, numberBytesRead);
        }

        return new String(byteArrayOutputStream.toByteArray());
    }

    public static InputStream getInputStream(String contentData) {
        return new ByteArrayInputStream(contentData.getBytes());
    }
}
