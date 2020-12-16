package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;

public final class DefaultMoveStrategyBuilder {
    private BoardEvaluator defaultEvaluator;
    private int depth;
    private int timeout;

    private DefaultMoveStrategyBuilder() {
    }

    public static DefaultMoveStrategyBuilder aDefaultMoveStrategyBuilder() {
        return new DefaultMoveStrategyBuilder();
    }

    public DefaultMoveStrategyBuilder withBoardEvaluator(final BoardEvaluator evaluator) {
        this.defaultEvaluator = evaluator;
        return this;
    }

    public DefaultMoveStrategyBuilder withDepth(final int depth) {
        this.depth = depth;
        return this;
    }

    public DefaultMoveStrategyBuilder withTimeoutInSeconds(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    public MiniMaxAlphaBeta build() {
        return new MiniMaxAlphaBeta(defaultEvaluator, depth, timeout);
    }
}
