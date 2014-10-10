/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.UnknownHostException;

public class Logger {

    private static final Object LOCK = new Object();

    private static final String TAG_NAME = "Pivotal";
    private static final String UI_THREAD = "UI";
    private static final String BG_THREAD = "BG";

    private static boolean sIsDebugEnabled = false;
    private static boolean sIsSetup = false;

    private static Listener sListener;
    private static Handler sMainHandler;

    private static class Holder {
        public static final Logger INSTANCE = new Logger();
    }

    public static interface Listener {
        void onLogMessage(String message);
    }

    private Logger() {}

    public static void setup(final Context context) {
        Logger.sIsDebugEnabled = isDebuggable(context);
        Logger.sIsSetup = true;
    }

    private static boolean isDebuggable(final Context context) {
        try {
            final PackageManager pm = context.getPackageManager();
            final ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            return (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isDebugEnabled() {
        return Logger.sIsDebugEnabled;
    }

    public static boolean isSetup() {
        return Logger.sIsSetup;
    }

    public static void i(final String message) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message);
            Log.i(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void w(final String message, final Throwable tr) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message) + ": " + Log.getStackTraceString(tr);
            Log.w(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void w(final Throwable tr) {
        if (sIsDebugEnabled) {
            final String formattedString = format("") + Log.getStackTraceString(tr);
            Log.w(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void w(final String message) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message);
            Log.w(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void v(final String message) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message);
            Log.v(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void d(final String message) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message);
            Log.d(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void d(final String message, final Throwable tr) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message) + ": " + Log.getStackTraceString(tr);
            Log.d(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void d(final Throwable tr) {
        if (sIsDebugEnabled) {
            final String formattedString = format("") + Log.getStackTraceString(tr);
            Log.d(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void fd(final String message, final Object... objects) {
        if (sIsDebugEnabled) {
            final String formattedString = format(message, objects);
            Log.d(TAG_NAME, formattedString);
            sendMessageToListener(formattedString);
        }
    }

    public static void e(final String message) {
        final String formattedString = format(message);
        sendMessageToListener(formattedString);
        Log.e(TAG_NAME, formattedString);
    }

    public static void ex(final String message, final Throwable tr) {
        final String stackTraceString;
        if (tr instanceof UnknownHostException) {
            stackTraceString = tr.getLocalizedMessage();
        } else {
            stackTraceString = Log.getStackTraceString(tr);
        }
        final String formattedString = format(message) + ": " + stackTraceString;
        sendMessageToListener(formattedString);
        Log.e(TAG_NAME, formattedString);
    }

    public static void ex(final Throwable tr) {
        final String formattedString = format("") + Log.getStackTraceString(tr);
        sendMessageToListener(formattedString);
        Log.e(TAG_NAME, formattedString);
    }

    private static String format(final String message, final Object... objects) {
        final StackTraceElement s = StackUtils.getCallingStackTraceElement();

        final String thread = isUiThread() ? UI_THREAD : BG_THREAD;
        final long threadId = Thread.currentThread().getId();

        final String klass = s.getClassName();
        final String method = s.getMethodName();
        final int line = s.getLineNumber();

        final String formatted = String.format(message, objects);
        return String.format("*%s* (%d) [%s:%s:%d] %s", thread, threadId, klass, method, line, formatted);
    }

    private static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void setListener(final Listener listener) {
        synchronized (LOCK) {
            Logger.sListener = listener;
            if (Logger.sListener != null && Logger.sMainHandler == null) {
                Logger.sMainHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    public static void sendMessageToListener(final String message) {
        synchronized (LOCK) {
            if (sListener != null && sMainHandler != null) {
                final Listener localListener = sListener;
                sMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        int indexBracket = message.indexOf("] ");
                        if (indexBracket == -1) {
                            localListener.onLogMessage(message);
                        } else {
                            final String messageWithoutLeader = message.substring(indexBracket + 2);
                            localListener.onLogMessage(messageWithoutLeader);
                        }
                    }
                });
            }
        }
    }

    private static final class StackUtils {

        private static StackTraceElement getCallingStackTraceElement() {
            final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            final int index = getFirstElementIndexForLoggerClass(elements);
            return getFirstElementInCallingClass(elements, index);
        }

        private static int getFirstElementIndexForLoggerClass(final StackTraceElement[] elements) {
            for (int i = 0; i < elements.length; i += 1) {
                final StackTraceElement s = elements[i];
                if (stackTraceElementIsForLoggerClass(s)) {
                    return i;
                }
            }
            throw new IllegalArgumentException("No class reference found");
        }

        private static StackTraceElement getFirstElementInCallingClass(final StackTraceElement[] elements, final int index) {
            for (int i = index; i < elements.length; i += 1) {
                final StackTraceElement s = elements[i];
                if (!stackTraceElementIsForLoggerClass(s)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("No calling class reference found");
        }

        private static boolean stackTraceElementIsForLoggerClass(final StackTraceElement s) {
            final String className = Logger.class.getName();
            return s.getClassName().equals(className);
        }
    }
}
