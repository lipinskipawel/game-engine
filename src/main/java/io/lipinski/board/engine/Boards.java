package io.lipinski.board.engine;


public class Boards {


    public static BoardInterface2 immutableBoard() {
        return new ImmutableBoard();
    }

}
