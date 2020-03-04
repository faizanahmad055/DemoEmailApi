package com.myemail.api.util;

public class ResilientValue {

    public static final int BACKOFF_MILLISECONDS = 1000;
    public static final int MAX_RETRY = 3;

    private ResilientValue() {
        throw new IllegalStateException("Utility class");
    }
}