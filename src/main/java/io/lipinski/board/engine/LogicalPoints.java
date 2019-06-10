package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.ArrayList;
import java.util.List;

class LogicalPoints {

    private final List<Point2> points;
    private final Point2 ballPosition;


    LogicalPoints() {
        this.points = PointUtils.initialPoints();
        this.ballPosition = points.get(58);
    }

    private LogicalPoints(final List<Point2> points,
                  final Point2 ballPosition) {
        this.points = points;
        this.ballPosition = points.get(ballPosition.getPosition());
    }

    LogicalPoints undoMove(final Direction direction) throws IllegalUndoMoveException {

        final var afterMovePosition = computeBallPosition(direction.opposite());

        final var afterMovePoints = new ArrayList<>(points);

        afterMovePoints.set(this.ballPosition.getPosition(),
                new Point2(this.ballPosition.getPosition()));

        final var previousPositionPoint = afterMovePoints.get(afterMovePosition);
        previousPositionPoint.setAvailableDirections(direction);
        afterMovePoints.set(afterMovePosition, previousPositionPoint);

        return new LogicalPoints(afterMovePoints, previousPositionPoint);
    }

    LogicalPoints makeAMove(final Direction destination) {

        if (isAvailable(destination)) {

            final var newPosition = computeBallPosition(destination);
            final var currentBall = new Point2(points.get(getBallPosition())).notAvailableDirection(destination);

            final var afterMove = new ArrayList<>(points);
            afterMove.set(getBallPosition(), currentBall);

            afterMove.set(newPosition, afterMove.get(newPosition).notAvailableDirection(destination.opposite()));
            return new LogicalPoints(afterMove, afterMove.get(newPosition));
        }
        throw new IllegalMoveException("Can't make a move " + destination.toString());
    }

    private int computeBallPosition(final Direction destination) {
        int moveBall = destination.changeToInt();
        return this.ballPosition.getPosition() + moveBall;
    }

    /**
     * In case were is need to compute next player to move,
     * during sub undoMove stage, need to pass previous LogicalPoints
     * @return
     */
    boolean isOtherPlayerToMove() {
        return ballPosition.getUnavailableDirection().size() == 1;
    }

    Point2 getBall() {
        return new Point2(ballPosition);
    }

    boolean isAvailable(Direction direction) {
        return this.ballPosition.isAvailable(direction);
    }

    int getBallPosition() {
        return this.ballPosition.getPosition();
    }
}
