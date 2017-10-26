package com.prayansh.upass.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prayansh on 2017-10-21.
 */
public class Util {

    private Util() {
    }

    public static String readConfigVar(String key) {
        return System.getenv(key);
    }

    public static Map<String, String> simpleKVP(String key, String value) {
        Map<String, String> result = new HashMap<>();
        result.put(key, value);
        return result;
    }
}
