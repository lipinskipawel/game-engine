package io.lipinski.board.engine;

import io.lipinski.board.BoardInterface;
import io.lipinski.board.MutableBoard;

public class Boards {


    public static BoardInterface mutableBoard() { return new MutableBoard(); }

    public static BoardInterface2 immutableBoard() {
        return new ImmutableBoard();
    }

}
