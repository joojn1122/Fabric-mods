package com.joojn.meteoraddon.chat;

import java.util.function.BiFunction;

public enum MessageFilter {
    EXACT("Exact", "Exact match", String::equals),
    CONTAINS("Contains", "Contains filter", String::contains),
    STARTS_WITH("Starts with", "Starts with filter", String::startsWith),
    ENDS_WITH("Ends with", "Ends with filter", String::endsWith),
    REGEX("Regex", "Regex filter", String::matches);


    public final String name;
    public final String description;
    public final BiFunction<String, String, Boolean> matcher;

    MessageFilter(String name, String description, BiFunction<String, String, Boolean> matcher)
    {
        this.name = name;
        this.description = description;
        this.matcher = matcher;
    }
}
