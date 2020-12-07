package com.github.lipinskipawel.board.ai;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;

import java.time.Duration;

/**
 * The underlying implementations does NOT guarantee thread safety.
 * Abstraction layer for different implementations of artificial intelligence.
 */
public interface MoveStrategy {

    /**
     * Provides default {@link BoardEvaluator}
     *
     * @param board best move will be find on that board
     * @param depth of the search
     * @return the best move that can be find using {@link BoardEvaluator}
     */
    Move execute(BoardInterface board, int depth);

    /**
     * @param board best move will be find on that board
     * @param depth of the search
     * @return the best move that can be find using {@link BoardEvaluator}
     */
    Move execute(BoardInterface board, int depth, BoardEvaluator evaluator);

    /**
     * This method will guarantee that the {@link Move} will be returned within given timeout.
     *
     * @param board best move will be find on that move
     * @param timeout in seconds
     * @return best move
     */
    Move execute(BoardInterface board, Duration timeout);
}
