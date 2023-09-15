package com.noah.bscript.utils;

public class CharacterUtils {

    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';
    public static final char TAB = '\t';
    public static final char SPACE = ' ';
    public static final char NULL_TERMINATOR = '\0';

    public static boolean isAlphanumeric(char character) {
        return Character.isDigit(character) || Character.isAlphabetic(character) || character == '_';
    }

}
