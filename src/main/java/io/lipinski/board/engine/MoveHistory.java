package io.lipinski.board.engine;

import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class hold order of moves which has been taken during the game.
 * No LOGIC is applied here except method {@link #subtract()}.
 */
class MoveHistory {


    private List<Direction> moves;


    MoveHistory() {
        this.moves = new ArrayList<>();
    }

    private MoveHistory(List<Direction> moves) {
        this.moves = moves;
    }


    MoveHistory add(Direction destination) {
        final var afterMove = new ArrayList<>(this.moves);
        afterMove.add(destination);
        return new MoveHistory(afterMove);
    }

    MoveHistory subtract() throws NoSuchElementException {
        checkSize();
        final var afterSubtract = new ArrayList<>(moves);
        afterSubtract.remove(afterSubtract.size() - 1);
        return new MoveHistory(afterSubtract);
    }

    Direction getLastMove() throws NoSuchElementException {
        checkSize();
        return this.moves.get(this.moves.size() - 1);
    }

    List<Direction> allMoves() {
        return new ArrayList<>(this.moves);
    }


    private void checkSize() {
        if (this.moves.size() == 0)
            throw new IllegalUndoMoveException("You can undo move when no move has been done");
    }

}
