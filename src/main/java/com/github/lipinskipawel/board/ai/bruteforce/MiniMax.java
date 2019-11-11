package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;

import java.util.Collections;

public final class MiniMax implements MoveStrategy {

    @Override
    public Move execute(final BoardInterface board,
                        final int depth) {
        return execute(board, depth, new DummyBoardEvaluator());
    }

    @Override
    public Move execute(final BoardInterface board,
                        final int depth,
                        final BoardEvaluator evaluator) {
        Move bestMove = new Move(Collections.emptyList());

        var highestSeenValue = -Double.MAX_VALUE;
        var lowestSeenValue = Double.MAX_VALUE;
        double currentValue;

        for (final Move move : board.allLegalMoves()) {

            BoardInterface afterMove = board.executeMove(move);

            currentValue = board.getPlayer() == Player.FIRST ? // here is CURRENT board
                    min(afterMove, depth - 1, evaluator) : // here is AFTER board
                    max(afterMove, depth - 1, evaluator); // here is AFTER board


            if (board.getPlayer() == Player.FIRST &&
                    currentValue >= highestSeenValue) {

                highestSeenValue = currentValue;
                bestMove = move;
            } else if (board.getPlayer() == Player.SECOND &&
                    currentValue <= lowestSeenValue) {

                lowestSeenValue = currentValue;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private double min(final BoardInterface board,
                    final int depth,
                    final BoardEvaluator evaluator) {
        if (depth <= 0)
            return evaluator.evaluate(board);

        var lowestSeenValue = Double.MAX_VALUE;

        for (Move move : board.allLegalMoves()) {
            BoardInterface afterMove = board.executeMove(move);
            double currentValue = max(afterMove, depth - 1, evaluator);
            if (currentValue <= lowestSeenValue)
                lowestSeenValue = currentValue;
        }
        return lowestSeenValue;
    }

    private double max(final BoardInterface board,
                    final int depth,
                    final BoardEvaluator evaluator) {
        if (depth <= 0)
            return evaluator.evaluate(board);

        var highestSeenValue = -Double.MAX_VALUE;

        for (Move move : board.allLegalMoves()) {
            BoardInterface afterMove = board.executeMove(move);
            double currentValue = min(afterMove, depth - 1, evaluator);
            if (currentValue >= highestSeenValue)
                highestSeenValue = currentValue;
        }
        return highestSeenValue;
    }
}
