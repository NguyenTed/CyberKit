package com.cyberkit.cyberkit_server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String extractToolIdFromRequest(String requestUri) {
        Pattern pattern = Pattern.compile("([a-f0-9\\-]{36})");
        Matcher matcher = pattern.matcher(requestUri);
        return matcher.find() ? matcher.group(1) : null;
    }
}
