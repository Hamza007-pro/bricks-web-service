package com.project.bricks.exceptions;

public class FileGeneratorException extends RuntimeException {
    public FileGeneratorException(String message) {
        super(message);
    }

    public FileGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
