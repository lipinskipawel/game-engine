package com.github.lipinskipawel.board.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

final class LogicalPoints implements Transformation {

    private final List<Point> points;
    private final Point ballPosition;


    LogicalPoints() {
        this.points = PointUtils.initialPoints();
        this.ballPosition = points.get(58);
    }

    private LogicalPoints(final List<Point> points,
                          final Point ballPosition) {
        this.points = points;
        this.ballPosition = points.get(ballPosition.getPosition());
    }

    LogicalPoints undoMove(final Direction direction) {

        final var newBallPosition = computeBallPosition(direction.opposite());
        final var fakeNewMovePoints = new ArrayList<>(points);

        final var newPositionPoint = points.get(this.ballPosition.getPosition());
        newPositionPoint.setAvailableDirections(direction.opposite());
        fakeNewMovePoints.set(this.ballPosition.getPosition(), newPositionPoint);

        final var originalPositionPoint = fakeNewMovePoints.get(newBallPosition);
        originalPositionPoint.setAvailableDirections(direction);
        fakeNewMovePoints.set(newBallPosition, originalPositionPoint);

        return new LogicalPoints(fakeNewMovePoints, originalPositionPoint);
    }

    LogicalPoints makeAMove(final Direction destination) {

        if (isAvailable(destination)) {

            final var newPosition = computeBallPosition(destination);
            final var currentBall = new Point(points.get(getBallPosition())).notAvailableDirection(destination);

            final var afterMove = new ArrayList<>(points);
            afterMove.set(getBallPosition(), currentBall);

            afterMove.set(newPosition, afterMove.get(newPosition).notAvailableDirection(destination.opposite()));
            return new LogicalPoints(afterMove, afterMove.get(newPosition));
        }
        throw new RuntimeException("Can't make a move " + destination.toString());
    }

    private int computeBallPosition(final Direction destination) {
        int moveBall = destination.changeToInt();
        return this.ballPosition.getPosition() + moveBall;
    }

    /**
     * In case were is need to compute next player to move,
     * during sub undoMove stage, need to pass previous LogicalPoints
     *
     * @return
     */
    boolean isOtherPlayerToMove() {
        return ballPosition.isOnTop() || ballPosition.isOnBottom() ||
                ballPosition.getUnavailableDirection().size() == 1 ||
                ballPosition.getAllowedDirection().size() == 8;
    }

    Point getBall() {
        return new Point(ballPosition);
    }

    boolean isAvailable(Direction direction) {
        return this.ballPosition.isAvailable(direction);
    }

    int getBallPosition() {
        return this.ballPosition.getPosition();
    }

    /**
     * Contract of this method is preserved in the TransformationTest class.
     *
     * @return Array of 1 and 0 in sequence for every Point in.
     * Points are sorted based on Position in ascending order.
     */
    @Override
    public int[] transform() {
        return points
                .stream()
                .sorted(Comparator.comparingInt(Point::getPosition))
                .map(Point::getAllDirections)
                .flatMap(Collection::stream)
                .mapToInt(x -> x ? 1 : 0)
                .toArray();
    }
}
