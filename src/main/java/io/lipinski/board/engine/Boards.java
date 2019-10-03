package io.lipinski.board.engine;


public class Boards {


    public static BoardInterface immutableBoard() {
        return new ImmutableBoard();
    }

}
