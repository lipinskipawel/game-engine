package com.github.lipinskipawel.board.ai.bruteforce;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class MiniMax implements MoveStrategy {

    private final BoardEvaluator evaluator;
    private final int depth;

    public MiniMax() {
        this.evaluator = new DummyBoardEvaluator();
        this.depth = 1;
    }

    public MiniMax(final BoardEvaluator evaluator) {
        this.evaluator = evaluator;
        this.depth = 1;
    }

    public MiniMax(final BoardEvaluator evaluator, final int depth) {
        this.evaluator = evaluator;
        this.depth = depth;
    }

    @Override
    public Move execute(final BoardInterface board, final Duration timeout) {
        final var pool = Executors.newSingleThreadExecutor();
        final var searchingForMove = pool.submit(() -> execute(board, this.depth, this.evaluator));
        try {
            return searchingForMove.get(timeout.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return board.allLegalMoves().get(0);
        } finally {
            pool.shutdown();
        }
    }

    @Override
    public Move execute(final BoardInterface board,
                        final int depth) {
        return execute(board, depth, this.evaluator);
    }

    @Override
    public Move execute(final BoardInterface board,
                        final int depth,
                        final BoardEvaluator evaluator) {
        final var actualDepth = this.depth == 1 ? depth : this.depth;
        Move bestMove = new Move(Collections.emptyList());

        var highestSeenValue = -Double.MAX_VALUE;
        var lowestSeenValue = Double.MAX_VALUE;
        double currentValue;
        final var allLegalMoves = board.allLegalMoves();
        Collections.shuffle(allLegalMoves);

        for (final Move move : allLegalMoves) {

            BoardInterface afterMove = board.executeMove(move);

            currentValue = board.getPlayer() == Player.FIRST ? // here is CURRENT board
                    min(afterMove, actualDepth - 1, evaluator) : // here is AFTER board
                    max(afterMove, actualDepth - 1, evaluator); // here is AFTER board

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
        if (depth <= 0 || board.isGameOver())
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
        if (depth <= 0 || board.isGameOver())
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
