package io.lipinski.board.engine;

import io.lipinski.board.engine.exceptions.IllegalMoveException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

// TODO Ideally this class should be package-private
// TODO refactor handling player, this 'if' should be replace somehow in the future
class ImmutableBoard implements BoardInterface {

    private final LogicalPoints points;
    private final MoveHistory moveHistory;
    private final Player playerToMove;
    private final PlayerMoveLog moveLog;
    private final List<Direction> intermediateMoves;

    private final static ThreadLocal<Stack<Direction>> stack = new ThreadLocal<>();
    private final static ThreadLocal<List<Move>> allMoves = new ThreadLocal<>();

    ImmutableBoard() {
        this.points = new LogicalPoints();
        this.playerToMove = Player.FIRST;
        this.moveHistory = new MoveHistory();
        this.moveLog = new PlayerMoveLog();
        this.intermediateMoves = new ArrayList<>();
    }

    private ImmutableBoard(final LogicalPoints points,
                           final Player currentPlayer,
                           final MoveHistory moveHistory,
                           final PlayerMoveLog playerMoveLog,
                           final List<Direction> intermediateMoves) {
        this.points = points;
        this.playerToMove = currentPlayer;
        this.moveHistory = moveHistory;
        this.moveLog = playerMoveLog;
        this.intermediateMoves = intermediateMoves;
    }

    @Override
    public List<Direction> allMoves() {
        return this.moveHistory.allMoves();
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
        final var newMoveHistory = this.moveHistory.add(destination);
        final var player = computePlayerToMove(logicalPoints);
        final var moveLogg = this.playerToMove == player ? this.moveLog : addMove(destination);
        final var interMoves = this.playerToMove == player ? add(destination) : new ArrayList<Direction>();

        return new ImmutableBoard(logicalPoints,
                player,
                newMoveHistory,
                moveLogg,
                interMoves);
    }

    @Override
    public BoardInterface executeMove(final Move move) throws IllegalMoveException {
        BoardInterface afterMove = this;
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
        final var logicalPoints = this.points.undoMove(this.moveHistory.getLastMove());
        final var newMoveHistory = this.moveHistory.subtract();
        final var player = computePlayerToMove(logicalPoints);
        final var moveLogg = this.playerToMove == player ? this.moveLog : this.moveLog.undoMove();
        final var interMove = this.playerToMove == player ? remove() : new ArrayList<Direction>();

        return new ImmutableBoard(logicalPoints,
                player,
                newMoveHistory,
                moveLogg,
                interMove);
    }

    @Override
    public BoardInterface undoPlayerMove() {
        return null;
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
        if (this.intermediateMoves.size() > 0)
            return this.playerToMove;
        var player = this.playerToMove;

        if (logicalPoints.isOtherPlayerToMove())
            player = this.playerToMove.opposite();

        return player;
    }

    private PlayerMoveLog addMove(final Direction destination) {
        final var list = new ArrayList<>(this.intermediateMoves);
        list.add(destination);
        return this.moveLog.addMove(new Move(list));
    }

    private ArrayList<Direction> remove() {
        if (this.intermediateMoves.size() == 0)
            return new ArrayList<>();
        final var moves = new ArrayList<>(this.intermediateMoves);
        moves.remove(moves.size() - 1);
        return moves;
    }

    private List<Direction> add(final Direction destination) {
        final var moves = new ArrayList<>(this.intermediateMoves);
        moves.add(destination);
        return moves;
    }
}
