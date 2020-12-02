package com.github.lipinskipawel.board.ai;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;

/**
 * Abstraction layer for different implementations of artificial intelligence.
 */
public interface MoveStrategy {

    /**
     * Provides default {@link BoardEvaluator}
     *
     * @param board best move will be finding on that board
     * @param depth of the search
     * @return the best move that can be find using {@link BoardEvaluator}
     */
    Move execute(BoardInterface board, int depth);

    /**
     * @param board best move will be finding on that board
     * @param depth of the search
     * @return the best move that can be find using {@link BoardEvaluator}
     */
    Move execute(BoardInterface board, int depth, BoardEvaluator evaluator);

    /**
     * @param cancel
     * @return
     */
    Move getEarlyMove(final boolean cancel);
}
