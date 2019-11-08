package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Testing brute force - minimax")
class MiniMaxTest {

    private MoveStrategy bruteForce, bruteForce2;
    private BoardInterface board;

    @BeforeEach
    void setUp() {
        this.bruteForce = new MiniMax();
        this.bruteForce2 = new MiniMax();
        this.board = Boards.immutableBoard();
    }

    @Nested
    @DisplayName("Dummy evaluator")
    class DummyEvaluator {

        @Test
        @RepeatedTest(10)
        void fdggvbnvfgde() {
            final var depth = 2;
            var bo = Boards.immutableBoard();

            while (!bo.isGoal() || bo.allLegalMoves().size() == 0) {
                final Move move = bruteForce.execute(bo, depth, new DummyBoardEvaluator());
                bo = bo.executeMove(move);
                final Move move2 = bruteForce2.execute(bo, depth, new LinearBoardEvaluator());
                bo = bo.executeMove(move2);
                System.out.println(bo.getPlayer());
            }

            Assertions.assertThat(board.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

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
                    () -> Assertions.assertThat(afterAiMove.getPlayer()).isEqualByComparingTo(Player.FIRST)
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
    }
}
