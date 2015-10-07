package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum expressing the severity of one comment.
 */
public enum CommentSeverity {
    // Informational purpose
    LOW ("Low"),    //1

    // Warning, likely an important issue
    MEDIUM("Medium"), //2

    // Error, a severe issue
    HIGH("High");   //3

    private String name;

     CommentSeverity(String name) {
         this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
