package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Values combined as flags to indicate expected use cases for a dependency file
 */
public enum DependencyFileUsage {
    NONE("None"),
    EXTRACTION("Extraction"),
    GENERATION("Generation"),
    FINAL("Final");

    private String name;

    DependencyFileUsage(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
