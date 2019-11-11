package com.github.lipinskipawel.board.engine;

import java.util.List;

public interface BoardInterface extends Transformation {

    /**
     * This method will move the ball in the given direction.
     *
     * @param direction this is the direction to move a ball
     * @return a new BoardInterface object with a ball position
     * @throws RuntimeException when the move can not be made
     */
    BoardInterface executeMove(final Direction direction);

    /**
     * This method will move the ball by the given move.
     * The method is safe to use and it will update Move History.
     *
     * @param move this move will be made
     * @return a new BoardInterface object with a new move
     * @throws RuntimeException when the move can not be made
     */
    BoardInterface executeMove(final Move move);

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
     * @throws RuntimeException when the move can not be undo at the beggining of the game
     */
    BoardInterface undo();

    List<Move> allLegalMoves();

    /**
     * @return all Moves made by each {@link Player}
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

    boolean isOver();

    Player getPlayer();

}
