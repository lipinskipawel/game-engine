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

    @Test
    @DisplayName("SmartBoardEvaluator should beat DummyBoardEvaluator")
    void checkBetterEvaluator() {
        final var dummyEvaluator = new DummyBoardEvaluator();
        final var smartEvaluator = new SmartBoardEvaluator();

        var gameBoard = board;
        BoardEvaluator evaluator = dummyEvaluator;

        while (!gameBoard.isGameOver()) {
            var move = bruteForce.execute(gameBoard, 1, evaluator);
            gameBoard = gameBoard.executeMove(move);
            evaluator = evaluator == dummyEvaluator ? smartEvaluator : dummyEvaluator;
        }
        final var playerThatWon = board.isGoal()
                ? board.getPlayer()
                : board.getPlayer().opposite();

        Assertions.assertThat(playerThatWon).isEqualByComparingTo(Player.SECOND);
    }

    @Nested
    @DisplayName("Dummy evaluator")
    class DummyEvaluator {

        @Nested
        @DisplayName("Upper goal")
        class UpperGoal {

            @Test
            @DisplayName("Should score the goal when player FIRST")
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
            @DisplayName("Should NOT score the goal when player FIRST")
            void shouldNotMakeSuicideMove() {
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
            @DisplayName("Should score the goal when player FIRST, depth 2")
            void scoreAGoalDepth2() {
                final var after4Moves = board
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N);

                final var bestMove = bruteForce.execute(after4Moves, 2);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should NOT score the goal when player FIRST, depth 2")
            void shouldNotMakeSuicideMoveDepth2() {
                final var after4Moves = board
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N);

                final var bestMove = bruteForce.execute(after4Moves, 2);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 3")
            void scoreAGoalDepth3() {
                final var after4Moves = board
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should NOT score the goal when player FIRST, depth 3")
            void shouldNotMakeSuicideMoveDepth3() {
                final var after4Moves = board
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N)
                        .executeMove(Direction.N);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
                );
            }
        }

        @Nested
        @DisplayName("Bottom goal")
        class BottomGoal {

            @Test
            @DisplayName("Should score a goal when player SECOND")
            void scoreAGoalInSecondDay() {
                final var after4Moves = board
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S);

                final var bestMove = bruteForce.execute(after4Moves, 1);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST")
            void shouldNotMakeSuicideMove() {
                final var after4Moves = board
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S);

                final var bestMove = bruteForce.execute(after4Moves, 1);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should score a goal when player SECOND, depth 2")
            void scoreAGoalInSecondDayDepth2() {
                final var after4Moves = board
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S);

                final var bestMove = bruteForce.execute(after4Moves, 2);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 2")
            void shouldNotMakeSuicideMoveDepth2() {
                final var after4Moves = board
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S);

                final var bestMove = bruteForce.execute(after4Moves, 2);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }

            @Test
            @DisplayName("Should score a goal when player SECOND, depth 3")
            void scoreAGoalInSecondDayDepth3() {
                final var after4Moves = board
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 3")
            void shouldNotMakeSuicideMoveDepth3() {
                final var after4Moves = board
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S)
                        .executeMove(Direction.S);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }
        }
    }
}
