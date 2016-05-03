package com.meiqi.data.engine.excel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: 
 * Date: 13-11-1
 * Time: 上午10:40
 */
public final class StringPool {
    public static final String EMPTY = "", TRUE = "TRUE", FALSE = "FALSE";


    private static final ConcurrentHashMap<String, String> cache
            = new ConcurrentHashMap<String, String>();

    public static String getString(String input) throws NumberFormatException {
        String result = cache.get(input);

        if (result == null) {
            result = input.substring(1, input.length() - 1);
            result = result.replace("\"\"", "\"");

            cache.put(input, result);
        }

        return result;
    }
}
