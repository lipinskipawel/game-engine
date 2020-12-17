package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.Board;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.max;
import static java.lang.Math.min;

final class MiniMaxAlphaBeta implements MoveStrategy {

    private final BoardEvaluator evaluator;
    private final int depth;
    private final int timeout;
    private final AtomicReference<Move> bestMove;
    private volatile boolean cancel;

    MiniMaxAlphaBeta(final BoardEvaluator defaultEvaluator,
                     final int depth,
                     final int timeout) {
        this.evaluator = defaultEvaluator;
        this.depth = depth;
        this.timeout = timeout;
        this.bestMove = new AtomicReference<>(Move.emptyMove());
        this.cancel = false;
    }

    MiniMaxAlphaBeta(final MiniMaxAlphaBeta miniMaxAlphaBeta) {
        this.evaluator = miniMaxAlphaBeta.evaluator;
        this.depth = miniMaxAlphaBeta.depth;
        this.timeout = miniMaxAlphaBeta.timeout;
        this.bestMove = new AtomicReference<>(Move.emptyMove());
        this.cancel = false;
    }

    @Override
    public Move execute(final Board board, final Duration timeout) {
        final var pool = Executors.newSingleThreadExecutor();
        final var copy = new MiniMaxAlphaBeta(this);
        final var searchingForMove = pool.submit(
                () -> copy.execute(board, copy.depth, copy.evaluator)
        );
        try {
            return searchingForMove.get(timeout.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            copy.cancel = true;
            return copy.bestMove.get();
        } finally {
            pool.shutdown();
        }
    }

    @Override
    public Move execute(final Board board, final int depth) {
        return execute(board, depth, this.evaluator);
    }

    @Override
    public Move execute(final Board board, final int depth, final BoardEvaluator evaluator) {
        final var actualDepth = this.depth == 1 ? depth : this.depth;
        Move bestMove = Move.emptyMove();

        var highestSeenValue = -Double.MAX_VALUE;
        var lowestSeenValue = Double.MAX_VALUE;
        double currentValue;
        final var allLegalMoves = board.allLegalMoves();

        for (final Move move : allLegalMoves) {
            setFirstMoveAsBestOnlyIfGlobalBestMoveIsEmpty(move);
            if (this.cancel) {
                return this.bestMove.get();
            }

            final var afterMove = board.executeMove(move);

            currentValue = minimaxWithCancel(
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
                updateGlobalBestMove(bestMove);
            } else if (board.getPlayer() == Player.SECOND &&
                    currentValue <= lowestSeenValue) {

                lowestSeenValue = currentValue;
                bestMove = move;
                updateGlobalBestMove(bestMove);
            }
        }
        return bestMove;
    }

    private void setFirstMoveAsBestOnlyIfGlobalBestMoveIsEmpty(final Move move) {
        if (this.bestMove.get().equals(Move.emptyMove())) {
            this.bestMove.set(move);
        }
    }

    private void updateGlobalBestMove(final Move bestMove) {
        if (!this.cancel) {
            this.bestMove.set(bestMove);
        }
    }

    private double minimaxWithCancel(final Board board,
                                     final int depth,
                                     double alpha,
                                     double beta,
                                     final boolean maximizingPlayer,
                                     final BoardEvaluator evaluator) {
        if (this.cancel) {
            return evaluator.evaluate(board);
        } else {
            return minimax(board, depth, alpha, beta, maximizingPlayer, evaluator);
        }
    }

    private double minimax(final Board board,
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
            for (final var move : allMoves) {
                final var eval = minimaxWithCancel(
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
            for (final var move : allMoves) {
                final var eval = minimaxWithCancel(
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
