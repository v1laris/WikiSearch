package com.wks.wikisearch.exception;

import java.io.Serial;

public class ObjectAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ObjectAlreadyExistsException(final String message) {
        super(message);
    }
}
