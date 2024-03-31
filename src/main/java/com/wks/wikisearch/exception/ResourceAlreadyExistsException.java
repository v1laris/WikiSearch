package com.wks.wikisearch.exception;

import java.io.Serial;

public class ResourceAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }
}
