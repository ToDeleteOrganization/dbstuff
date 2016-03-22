package com.sdl.bcm.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariuscocoi on 2/1/2016.
 */
public abstract class BCMResourceNotFoundException extends RuntimeException {

    private static final String BCMS_DEFAULT_NOT_FOUND_EXCEPTION_MESSAGE = "The requested resource is not available.";

    private String customReason;

    public BCMResourceNotFoundException() {
        this(BCMS_DEFAULT_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    public BCMResourceNotFoundException(final String errorMessage) {
        super(errorMessage);
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

}
