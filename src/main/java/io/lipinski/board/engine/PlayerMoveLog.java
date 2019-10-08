package io.lipinski.board.engine;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static java.util.stream.Collectors.toList;

/**
 * This class is responsible for holding all moves made by players and
 * computing current player to move. This class is also responsible for
 * 'small' moves made by the player during the game.
 * 'Small' move is when the ball is bouncing off the wall or different type of obstacle.
 */
final class PlayerMoveLog {

    private final Stack<Move> moves;
    private final List<Direction> smallMove;


    PlayerMoveLog() {
        this.moves = new Stack<>();
        this.smallMove = new ArrayList<>();
    }

    private PlayerMoveLog(final Stack<Move> moves,
                          final List<Direction> smallMove) {
        this.moves = moves;
        this.smallMove = smallMove;
    }

    List<Move> allMoves() {
        return new ArrayList<>(this.moves);
    }

    List<Direction> allDirections() {
        final var directions = new ArrayList<>(this.moves)
                .stream()
                .map(Move::getMove)
                .flatMap(List::stream)
                .collect(toList());
        directions.addAll(this.smallMove);
        return directions;
    }

    PlayerMoveLog addMove(final Move move) {
        if (this.smallMove.size() > 0) {
            this.moves.add(new Move(this.smallMove));
            this.smallMove.clear();
            this.moves.add(move);
            return new PlayerMoveLog(this.moves, this.smallMove);
        }
        this.moves.add(move);
        this.smallMove.clear();
        return new PlayerMoveLog(this.moves, this.smallMove);
    }

    PlayerMoveLog add(final Direction direction) {
        this.smallMove.add(direction);
        return new PlayerMoveLog(this.moves, this.smallMove);
    }

    PlayerMoveLog undoMove() {
        if (this.moves.size() <= 0)
            return this;
        this.moves.pop();
        this.smallMove.clear();
        return new PlayerMoveLog(this.moves, this.smallMove);
    }

    PlayerMoveLog undo() {
        if (this.smallMove.size() == 0)
            return new PlayerMoveLog(this.moves, this.smallMove);
        this.smallMove.remove(this.smallMove.size() -1);
        return new PlayerMoveLog(this.moves, this.smallMove);
    }

    Player currentPlayer() {
        return this.moves.size() % 2 == 0 ? Player.FIRST : Player.SECOND;
    }

    Optional<Direction> getLastSmallMove() {
        return Optional.of(this.smallMove.get(this.smallMove.size() -1));
    }

    Optional<Move> getLastMove() {
        try {
            return Optional.of(this.moves.peek());
        } catch (EmptyStackException exception) {
            return Optional.empty();
        }
    }
}
