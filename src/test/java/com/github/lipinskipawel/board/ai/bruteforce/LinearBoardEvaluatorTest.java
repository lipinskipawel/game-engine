package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

@DisplayName("Testing linear board evaluator")
class LinearBoardEvaluatorTest {

    private BoardEvaluator evaluator;
    private BoardInterface board;

    @BeforeEach
    void setUp() {
        this.evaluator = new LinearBoardEvaluator();
        this.board = Boards.immutableBoard();
    }

    @Nested
    @DisplayName("Linear evaluator")
    class LinearEvaluator {

        @Test
        @DisplayName("Should have linear evaluations")
        void evaluateBoard() {
            var before = evaluator.evaluate(board);
            board = board.executeMove(Direction.N).executeMove(Direction.N);
            var afterMove = evaluator.evaluate(board);

            Assertions.assertThat(before).isGreaterThan(afterMove);
        }
    }

}
