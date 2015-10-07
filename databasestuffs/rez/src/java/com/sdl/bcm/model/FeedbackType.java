package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FeedbackType {
    ADDED("Added"),
    DELETED("Deleted"),
    COMMENT("Comment");

    private String name;

    FeedbackType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
