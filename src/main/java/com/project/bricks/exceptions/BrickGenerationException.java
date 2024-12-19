package com.project.bricks.exceptions;

public class BrickGenerationException extends RuntimeException {
    public BrickGenerationException(String message) {
        super(message);
    }

    public BrickGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
