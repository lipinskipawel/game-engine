package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.lipinskipawel.board.engine.Direction.N;
import static com.github.lipinskipawel.board.engine.Direction.NE;
import static com.github.lipinskipawel.board.engine.Direction.NW;
import static com.github.lipinskipawel.board.engine.Direction.S;
import static com.github.lipinskipawel.board.engine.Direction.SE;
import static com.github.lipinskipawel.board.engine.Direction.W;
import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static com.github.lipinskipawel.board.engine.Player.SECOND;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("API -- Minimax alpha-beta")
class MiniMaxAlphaBetaTest {

    private MoveStrategy bruteForce;
    private BoardInterface board;

    @BeforeEach
    void setUp() {
        this.bruteForce = new MiniMaxAlphaBeta(new DummyBoardEvaluator());
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

        final var winner = computeWinner(gameBoard);
        Assertions.assertThat(winner).isEqualByComparingTo(Player.SECOND);
    }

    private Player computeWinner(final BoardInterface gameBoard) {
        if (gameBoard.isGoal() && gameBoard.getBallPosition() < 20) {
            return FIRST;
        } else if (gameBoard.isGoal() && gameBoard.getBallPosition() > 50) {
            return SECOND;
        }
        return gameBoard.getPlayer();
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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 1);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 2")
            void scoreAGoalDepth2() {
                final var after4Moves = board
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 2);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 3")
            void scoreAGoalDepth3() {
                final var after4Moves = board
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
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
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 1);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST")
            void shouldNotMakeSuicideMove() {
                final var after4Moves = board
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

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
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 2);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 2")
            void shouldNotMakeSuicideMoveDepth2() {
                final var after4Moves = board
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

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
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 3")
            void shouldNotMakeSuicideMoveDepth3() {
                final var after4Moves = board
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 3);
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }
        }

        @Nested
        @DisplayName("Close corner")
        class CornerTest {

            @Test
            @DisplayName("should not hit the corner, depth 1")
            void rightUpperCorner() {
                final var afterMoves = board
                        .executeMove(NE)
                        .executeMove(S)
                        .executeMove(NE)
                        .executeMove(NE)
                        .executeMove(NW)
                        .executeMove(NE)
                        .executeMove(N)
                        .executeMove(SE)
                        .executeMove(W)
                        .executeMove(W);

                final var best = bruteForce.execute(afterMoves, 1);
                final var afterAi = afterMoves.executeMove(best);

                Assertions.assertThat(afterAi.isGameOver()).isFalse();
            }

            @Test
            @DisplayName("should not hit the corner, depth 2")
            void rightUpperCorner2() {
                final var afterMoves = board
                        .executeMove(NE)
                        .executeMove(S)
                        .executeMove(NE)
                        .executeMove(NE)
                        .executeMove(NW)
                        .executeMove(NE)
                        .executeMove(N)
                        .executeMove(SE)
                        .executeMove(W)
                        .executeMove(W);

                final var best = bruteForce.execute(afterMoves, 2);
                final var afterAi = afterMoves.executeMove(best);

                Assertions.assertThat(afterAi.isGameOver()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("Smart evaluator")
    class SmartEvaluator {

        @Nested
        @DisplayName("Upper goal")
        class UpperGoal {

            @Test
            @DisplayName("Should score the goal when player FIRST")
            void scoreAGoal() {
                final var after4Moves = board
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 1, new SmartBoardEvaluator());
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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 1, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 2")
            void scoreAGoalDepth2() {
                final var after4Moves = board
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 2, new SmartBoardEvaluator());
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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 2, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should score the goal when player FIRST, depth 3")
            void scoreAGoalDepth3() {
                final var after4Moves = board
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 3, new SmartBoardEvaluator());
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
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N)
                        .executeMove(N);

                final var bestMove = bruteForce.execute(after4Moves, 3, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
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
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 1, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST")
            void shouldNotMakeSuicideMove() {
                final var after4Moves = board
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 1, new SmartBoardEvaluator());
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
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 2, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 2")
            void shouldNotMakeSuicideMoveDepth2() {
                final var after4Moves = board
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 2, new SmartBoardEvaluator());
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
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 3, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isTrue(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(FIRST)
                );
            }

            @Test
            @DisplayName("Should NOT score a goal when player FIRST, depth 3")
            void shouldNotMakeSuicideMoveDepth3() {
                final var after4Moves = board
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S)
                        .executeMove(S);

                final var bestMove = bruteForce.execute(after4Moves, 3, new SmartBoardEvaluator());
                final var afterAiMove = after4Moves.executeMove(bestMove);

                assertAll(
                        () -> Assertions.assertThat(afterAiMove.isGoal()).isFalse(),
                        () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.SECOND)
                );
            }
        }

        @Nested
        @DisplayName("Close corner")
        class CornerTest {

            @Test
            @DisplayName("should not hit the corner, depth 1")
            void rightUpperCorner() {
                final var afterMoves = board
                        .executeMove(NE)
                        .executeMove(S)
                        .executeMove(NE)
                        .executeMove(NE)
                        .executeMove(NW)
                        .executeMove(NE)
                        .executeMove(N)
                        .executeMove(SE)
                        .executeMove(W)
                        .executeMove(W);

                final var best = bruteForce.execute(afterMoves, 1, new SmartBoardEvaluator());
                final var afterAi = afterMoves.executeMove(best);

                Assertions.assertThat(afterAi.isGameOver()).isFalse();
            }

            @Test
            @DisplayName("should not hit the corner, depth 2")
            void rightUpperCorner2() {
                final var afterMoves = board
                        .executeMove(NE)
                        .executeMove(S)
                        .executeMove(NE)
                        .executeMove(NE)
                        .executeMove(NW)
                        .executeMove(NE)
                        .executeMove(N)
                        .executeMove(SE)
                        .executeMove(W)
                        .executeMove(W);

                final var best = bruteForce.execute(afterMoves, 2, new SmartBoardEvaluator());
                final var afterAi = afterMoves.executeMove(best);

                Assertions.assertThat(afterAi.isGameOver()).isFalse();
            }
        }
    }
}
