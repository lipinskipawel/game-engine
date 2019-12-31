package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;

import java.util.Collections;
import java.util.List;

public final class SmartBoardEvaluator implements BoardEvaluator {

    private static List<Integer> POINTS = List.of(
               60,   60,   60,   60,   60,   60,   60,   60,    60,
            -1000,   50,   50,   50,   50,   50,   50,   50, -1000,
               10,   10,   10,   10,   10,   10,   10,   10,    10,
               20,   20,   20,   20,   20,   20,   20,   20,    20,
               30,   30,   30,   30,   30,   30,   30,   30,    30,
               40,   40,   40,   40,   40,   40,   40,   40,    40,
                0,    0,    0,    0,    0,    0,    0,    0,     0,
               10,   10,   10,   10,   10,   10,   10,   10,    10,
               20,   20,   20,   20,   20,   20,   20,   20,    20,
               30,   30,   30,   30,   30,   30,   30,   30,    30,
               40,   40,   40,   40,   40,   40,   40,   40,    40,
            -1000,  -50,  -50,  -50,  -50,  -50,  -50,  -50, -1000,
              -60,  -60,  -60,  -60,  -60,  -60,  -60,  -60,  -60);

    @Override
    public double evaluate(BoardInterface board) {
        if (!board.isGoal() && board.isGameOver()) {
            return 1000;
        }
        return POINTS.get(board.getBallPosition());
    }
}
