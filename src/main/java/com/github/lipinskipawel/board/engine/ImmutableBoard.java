package com.github.lipinskipawel.board.engine;

import com.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static com.github.lipinskipawel.board.engine.Player.SECOND;

final class ImmutableBoard implements Board {

    private final LogicalPoints points;
    private final Player playerToMove;
    private final MoveHistory moveLog;

    private final static ThreadLocal<Stack<Direction>> stack = new ThreadLocal<>();
    private final static ThreadLocal<List<Move>> allMoves = new ThreadLocal<>();

    ImmutableBoard() {
        this.points = new LogicalPoints();
        this.playerToMove = FIRST;
        this.moveLog = new MoveHistory();
    }

    private ImmutableBoard(final LogicalPoints points,
                           final Player currentPlayer,
                           final MoveHistory moveHistory) {
        this.points = points;
        this.playerToMove = currentPlayer;
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
    public ImmutableBoard executeMove(final Direction destination) {

        final var logicalPoints = this.points.makeAMove(destination);
        final var player = computePlayerToMove(logicalPoints);
        final var moveLogg = this.playerToMove == player ? this.moveLog.add(destination) : this.moveLog.addMove(new Move(List.of(destination)));

        return new ImmutableBoard(logicalPoints, player, moveLogg);
    }

    @Override
    public Board executeMove(final Move move) {
        Board afterMove = new ImmutableBoard(this.points, this.playerToMove, this.moveLog);
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
    public ImmutableBoard undo() {
        final var lastDirection = this.moveLog
                .getLastDirection()
                .orElseThrow(() -> new RuntimeException("There is no move to undo"));
        final var logicalPoints = this.points.undoMove(lastDirection);
        final var moveLogg = this.moveLog.forceUndo();
        final var player = moveLogg.currentPlayer();

        return new ImmutableBoard(logicalPoints, player, moveLogg);
    }

    @Override
    public Board undoPlayerMove() {
        final var another = new ImmutableBoard(this.points, this.playerToMove, this.moveLog).undo();
        if (this.playerToMove == another.playerToMove) {
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

    private void findAllMovesRecursively(final Board board) {

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
    public Optional<Player> takeTheWinner() {
        if (!isGameOver())
            return Optional.empty();
        if (isGoal() && getBallPosition() < 20) {
            return Optional.of(FIRST);
        }
        if (!isGoal() && isGameOver()) {
            return Optional.of(getPlayer());
        }
        return Optional.of(SECOND);
    }

    @Override
    public Board nextPlayerToMove(final Player nextPlayerToMove) throws ChangePlayerIsNotAllowed {
        if (this.moveLog.isSmallMoveHasBeenMade()) {
            throw new ChangePlayerIsNotAllowed();
        }
        if (nextPlayerToMove == this.playerToMove) {
            return this;
        }
        final var newPlayer = computePlayerToMove(this.points);
        return new ImmutableBoard(this.points, newPlayer, this.moveLog);
    }

    @Override
    public Player getPlayer() {
        return this.playerToMove;
    }

    @Override
    public int[] transform() {
        return this.points.transform();
    }

    @Override
    public double[] nonBinaryTransformation() {
        return this.points.nonBinaryTransformation();
    }

    private Player computePlayerToMove(final LogicalPoints logicalPoints) {
        var player = this.playerToMove;

        if (logicalPoints.isOtherPlayerToMove())
            player = this.playerToMove.opposite();

        return player;
    }
}
