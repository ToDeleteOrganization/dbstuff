package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TextContextMatchLevel {
    NONE("None"),
    SOURCE("Source"),
    SOURCE_AND_TARGET("SourceAndTarget");

    private String name;

    TextContextMatchLevel(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
