package com.tks.util;

/**
 * Contains string utility static functions
 */
public class StringProcess {
    public static String makeSingleSpaced(String input) {
        input = input.trim();
        input = input.replaceAll("\\s{2,}", " ");
        return input;
    }
}