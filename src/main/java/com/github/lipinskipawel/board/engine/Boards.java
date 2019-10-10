package com.github.lipinskipawel.board.engine;


public class Boards {


    public static BoardInterface immutableBoard() {
        return new ImmutableBoard();
    }

}
