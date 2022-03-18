package com.optimised.cylonAlarms.tools.iniFilesToDB;

import java.util.regex.Pattern;

public class Converions {
    private static final Pattern isInteger = Pattern.compile("[+-]?\\d+");

    public static final int tryParseInt(String value) {
        if (value == null || !isInteger.matcher(value).matches()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }
}
