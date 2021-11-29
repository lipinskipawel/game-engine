package com.github.lipinskipawel.board.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

/**
 * This class is responsible for holding all moves made by players and
 * computing current player to move. This class is also responsible for
 * 'small' moves made by the player during the game.
 * 'Small' move is when the ball is bouncing off the wall or different type of obstacle.
 */
final class MoveHistory {

    private final Queue<Move> moves;
    private final List<Direction> smallMove;


    MoveHistory() {
        this.moves = new ArrayDeque<>();
        this.smallMove = new ArrayList<>();
    }

    private MoveHistory(final Queue<Move> moves,
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
        directions.addAll(new ArrayList<>(this.smallMove));
        return directions;
    }

    MoveHistory addMove(final Move move) {
        if (this.smallMove.size() > 0) {
            final var copyMoves = new ArrayDeque<>(this.moves);
            final var copySmallMoves = new ArrayList<>(this.smallMove);
            copySmallMoves.addAll(move.getMove());
            copyMoves.add(new Move(copySmallMoves));
            return new MoveHistory(copyMoves, new ArrayList<>());
        } else {
            final var copyMoves = new ArrayDeque<>(this.moves);
            copyMoves.add(move);
            return new MoveHistory(copyMoves, new ArrayList<>());
        }
    }

    MoveHistory add(final Direction direction) {
        final var copySmall = new ArrayList<>(this.smallMove);
        copySmall.add(direction);
        return new MoveHistory(new ArrayDeque<>(this.moves), copySmall);
    }

    /**
     * Undo small move if there is no small moves already made then
     * it will change last move into small move and then remove one small move
     *
     * @return
     */
    MoveHistory forceUndo() {
        if (this.smallMove.size() == 0) {
            final var copyMoves = new ArrayDeque<>(this.moves);
            final var directions = copyMoves.removeLast().getMove();
            directions.remove(directions.size() - 1);
            return new MoveHistory(copyMoves, directions);
        }
        final var copySmall = new ArrayList<>(this.smallMove);
        copySmall.remove(copySmall.size() - 1);
        return new MoveHistory(new ArrayDeque<>(this.moves), copySmall);
    }

    /**
     * If there is move to undo this method will undo move and all small moves
     * otherwise will not undo anything
     *
     * @return
     */
    MoveHistory undoMove() {
        if (this.moves.size() <= 0)
            return this;
        final var copyMoves = new ArrayDeque<>(this.moves);
        copyMoves.removeLast();
        return new MoveHistory(copyMoves, new ArrayList<>());
    }

    /**
     * Undo only the small move
     *
     * @return
     */
    MoveHistory undo() {
        if (this.smallMove.size() == 0)
            return new MoveHistory(new ArrayDeque<>(this.moves), new ArrayList<>(this.smallMove));
        final var copySmall = new ArrayList<>(this.smallMove);
        copySmall.remove(copySmall.size() - 1);
        return new MoveHistory(new ArrayDeque<>(this.moves), copySmall);
    }

    boolean currentPlayer() {
        return this.moves.size() % 2 == 0;
    }

    /**
     * @return last Direction
     */
    Optional<Direction> getLastDirection() {
        if (this.smallMove.size() == 0) {
            if (this.moves.size() == 0) {
                return Optional.empty();
            } else {
                // should be remove last item
                final var copyMoves = new ArrayDeque<>(this.moves);
                final var last = copyMoves.removeLast().getMove();
                return Optional.of(last.get(last.size() - 1));
            }
        } else {
            return Optional.of(this.smallMove.get(this.smallMove.size() - 1));
        }
    }

    boolean isSmallMoveHasBeenMade() {
        return !this.smallMove.isEmpty();
    }

    Optional<Move> getLastMove() {
        try {
            return Optional.of(this.moves.peek());
        } catch (EmptyStackException exception) {
            return Optional.empty();
        }
    }
}
