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

    /**
     * Returns all possible moves whenever this move ends up hitting the corner or not.
     * This method could be time consuming based on the board state. There can be many
     * moves already played on the board and the complexity could be high.
     *
     * @return Returns all possible moves for current {@link Player}
     */
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

    /**
     * Move is allowed when it hasn't been made or the move from opposite
     * direction was't made yet.
     *
     * @param destination possible direction to move ball
     * @return true or false whenever the move is allowed
     */
    boolean isMoveAllowed(final Direction destination);

    /**
     * This method will be move into {@link Point} API.
     *
     * @return position where the ball is
     */
    @Deprecated
    int getBallPosition();

    /**
     * @return current {@link Point} where the ball is present
     */
    Point getBallAPI();

    /**
     * Goal area is defined by the three inner points. On the board there are
     * two goal area.
     *
     * @return true or false whenever ball in the one of the goal area
     */
    boolean isGoal();

    /**
     * Returns current {@link Player} to move. When new board is created
     * then this method will return {@link Player#FIRST}. Whenever new move
     * has been made then opposite Player will be returned. <p>A new move is made
     * only when</p>:
     * - ball has been kicked to new point
     * - ball has been kicked into the goal area
     * - ball has been stuck into corner or other forms without possibility to move
     *
     * <p>This method will not return opposite player when</p>:
     * - ball has been kicked to the wall or to already drawn move
     *
     * @return {@link Player#FIRST} or {@link Player#SECOND}
     */
    Player getPlayer();

    /**
     * @return true if any of player score a goal or there are no possible moves to make. Otherwise false.
     */
    boolean isGameOver();

    /**
     * This method is able to change player which is allowed to make a move. It could be invoked in any board state.
     * This method will succeed only when non small moves has been made.
     *
     * @param nextPlayerToMove this PLayer will be next to move
     * @return a new instance of BoardInterface with the same logical state <strong>except</strong> next player to move
     * @throws ChangePlayerIsNotAllowed whenever small move has been made and wasn't undo
     */
    BoardInterface nextPlayerToMove(final Player nextPlayerToMove) throws ChangePlayerIsNotAllowed;
}
