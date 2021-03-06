package com.github.lipinskipawel.board.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparingInt;

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
                ballPosition.getAllowedDirection().size() == 8 ||
                ballPosition.getAllowedDirection().isEmpty();
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
                .sorted(comparingInt(Point::getPosition))
                .map(Point::getAllDirections)
                .flatMap(List::stream)
                .mapToInt(x -> x ? 1 : 0)
                .toArray();
    }

    @Override
    public double[] nonBinaryTransformation() {
        return points
                .stream()
                .sorted(comparingInt(Point::getPosition))
                .map(LogicalPoints::nonBinary)
                .flatMapToDouble(Arrays::stream)
                .toArray();
    }

    /**
     * This method convert {@link Point} into double[].
     * Each direction has it's mapping. List of mappings:
     * {@link Direction#N}  to 0.1
     * {@link Direction#NE} to 0.2
     * {@link Direction#E}  to 0.3
     * {@link Direction#SE} to 0.4
     * {@link Direction#S}  to 0.5
     * {@link Direction#SW} to 0.6
     * {@link Direction#W}  to 0.7
     * {@link Direction#NW} to 0.8
     * In case of false in any particular direction then 0.9 is provided.
     *
     * @param point is the object to convert
     * @return double[] after conversion of {@link Point}
     */
    static double[] nonBinary(final Point point) {
        final var directions = point.getAllDirections();
        final var doubles = new double[8];
        doubles[0] = directions.get(0) ? 0.1 : 0.9;
        doubles[1] = directions.get(1) ? 0.2 : 0.9;
        doubles[2] = directions.get(2) ? 0.3 : 0.9;
        doubles[3] = directions.get(3) ? 0.4 : 0.9;
        doubles[4] = directions.get(4) ? 0.5 : 0.9;
        doubles[5] = directions.get(5) ? 0.6 : 0.9;
        doubles[6] = directions.get(6) ? 0.7 : 0.9;
        doubles[7] = directions.get(7) ? 0.8 : 0.9;
        return doubles;
    }
}
