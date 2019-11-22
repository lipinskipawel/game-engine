package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Direction;
import com.github.lipinskipawel.board.engine.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Testing brute force - minimax")
class MiniMaxTest {

    private MoveStrategy bruteForce;
    private BoardInterface board;

    @BeforeEach
    void setUp() {
        this.bruteForce = new MiniMax();
        this.board = Boards.immutableBoard();
    }

    @Nested
    @DisplayName("Dummy evaluator")
    class DummyEvaluator {

        @Test
        @DisplayName("Should score the goal at 1 deep level")
        void scoreAGoal() {
            final var after4Moves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N);

            final var bestMove = bruteForce.execute(after4Moves, 1);
            final var afterAiMove = after4Moves.executeMove(bestMove);

            assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
            );
        }

        @Test
        @DisplayName("Should not make suicide move at 1 deep level")
        void notScoreAGoal() {
            final var after4Moves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N);

            final var bestMove = bruteForce.execute(after4Moves, 1);
            final var afterAiMove = after4Moves.executeMove(bestMove);

            assertAll(
                    () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
            );
        }

        @Test
        @DisplayName("SmartBoardEvaluator should beat DummyBoardEvaluator")
        void checkBetterEvaluator() {
            final var dummyEvaluator = new DummyBoardEvaluator();
            final var smartEvaluator = new SmartBoardEvaluator();

            var gameBoard = board;
            BoardEvaluator evaluator = dummyEvaluator;

            while (!gameBoard.isOver()) {
                var move = bruteForce.execute(gameBoard, 1, evaluator);
                gameBoard = gameBoard.executeMove(move);
                evaluator = evaluator == dummyEvaluator ? smartEvaluator : dummyEvaluator;
            }
            final var playerThatWon = board.isGoal()
                    ? board.getPlayer()
                    : board.getPlayer().opposite();

            Assertions.assertThat(playerThatWon).isEqualByComparingTo(Player.SECOND);
        }
    }
}
