package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RevisionType {
    INSERTED("Inserted"),
    DELETED("Deleted"),
    UNCHANGED("Unchanged");

    private String name;

    RevisionType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
