package com.github.lipinskipawel.board.engine;

import com.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class ImmutableBoard<T> implements Board<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImmutableBoard.class);
    private final LogicalPoints points;
    private final PlayerProvider<T> playerProvider;
    private final MoveHistory moveLog;

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
            LOGGER.trace("executeMove: {}", destination);
            final var logicalPoints = this.points.makeAMove(destination);
            final var player = computePlayerToMove(logicalPoints);
            final var moveLogg = this.playerProvider.current().equals(player) ? this.moveLog.add(destination) : this.moveLog.addMove(new Move(List.of(destination)));
            final var providedPlayer = computePlayerProvider(player);

            LOGGER.debug("Move has been made: {}", destination);
            return new ImmutableBoard<>(logicalPoints, providedPlayer, moveLogg);
        } else {
            LOGGER.debug("Move has NOT been made: {}", destination);
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
        LOGGER.trace("undo executes");
        final var lastDirection = this.moveLog
                .getLastDirection()
                .orElseThrow(() -> new RuntimeException("There is no move to undo"));
        final var logicalPoints = this.points.undoMove(lastDirection);
        final var moveLogg = this.moveLog.forceUndo();
        final var isFirst = moveLogg.currentPlayer();
        final var providedPlayer = computePlayer(isFirst);

        final var newImmutableBoard = new ImmutableBoard<T>(logicalPoints, providedPlayer, moveLogg);
        if (this.equals(newImmutableBoard)) {
            LOGGER.debug("undo returned THIS reference.");
        }
        return newImmutableBoard;
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
            LOGGER.debug("undoPlayerMove has been made.");
            return another;
        } else {
            LOGGER.debug("undoPlayerMove has returned THIS reference.");
            return this;
        }
    }

    @Override
    public List<Move> allLegalMoves() {
        LOGGER.debug("allLegalMoves executed.");
        final var legalMovesFuture = new LegalMovesFuture(this);
        legalMovesFuture.start(Duration.ofSeconds(Integer.MAX_VALUE));
        final List<Move> result = new ArrayList<>();
        while (legalMovesFuture.isRunning()) {
            result.addAll(legalMovesFuture.partialResult());
        }
        result.addAll(legalMovesFuture.partialResult());
        LOGGER.debug("allLegalMoves finds: {} moves", result.size());
        return result;
    }

    @Override
    public LegalMovesFuture allLegalMovesFuture() {
        return new LegalMovesFuture(this);
    }

    @Override
    public Point getBallAPI() {
        return this.points.getBall();
    }

    @Override
    public boolean isGoal() {
        return this.points.getBall().isOnTop() || this.points.getBall().isOnBottom();
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
            LOGGER.debug("{} is the same as current player to move {}. Returning THIS reference.",
                    nextPlayerToMove, this.playerProvider.current());
            return this;
        }
        final var newPlayer = computePlayerToMove(this.points);
        final var providedPlayer = this.playerProvider.current().equals(newPlayer)
                ? this.playerProvider
                : this.playerProvider.copy().swap();
        LOGGER.debug("nextPlayerToMove returns board with player to move {}.", providedPlayer.current());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableBoard<?> that = (ImmutableBoard<?>) o;
        return Objects.equals(points, that.points) &&
                Objects.equals(playerProvider, that.playerProvider) &&
                Objects.equals(moveLog, that.moveLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, playerProvider, moveLog);
    }

    @Override
    public String toString() {
        return "ImmutableBoard{" +
                "points=" + points +
                ", playerProvider=" + playerProvider +
                ", moveLog=" + moveLog +
                '}';
    }
}
