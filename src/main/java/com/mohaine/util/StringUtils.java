package com.mohaine.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {


    private StringUtils() {
    }


    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter printer = new PrintWriter(sw);
        if (t != null) {
            t.printStackTrace(printer);
        }
        String stack = sw.getBuffer().toString();
        return stack;
    }

    public static String valueOf(Object string) {
        if (string == null) {
            return "";
        }
        return string.toString();
    }

    public static String toString(Object string) {
        if (string == null) {
            return null;
        }
        return string.toString();
    }

    public static boolean hasLength(Object string) {
        return hasLength(valueOf(string));
    }

    public static boolean hasLength(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        int strLength = string.length();
        for (int i = 0; i < strLength; i++) {
            char charAt = string.charAt(i);
            if (charAt > ' ') {
                return true;
            }
        }
        return false;
    }


}