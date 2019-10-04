package io.lipinski.board.engine;

import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.List;

public interface BoardInterface extends Transformation {

    /**
     * This method will move the ball in the given direction.
     *
     * @param direction this is the direction to move a ball
     * @return a new BoardInterface object with a ball position
     * @throws IllegalMoveException when the move can not be made
     */
    BoardInterface executeMove(final Direction direction) throws IllegalMoveException;

    /**
     * This method will move the ball by the given move.
     * The method is safe to use and it will update Move History.
     *
     * @param move this move will be made
     * @return a new BoardInterface object with a new move
     * @throws IllegalMoveException when the move can not be made
     */
    BoardInterface executeMove(final Move move) throws IllegalMoveException;

    /**
     * This method will undo only current player small moves.
     * Which means that this method will never change the current player.
     *
     * @return a new BoardInterface object with a undo Player move
     */
    BoardInterface undoPlayerMove();

    /**
     * This method will undo small move and hasn't any restrictions. It means
     * that this method can change next player to move.
     * If method is call enough time it is possible to undo whole game.
     *
     * @return a new BoardInterface object with a undo move
     */
    BoardInterface undo();

    List<Move> allLegalMoves();

    /**
     *
     * @return all Moves made by each {@link io.lipinski.board.engine.Player}
     */
    List<Move> moveHistory();

    /**
     * 'Small' move is when not done yet. Move is done when there is
     * next player to move.
     *
     * @return all 'small' which has been made
     */
    List<Direction> allMoves();
    boolean isMoveAllowed(final Direction destination);
    int getBallPosition();
    Point getBallAPI();
    boolean isGoal();

    Player getPlayer();

}
