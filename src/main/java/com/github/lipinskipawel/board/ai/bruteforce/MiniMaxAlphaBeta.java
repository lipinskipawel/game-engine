package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;

import java.util.Collections;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class MiniMaxAlphaBeta implements MoveStrategy {

    private final BoardEvaluator defaultEvaluator;
    private final int depth;

    private volatile Move bestMove;
    private boolean cancel;

    public MiniMaxAlphaBeta(final BoardEvaluator defaultEvaluator) {
        this.defaultEvaluator = defaultEvaluator;
        this.depth = 1;
        this.bestMove = Move.emptyMove();
        this.cancel = false;
    }

    @Override
    public Move getEarlyMove(final boolean cancel) {
        this.cancel = cancel;
        return this.bestMove;
    }

    @Override
    public Move execute(final BoardInterface board, final int depth) {
        return execute(board, depth, this.defaultEvaluator);
    }

    @Override
    public Move execute(final BoardInterface board, final int depth, final BoardEvaluator evaluator) {
        final var actualDepth = this.depth == 1 ? depth : this.depth;
        Move bestMove = new Move(Collections.emptyList());

        var highestSeenValue = -Double.MAX_VALUE;
        var lowestSeenValue = Double.MAX_VALUE;
        double currentValue;
        final var allLegalMoves = board.allLegalMoves();
        Collections.shuffle(allLegalMoves);

        for (final Move move : allLegalMoves) {

            if (!cancel) {
                BoardInterface afterMove = board.executeMove(move);

                currentValue = minimax(
                        afterMove,
                        actualDepth - 1,
                        0.0,
                        0.0,
                        board.getPlayer() == Player.FIRST,
                        evaluator
                );

                if (board.getPlayer() == Player.FIRST &&
                        currentValue >= highestSeenValue) {

                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.getPlayer() == Player.SECOND &&
                        currentValue <= lowestSeenValue) {

                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            } else {
                return this.bestMove.equals(Move.emptyMove()) ? this.bestMove : bestMove;
            }
            this.bestMove = bestMove;
        }
        return bestMove;
    }

    private double minimax(final BoardInterface board,
                           final int depth,
                           double alpha,
                           double beta,
                           final boolean maximizingPlayer,
                           final BoardEvaluator evaluator) {
        if (depth <= 0 || board.isGameOver())
            return evaluator.evaluate(board);

        if (!maximizingPlayer) {
            var maxEval = -Double.MAX_VALUE;
            final var allMoves = board.allLegalMoves();
            Collections.shuffle(allMoves);
            for (final var move : allMoves) {
                final var eval = minimax(
                        board.executeMove(move), depth - 1, alpha, beta, true, evaluator);
                maxEval = max(maxEval, eval);
                beta = min(beta, eval);
                if (beta >= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            var minEval = Double.MAX_VALUE;
            final var allMoves = board.allLegalMoves();
            Collections.shuffle(allMoves);
            for (final var move : allMoves) {
                final var eval = minimax(
                        board.executeMove(move), depth - 1, alpha, beta, false, evaluator);
                minEval = min(minEval, eval);
                alpha = max(alpha, eval);
                if (beta >= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}
