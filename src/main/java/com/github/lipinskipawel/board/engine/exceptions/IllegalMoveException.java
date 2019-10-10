package com.github.lipinskipawel.board.engine.exceptions;

public class IllegalMoveException extends IllegalStateException {


    public IllegalMoveException() {
    }

    public IllegalMoveException(String s) {
        super(s);
    }

    public IllegalMoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalMoveException(Throwable cause) {
        super(cause);
    }
}
