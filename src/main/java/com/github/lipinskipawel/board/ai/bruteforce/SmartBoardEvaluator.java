package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;

import java.util.List;

final class SmartBoardEvaluator implements BoardEvaluator {

    private static List<Integer> POINTS = List.of(
            -60, -60, -60, -60, -60, -60, -60, -60, -60,
            -50, -50, -50, -50, -50, -50, -50, -50, -50,
            -40, -40, -40, -40, -40, -40, -40, -40, -40,
            -30, -30, -30, -30, -30, -30, -30, -30, -30,
            -20, -20, -20, -20, -20, -20, -20, -20, -20,
            -10, -10, -10, -10, -10, -10, -10, -10, -10,
              0,   0,   0,   0,   0,   0,   0,   0,   0,
             10,  10,  10,  10,  10,  10,  10,  10,  10,
             20,  20,  20,  20,  20,  20,  20,  20,  20,
             30,  30,  30,  30,  30,  30,  30,  30,  30,
             40,  40,  40,  40,  40,  40,  40,  40,  40,
             50,  50,  50,  50,  50,  50,  50,  50,  50,
             60,  60,  60,  60,  60,  60,  60,  60,  60);

    @Override
    public double evaluate(BoardInterface board) {
        if (board.allLegalMoves().isEmpty()) {
            return -1000;
        }
        return POINTS.get(board.getBallPosition());
    }
}
