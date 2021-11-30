package com.github.lipinskipawel.board.engine;

import com.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

final class ImmutableBoard<T> implements Board<T> {

    private final LogicalPoints points;
    private final PlayerProvider<T> playerProvider;
    private final MoveHistory moveLog;

    private final static ThreadLocal<Stack<Direction>> stack = new ThreadLocal<>();
    private final static ThreadLocal<List<Move>> allMoves = new ThreadLocal<>();

    ImmutableBoard(final PlayerProvider<T> provider) {
        this.points = new LogicalPoints();
        this.playerProvider = provider.copy();
        this.moveLog = new MoveHistory();
    }

    private ImmutableBoard(final LogicalPoints points,
                           final PlayerProvider<T> provider,
                           final MoveHistory moveHistory) {
        this.points = points;
        this.playerProvider = provider;
        this.moveLog = moveHistory;
    }

    @Override
    public List<Direction> allMoves() {
        return this.moveLog.allDirections();
    }

    @Override
    public List<Move> moveHistory() {
        return this.moveLog.allMoves();
    }

    @Override
    public boolean isMoveAllowed(final Direction destination) {
        return this.points.isAvailable(destination);
    }

    @Override
    public ImmutableBoard<T> executeMove(final Direction destination) {
        if (isMoveAllowed(destination)) {
            final var logicalPoints = this.points.makeAMove(destination);
            final var player = computePlayerToMove(logicalPoints);
            final var moveLogg = this.playerProvider.current().equals(player) ? this.moveLog.add(destination) : this.moveLog.addMove(new Move(List.of(destination)));
            final var providedPlayer = computePlayerProvider(player);

            return new ImmutableBoard<>(logicalPoints, providedPlayer, moveLogg);
        } else {
            return this;
        }
    }

    private PlayerProvider<T> computePlayerProvider(final T player) {
        if (!this.playerProvider.current().equals(player)) {
            return this.playerProvider.copy().swap();
        }
        return this.playerProvider.copy();
    }

    @Override
    public Board<T> executeMove(final Move move) {
        var afterMove = new ImmutableBoard<>(this.points, this.playerProvider, this.moveLog);
        for (var dir : move.getMove()) {
            afterMove = afterMove.executeMove(dir);
        }
        return afterMove;
    }

    @Override
    public int getBallPosition() {
        return this.points.getBallPosition();
    }

    @Override
    public ImmutableBoard<T> undo() {
        final var lastDirection = this.moveLog
                .getLastDirection()
                .orElseThrow(() -> new RuntimeException("There is no move to undo"));
        final var logicalPoints = this.points.undoMove(lastDirection);
        final var moveLogg = this.moveLog.forceUndo();
        final var isFirst = moveLogg.currentPlayer();
        final var providedPlayer = computePlayer(isFirst);

        return new ImmutableBoard<>(logicalPoints, providedPlayer, moveLogg);
    }

    private PlayerProvider<T> computePlayer(final boolean isFirst) {
        if (isFirst) {
            if (this.playerProvider.current().equals(this.playerProvider.second())) {
                return this.playerProvider.copy().swap();
            }
        } else {
            if (this.playerProvider.current().equals(this.playerProvider.first())) {
                return this.playerProvider.copy().swap();
            }
        }
        return this.playerProvider;
    }

    @Override
    public Board<T> undoPlayerMove() {
        final var another = new ImmutableBoard<>(this.points, this.playerProvider.copy(), this.moveLog).undo();
        if (this.playerProvider.current().equals(another.playerProvider.current())) {
            return another;
        } else {
            return this;
        }
    }

    @Override
    public List<Move> allLegalMoves() {

        stack.set(new Stack<>());
        allMoves.set(new ArrayList<>());
        findAllMovesRecursively(this);

        return allMoves.get();
    }

    private void findAllMovesRecursively(final Board<T> board) {

        for (var move : board.getBallAPI().getAllowedDirection()) {

            stack.get().push(move);
            final var afterMove = board.executeMove(move);

            if (isItEnd(afterMove.getBallAPI())) {
                final var moveToSave = new Move(new ArrayList<>(stack.get()));
                allMoves.get().add(moveToSave);
            } else {

                final var afterMove2 = board.executeMove(move);
                findAllMovesRecursively(afterMove2);
            }

            stack.get().pop();
        }
    }

    @Override
    public Point getBallAPI() {
        return this.points.getBall();
    }

    @Override
    public boolean isGoal() {
        return this.points.getBall().isOnTop() || this.points.getBall().isOnBottom();
    }

    private boolean isItEnd(final Point ball) {
        return ball.getAllowedDirection().size() == 7 ||
                ball.getAllowedDirection().size() == 0;
    }

    @Override
    public boolean isGameOver() {
        return isGoal() || points.getBall().getUnavailableDirection().size() == 8;
    }

    @Override
    public Optional<T> takeTheWinner() {
        if (!isGameOver())
            return Optional.empty();
        if (isGoal() && getBallPosition() < 20) {
            return Optional.of(this.playerProvider.first());
        }
        if (!isGoal() && isGameOver()) {
            return Optional.of(getPlayer());
        }
        return Optional.of(this.playerProvider.second());
    }

    @Override
    public Board<T> nextPlayerToMove(final T nextPlayerToMove) throws ChangePlayerIsNotAllowed {
        if (this.moveLog.isSmallMoveHasBeenMade()) {
            throw new ChangePlayerIsNotAllowed();
        }
        if (nextPlayerToMove.equals(this.playerProvider.current())) {
            return this;
        }
        final var newPlayer = computePlayerToMove(this.points);
        final var providedPlayer = this.playerProvider.current().equals(newPlayer)
                ? this.playerProvider
                : this.playerProvider.copy().swap();
        return new ImmutableBoard<>(this.points, providedPlayer, this.moveLog);
    }

    @Override
    public T getPlayer() {
        return this.playerProvider.current();
    }

    @Override
    public PlayerProvider<T> getPlayerProvider() {
        return this.playerProvider.copy();
    }

    @Override
    public int[] transform() {
        return this.points.transform();
    }

    @Override
    public double[] nonBinaryTransformation() {
        return this.points.nonBinaryTransformation();
    }

    private T computePlayerToMove(final LogicalPoints logicalPoints) {
        var player = this.playerProvider.current();

        if (logicalPoints.isOtherPlayerToMove()) {
            return player.equals(this.playerProvider.first()) ? this.playerProvider.second() : this.playerProvider.first();
        }

        return player;
    }
}
