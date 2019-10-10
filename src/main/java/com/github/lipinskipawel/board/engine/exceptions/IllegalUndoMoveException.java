package com.github.lipinskipawel.board.engine.exceptions;

public class IllegalUndoMoveException extends RuntimeException {

    public IllegalUndoMoveException() {
    }

    public IllegalUndoMoveException(final String message) {
        super(message);
    }

    public IllegalUndoMoveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IllegalUndoMoveException(final Throwable cause) {
        super(cause);
    }

    public IllegalUndoMoveException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
