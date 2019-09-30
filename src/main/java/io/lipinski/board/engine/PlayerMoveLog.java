package io.lipinski.board.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class holds all moves made by players and compute current player to move.
 * This class is not responsible for 'small' moves made by the player
 * during the game. 'Small' move is when the ball is bouncing off the wall
 * or different type of obstacle.
 */
final class PlayerMoveLog {

    private final Stack<Move> moves;


    PlayerMoveLog() {
        this.moves = new Stack<>();
    }

    private PlayerMoveLog(final Stack<Move> moves) {
        this.moves = moves;
    }

    List<Move> allMoves() {
        return new ArrayList<>(this.moves);
    }

    PlayerMoveLog addMove(final Move move) {
        this.moves.add(move);
        return new PlayerMoveLog(this.moves);
    }

    PlayerMoveLog undoMove() {
        this.moves.pop();
        return new PlayerMoveLog(this.moves);
    }

    Player currentPlayer() {
        return this.moves.size() % 2 == 0 ? Player.FIRST : Player.SECOND;
    }

}
