/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Used by the Logger Library to log messages to the device log.  An optional 'listener'
 * can be registered so an application can watch all the message traffic.
 *
 * If the "debuggable" flag is "false" in the application manifest file then only warning
 * or error messages will be printed to the device log.
 */
public class Logger {

    public static final String TAG_NAME = "PivotalMSS";

    private static final String UI_THREAD = "UI";
    private static final String BACKGROUND_THREAD = "BG";

    private static boolean isDebuggable = false;
    private static Logger loggerInstance;
    private static boolean isSetup = false;
    private static Listener listener;
    private static Object lock = new Object();
    private static Handler mainHandler;

    public static interface Listener {
        void onLogMessage(String message);
    }

    public static void setup(Context context) {
        Logger.isDebuggable = DebugUtil.getInstance(context).isDebuggable();
        Logger.isSetup = true;
    }

    private Logger() {
    }

    public static boolean isSetup() {
        return Logger.isSetup;
    }

    public static void i(String message) {
        final String formattedString = formatMessage(message, new Object[] {});
        if (isDebuggable) {
            Log.i(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void w(String message, Throwable tr) {
        final String formattedString = formatMessage(message, new Object[] {}) + ": " + Log.getStackTraceString(tr);
        if (isDebuggable) {
            Log.w(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void w(Throwable tr) {
        final String formattedString = formatMessage("", new Object[] {}) + Log.getStackTraceString(tr);
        if (isDebuggable) {
            Log.w(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void w(String message) {
        final String formattedString = formatMessage(message, new Object[] {});
        if (isDebuggable) {
            Log.w(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void v(String message) {
        final String formattedString = formatMessage(message, new Object[] {});
        if (isDebuggable) {
            Log.v(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void d(String message) {
        final String formattedString = formatMessage(message, new Object[] {});
        if (isDebuggable) {
            Log.d(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void d(String message, Throwable tr) {
        final String formattedString = formatMessage(message, new Object[] {}) + ": " + Log.getStackTraceString(tr);
        if (isDebuggable) {
            Log.d(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void d(Throwable tr) {
        final String formattedString = formatMessage("", new Object[] {}) + Log.getStackTraceString(tr);
        if (isDebuggable) {
            Log.d(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void fd(String message, Object... objects) {
        final String formattedString = formatMessage(message, objects);
        if (isDebuggable) {
            Log.d(TAG_NAME, formattedString);
        }
        sendMessageToListener(formattedString);
    }

    public static void e(String message) {
        final String formattedString = formatMessage(message, new Object[] {});
        sendMessageToListener(formattedString);
        Log.e(TAG_NAME, formattedString);
    }

    public static void ex(String message, Throwable tr) {
        final String stackTraceString;
        if (tr instanceof UnknownHostException) {
            // Note: can't get the stack trace of an UnknownHostException
            stackTraceString = tr.getLocalizedMessage();
        } else {
            stackTraceString = Log.getStackTraceString(tr);
        }
        final String formattedString = formatMessage(message, new Object[] {}) + ": " + stackTraceString;
        sendMessageToListener(formattedString);
        Log.e(TAG_NAME, formattedString);
    }

    public static void ex(Throwable tr) {
        final String formattedString = formatMessage("", new Object[] {}) + Log.getStackTraceString(tr);
        sendMessageToListener(formattedString);
        Log.e(TAG_NAME, formattedString);
    }

    private static String formatMessage(String message, Object... objects) {

        final StackTraceElement s = getCallingStackTraceElement();
        String formattedMessage = String.format(Locale.getDefault(), "[%s:%s:%d:tid%d] ", s.getClassName(), s.getMethodName(), s.getLineNumber(), Thread.currentThread().getId());

        if (objects.length > 0)
            formattedMessage += String.format(message, objects);
        else
            formattedMessage += message;

        return addThreadInfo(formattedMessage);
    }

    private static StackTraceElement getCallingStackTraceElement() {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        final int indexForFirstElementInLoggerClass = getFirstElementIndexForLoggerClass(stackTraceElements);
        return getFirstElementInCallingClass(stackTraceElements, indexForFirstElementInLoggerClass);
    }

    private static int getFirstElementIndexForLoggerClass(final StackTraceElement[] stackTraceElements) {
        for (int i = 0; i < stackTraceElements.length; i += 1) {
            final StackTraceElement s = stackTraceElements[i];
            if (stackTraceElementIsForLoggerClass(s)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No " + TAG_NAME + " Logger class reference found");
    }

    private static StackTraceElement getFirstElementInCallingClass(StackTraceElement[] stackTraceElements, int indexForFirstElementInLoggerClass) {
        for (int i = indexForFirstElementInLoggerClass; i < stackTraceElements.length; i += 1) {
            final StackTraceElement s = stackTraceElements[i];
            if (!stackTraceElementIsForLoggerClass(s)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No calling class reference found");
    }

    private static String getLoggerClassName() {
        return getInstance().getClass().getName();
    }

    private static boolean stackTraceElementIsForLoggerClass(StackTraceElement s) {
        final String loggerClassName = getLoggerClassName();
        return s.getClassName().equals(loggerClassName);
    }

    private static String addThreadInfo(String string) {
        if (isUiThread()) {
            return "*" + UI_THREAD + "* " + string;
        }
        return "*" + BACKGROUND_THREAD + "* " + string;
    }

    private static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static Logger getInstance() {
        if (loggerInstance == null)
            loggerInstance = new Logger();
        return loggerInstance;
    }

    public static void setListener(Listener listener) {
        synchronized (lock) {
            Logger.listener = listener;
            if (Logger.listener != null && Logger.mainHandler == null) {
                Logger.mainHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    public static void sendMessageToListener(final String message) {
        synchronized (lock) {
            if (listener != null && mainHandler != null) {
                final Listener localListener = listener;
                mainHandler.post(new Runnable() {
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
}
