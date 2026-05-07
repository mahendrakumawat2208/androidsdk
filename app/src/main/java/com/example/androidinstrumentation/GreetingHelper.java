package com.example.androidinstrumentation;

/**
 * Simple Java utility called from Kotlin ({@link MainActivity}) to show mixed-language modules.
 */
public final class GreetingHelper {

    private GreetingHelper() {
    }

    public static String getGreeting(String name) {
        return "Hello, " + name + "!";
    }
}
