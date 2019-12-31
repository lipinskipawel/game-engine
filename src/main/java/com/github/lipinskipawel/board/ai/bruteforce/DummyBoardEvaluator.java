package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;

public final class DummyBoardEvaluator implements BoardEvaluator {

    @Override
    public double evaluate(final BoardInterface board) {
        if (!board.isGoal() && board.isGameOver()) {
            return 1000;
        }
        if (board.getBallPosition() == 3 ||
                board.getBallPosition() == 4 ||
                board.getBallPosition() == 5)
            return 300;
        else if (board.getBallPosition() == 111 ||
                board.getBallPosition() == 112 ||
                board.getBallPosition() == 113)
            return -300;
        return 50;
    }
}
