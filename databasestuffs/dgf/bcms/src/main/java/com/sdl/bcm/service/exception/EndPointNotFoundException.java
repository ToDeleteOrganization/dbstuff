package com.sdl.bcm.service.exception;

import org.springframework.http.HttpMethod;

/**
 * Created by mariuscocoi on 2/2/2016.
 */
public class EndPointNotFoundException extends BCMResourceNotFoundException {

    private static final String DEFAULT_ENDPOINT_NOT_FOUND_EXCEPTION_MESSAGE = "Endpoint not found.";

    private String endpoint;

    private HttpMethod method;

    public EndPointNotFoundException() {
        this(DEFAULT_ENDPOINT_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    public EndPointNotFoundException(String message) {
        super(message);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }


}
