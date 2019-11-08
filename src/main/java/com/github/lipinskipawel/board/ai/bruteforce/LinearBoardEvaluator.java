package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;

final class LinearBoardEvaluator implements BoardEvaluator {

    @Override
    public double evaluate(final BoardInterface board) {
        if (board.getBallPosition() == 3 ||
                board.getBallPosition() == 4 ||
                board.getBallPosition() == 5)
            return 150;
        else if (board.getBallPosition() == 111 ||
                board.getBallPosition() == 112 ||
                board.getBallPosition() == 113)
            return -150;

       return (double) (90 - ((((board.getBallPosition() - 1)) / 9) * 10));
    }
}
