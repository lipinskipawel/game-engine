package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.engine.BoardInterface;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class SmartBoardEvaluator implements BoardEvaluator {

    private static int CONST = 10;
    private static List<Integer> POINTS = IntStream.range(-7, 7)
            .flatMap(i -> IntStream.range(0, 9).map(j -> i * CONST))
            .boxed()
            .collect(toList());

    @Override
    public double evaluate(BoardInterface board) {
        if (board.allLegalMoves().isEmpty()) {
            return -1000;
        }
        return POINTS.get(board.getBallPosition());
    }
}
