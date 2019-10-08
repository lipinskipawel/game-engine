package io.lipinski.board.engine;

import io.lipinski.board.engine.exceptions.IllegalMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO Ideally this class should be package-private
// TODO refactor handling player, this 'if' should be replace somehow in the future
class ImmutableBoard implements BoardInterface {

    private final LogicalPoints points;
    private final Player playerToMove;
    private final MoveHistory moveLog;

    private final static ThreadLocal<Stack<Direction>> stack = new ThreadLocal<>();
    private final static ThreadLocal<List<Move>> allMoves = new ThreadLocal<>();

    ImmutableBoard() {
        this.points = new LogicalPoints();
        this.playerToMove = Player.FIRST;
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

        return new ImmutableBoard(logicalPoints,
                player,
                moveLogg);
    }

    @Override
    public BoardInterface executeMove(final Move move) throws IllegalMoveException {
        BoardInterface afterMove = new ImmutableBoard(this.points, this.playerToMove, this.moveLog);
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
        final var lol = this.moveLog
                .getLastDirection()
                .orElseThrow(() -> new RuntimeException("There is no move to undo"));
        final var logicalPoints = this.points.undoMove(lol);
        final var moveLogg = this.moveLog.forceUndo();
        final var player = moveLogg.currentPlayer();

        return new ImmutableBoard(logicalPoints,
                player,
                moveLogg);
    }

    @Override
    public BoardInterface undoPlayerMove() {
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

    private void findAllMovesRecursively(final BoardInterface boardInterface) {

        for (var move : boardInterface.getBallAPI().getAllowedDirection()) {

            stack.get().push(move);
            final var afterMove = boardInterface.executeMove(move);

            if (isItEnd(afterMove.getBallAPI())) {
                final var moveToSave = new Move(new ArrayList<>(stack.get()));
                allMoves.get().add(moveToSave);
            } else {

                final var afterMove2 = boardInterface.executeMove(move);
                findAllMovesRecursively(afterMove2);
            }

            stack.get().pop();
        }
    }

    @Override
    public Point getBallAPI() {
        return this.points.getBall();
    }

    // TODO need a layer of abstraction
    // TODO some class that will accept BoardInterface2?? and compute the output
    @Override
    public boolean isGoal() {
        return this.points.getBall().getPosition() == 3 ||
                this.points.getBall().getPosition() == 4 ||
                this.points.getBall().getPosition() == 5 ||
                this.points.getBall().getPosition() == 112 ||
                this.points.getBall().getPosition() == 111 ||
                this.points.getBall().getPosition() == 113;
    }

    private boolean isItEnd(final Point ball) {
        return ball.getAllowedDirection().size() == 7 ||
                ball.getAllowedDirection().size() == 0;
    }

    @Override
    public Player getPlayer() {
        return this.moveLog.currentPlayer();
    }

    @Override
    public int[] transform() {
        return this.points.transform();
    }

    private Player computePlayerToMove(final LogicalPoints logicalPoints) {
        var player = this.playerToMove;

        if (logicalPoints.isOtherPlayerToMove())
            player = this.playerToMove.opposite();

        return player;
    }
}
