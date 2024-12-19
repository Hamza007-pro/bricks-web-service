package com.project.bricks.exceptions;

public class BlueprintGenerationException extends RuntimeException {
    public BlueprintGenerationException(String message) {
        super(message);
    }

    public BlueprintGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
