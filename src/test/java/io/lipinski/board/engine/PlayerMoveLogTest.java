package io.lipinski.board.engine;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Internal -- PlayerMoveLog")
class PlayerMoveLogTest {

    @Test
    @DisplayName("Undo first move")
    void undoFirstMove() {
        final var moveLog = new PlayerMoveLog();

        final var afterUndo = moveLog
                .addMove(new Move(List.of(Direction.E, Direction.N)))
                .undoMove();

        Assertions.assertThat(afterUndo).isEqualToComparingFieldByField(moveLog);
    }

    @Test
    @DisplayName("Undo second move")
    void undoSecondMove() {
        final var oneMove = new PlayerMoveLog()
                .addMove(new Move(List.of(Direction.E, Direction.N)));

        final var afterMoveAndUndo = oneMove
                .addMove(new Move(List.of(Direction.NE, Direction.W)))
                .undoMove();

        Assertions.assertThat(afterMoveAndUndo).isEqualToComparingFieldByField(oneMove);
    }

    @Test
    @DisplayName("Check player 2 moves")
    void checkPlayer() {
        final var moveLog = new PlayerMoveLog()
                .addMove(new Move(List.of(Direction.NE)))
                .addMove(new Move(List.of(Direction.N)));

        Assertions.assertThat(moveLog.currentPlayer()).isEqualByComparingTo(Player.FIRST);
    }

    @Test
    @DisplayName("Check player 2 moves 1 undo")
    void checkPlayerUndo() {
        final var moveLog = new PlayerMoveLog()
                .addMove(new Move(List.of(Direction.NE)))
                .addMove(new Move(List.of(Direction.N)))
                .undoMove();

        Assertions.assertThat(moveLog.currentPlayer()).isEqualByComparingTo(Player.SECOND);
    }
}