package com.hospital.util;

import com.hospital.model.Jsonable;

import java.util.List;
import java.util.Locale;

/**
 * Tiny helper for building JSON text by hand (no external library needed).
 */
public class JsonUtil {

    private JsonUtil() { }

    /** Turns a Java String into a JSON string literal (quotes + escaping). null becomes null. */
    public static String str(String s) {
        if (s == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.append('"').toString();
    }

    /** Money with exactly 2 decimals, e.g. 600.00 (Locale.US makes sure the separator is a dot). */
    public static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    /** Turns a list of model objects into a JSON array: [ {...}, {...} ] */
    public static String array(List<? extends Jsonable> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(items.get(i).toJson());
        }
        return sb.append(']').toString();
    }

    /** Standard reply for actions: {"success":true,"message":"..."} */
    public static String message(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":" + str(message) + "}";
    }
}
