package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.List;

public interface BoardInterface2 {

    BoardInterface2 executeMove(final Direction destination) throws IllegalMoveException;
    BoardInterface2 executeMove(final Move move) throws IllegalMoveException;
    BoardInterface2 undoMove() throws IllegalUndoMoveException;

    List<Move> allLegalMoves();
    boolean isMoveAllowed(final Direction destination);
    int getBallPosition();
    Point2 getBallAPI();
    boolean isGoal();

    Player getPlayer();

}
