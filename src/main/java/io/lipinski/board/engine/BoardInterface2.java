package io.lipinski.board.engine;

import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.List;

public interface BoardInterface2 extends Transformation {

    /**
     * This method will move the ball in the given direction.
     *
     * @param direction this is the direction to move a ball
     * @return a new BoardInterface2 object with a ball position
     * @throws IllegalMoveException when the move can not be made
     */
    BoardInterface2 executeMove(final Direction direction) throws IllegalMoveException;

    /**
     * This method will move the ball by the given move.
     * The method is safe to use and it will update Move History.
     *
     * @param move this move will be made
     * @return a new BoardInterface2 object with a new move
     * @throws IllegalMoveException when the move can not be made
     */
    BoardInterface2 executeMove(final Move move) throws IllegalMoveException;
    BoardInterface2 undoMove() throws IllegalUndoMoveException;

    List<Move> allLegalMoves();
    List<Move> moveHistory();
    boolean isMoveAllowed(final Direction destination);
    int getBallPosition();
    Point2 getBallAPI(); // change this into List<Direction> getAllDirections()
    boolean isGoal();

    Player getPlayer();

}
