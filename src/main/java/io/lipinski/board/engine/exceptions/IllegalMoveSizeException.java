package io.lipinski.board.engine.exceptions;

public class IllegalMoveSizeException extends RuntimeException {

    public IllegalMoveSizeException() {
    }

    public IllegalMoveSizeException(final String message) {
        super(message);
    }

    public IllegalMoveSizeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IllegalMoveSizeException(final Throwable cause) {
        super(cause);
    }

    public IllegalMoveSizeException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
