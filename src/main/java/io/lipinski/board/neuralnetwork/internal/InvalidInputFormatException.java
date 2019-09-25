package io.lipinski.board.neuralnetwork.internal;

class InvalidInputFormatException extends RuntimeException {

    InvalidInputFormatException() { }

    InvalidInputFormatException(final String message) {
        super(message);
    }

    InvalidInputFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }

    InvalidInputFormatException(final Throwable cause) {
        super(cause);
    }

    InvalidInputFormatException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
