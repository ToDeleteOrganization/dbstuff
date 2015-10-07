package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The approval level of the translation for a target segment pair.
 * This property does not apply to source segments.
 */
public enum ConfirmationLevel {
    //NotTranslated - default value
    NOT_TRANSLATED("NotTranslated"),

    //Draft
    DRAFT("Draft"),

    //TRANSLATED
    TRANSLATED("Translated"),

    //Translation Rejected
    REJECTED_TRANSLATION("RejectedTranslation"),

    //Translation Approved
    APPROVED_TRANSLATION("ApprovedTranslation"),

    //Sign-off rejected
    REJECTED_SIGN_OFF("RejectedSignOff"),

    //Signed off
    APPROVED_SIGN_OFF("ApprovedSignOff");

    private String name;

    ConfirmationLevel(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
