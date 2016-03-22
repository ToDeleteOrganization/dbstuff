package com.sdl.bcm.service.exception;

/**
 * Created by mariuscocoi on 2/2/2016.
 */
public class DocumentNotFoundException extends BCMResourceNotFoundException {

    private static final String DEFAULT_DOCUMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Document not found.";

    private String documentId;

    public DocumentNotFoundException() {
        this(DEFAULT_DOCUMENT_NOT_FOUND_EXCEPTION_MESSAGE);
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
