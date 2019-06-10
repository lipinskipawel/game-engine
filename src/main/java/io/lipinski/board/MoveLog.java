package io.lipinski.board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread unsafe class.
 */
class MoveLog implements Serializable {

    private final List<List<Integer>> listOfMoves;
    private List<Integer> currentMove;
    private final Points points;
    /**
     * This flag is used to handle with currentPlayer from MutableBoard
     */
    private boolean newPlayer;


    MoveLog() {
        this.listOfMoves = new ArrayList<>();
        this.currentMove = new ArrayList<>();
        this.currentMove.add(58);
        this.listOfMoves.add(this.currentMove);
        this.points = new Points();
        this.newPlayer = true;
    }


    boolean addMove(final Point src, final Point dest) {
        return addMoveHelper(src, dest);
    }
    boolean undoMove(final boolean doYouWantToUndoAWholeMove) {
        return undoMoveHelper(doYouWantToUndoAWholeMove);
    }
    Point getBall() {return this.points.ballPosition;}
    List<List<Integer>> getListOfMoves() {return this.listOfMoves;}
    Point getPoint(int position) {
        return this.points.getPoint(position);
    }
    boolean isNewPlayerRequire() {return this.newPlayer;}
    boolean isThisGoal(final int position) {return this.points.isThisGoal(position);}
    Player winnerIs(final int goalCandidate) {
        return this.points.winnerIs(goalCandidate);
    }

    Direction[] getAvailableDirection() {
        return this.points.ballPosition.getAvailableDirection();
    }



    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (List<Integer> temp : this.listOfMoves) {
            for (Integer value : temp) {
                stringBuilder.append(value).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private boolean addMoveHelper(final Point src, final Point dest) {
        if (this.points.isLegal(src, dest)) {
            if (!dest.isCanMoveAgain()) {
                // handle 'old' move
                this.listOfMoves.remove(this.listOfMoves.size() - 1);
                this.currentMove.add(dest.getPosition());
                this.listOfMoves.add(this.currentMove);
                // create new move with initial state
                this.currentMove = new ArrayList<>();
                this.currentMove.add(dest.getPosition());
                this.listOfMoves.add(this.currentMove);
                this.points.makeMove(src, dest);
                this.newPlayer = true;
                return true;
            } else {
                this.listOfMoves.remove(this.listOfMoves.size() - 1);
                this.currentMove.add(dest.getPosition());
                this.listOfMoves.add(this.currentMove);
                this.points.makeMove(src, dest);
                this.newPlayer = false;
                return true;
            }
        }
        return false;
    }
    private boolean undoMoveHelper(final boolean doYouWantToUndoAWholeMove) {
        if (doYouWantToUndoAWholeMove) {
            if (this.listOfMoves.size() > 1) {
                if (this.currentMove.size() == 1) {
                    this.listOfMoves.remove(this.listOfMoves.size() - 1);
                    this.currentMove.clear();
                    this.currentMove = this.listOfMoves.get(this.listOfMoves.size() - 1);
                    do {

                        this.points.undoMove(getTwoLastPointFrom(this.currentMove));
                        this.currentMove.remove(this.currentMove.size() - 1);
                    } while (this.currentMove.size() > 1);

                    this.newPlayer = true;
                    return true;
                }
            }
            return false;
        }
        else {
            if (this.currentMove.size() > 1) {
                this.points.undoMove(getTwoLastPointFrom(this.currentMove));
                this.currentMove.remove(this.currentMove.size() - 1);
                this.listOfMoves.remove(this.listOfMoves.size() - 1);
                this.listOfMoves.add(this.currentMove);
                this.newPlayer = false;
                return true;
            }
            return false;
        }
    }
    private Point[] getTwoLastPointFrom(final List<Integer> list) {
        Point[] twoPoint = new Point[2];

        if (list.size() < 2) {
            return null;
        }

        twoPoint[0] = this.points.getPoint(list.get(list.size() - 1));
        twoPoint[1] = this.points.getPoint(list.get(list.size() - 2));

        return twoPoint;
    }




    /**
     * This class contains List<Point>, so this is the actual container of Point.
     * Here we can modify logical state of Point.
     */
    private static class Points implements Serializable {


        private final List<Point> listOfPoint;
        /**
         * This field tracks previous ball position. This is necessary to provide
         * good work of method undoMove.
         */
        private Point ballPosition;

        Points() {
            this.listOfPoint = createStandardListOfPoints();
            this.ballPosition = this.listOfPoint.get(58);
        }




        /**
         * This method take carry of setting notAvailableDirection of each Point that is passed.
         * This is true whenever is available Direction between those Point(s).
         * @param src is where move came from.
         * @param dest is where move is coming to.
         * @return if there is possibility to disconnect Direction return true.
         */
        boolean isLegal(final Point src, final Point dest) {
            int result = dest.getPosition() - src.getPosition();
            Direction tempDirection = BoardUtils.changeIntToDirection(result);
            if (tempDirection != null) {
                if (src.containsKey(tempDirection))
                    return true;
            }
            return false;
        }
        void makeMove(final Point src, final Point dest) throws IllegalArgumentException {
            int result = dest.getPosition() - src.getPosition();
            Direction tempDirection = BoardUtils.changeIntToDirection(result);
            if (tempDirection != null) {
                if (src.containsKey(tempDirection)) {
                    src.notAvailableDirection(tempDirection);
                    dest.notAvailableDirection(tempDirection.opposite());
                    setBallPosition(dest.getPosition());
                    return;
                }
            }
            throw new IllegalStateException("Probably you didn't use method isLegal() before execute this method.");
        }

        void undoMove(final Point... arg) throws IllegalArgumentException, NullPointerException {
            if (arg.length != 2)
                throw new IllegalArgumentException("Incorrect number of argument: " + arg.length + ", should be 2");

            Point beforeUndo = arg[0];
            Point afterUndo = arg[1];

            int result = beforeUndo.getPosition() - afterUndo.getPosition();
            Direction tempDirection = BoardUtils.changeIntToDirection(result);
            if (tempDirection == null)
                throw new NullPointerException("Invalid points. You can undo that move with those points!");

            if (!afterUndo.isAvailableDirection(tempDirection)) {
                afterUndo.setAvailableDirection(tempDirection);
                beforeUndo.setAvailableDirection(tempDirection.opposite());
                setBallPosition(afterUndo.getPosition());
            }
        }

        Point getPoint(final int position) {
            return this.listOfPoint.get(position);
        }
        boolean isThisGoal(final int position) {
            return isInSideGoal(position);
        }
        Player winnerIs(final int goalCandidate) {
            if (isTopGoal(goalCandidate))
                return Player.FIRST;
            else
                return Player.SECOND;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (Point point : listOfPoint) {
                stringBuilder.append(point.getPosition()).append(" ");
            }
            return stringBuilder.toString();
        }







        private void setBallPosition(final int ballPosition) {
            if ((!isNullPoint(ballPosition)) ||
                    ballPosition < 0 || ballPosition > 117)
                this.ballPosition = this.listOfPoint.get(ballPosition);
        }
        /**
         * Create standard list of Point. This method is invoked only in constructor.
         * Each point is modifiable.
         * @return List<Point>
         */
        private List<Point> createStandardListOfPoints() {
            List<Point> tempListPoint = new ArrayList<>();

            for (int i = 0; i < 117; i++) {
                if (isInSideGoal(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.S,
                            Direction.SW,
                            Direction.W,
                            Direction.NW,
                            Direction.N,
                            Direction.NE,
                            Direction.E,
                            Direction.SE);
                    tempListPoint.add(point);
                }
                else if (isCorner(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.S,
                            Direction.SW,
                            Direction.W,
                            Direction.NW,
                            Direction.N,
                            Direction.NE,
                            Direction.E,
                            Direction.SE);
                    tempListPoint.add(point);
                } else if (isTopEdgeOfPitch(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.W,
                            Direction.NW,
                            Direction.N,
                            Direction.NE,
                            Direction.E);
                    tempListPoint.add(point);
                } else if (isTopEdgeNearGoalRight(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.N,
                            Direction.NE,
                            Direction.E);
                    tempListPoint.add(point);
                } else if (isTopEdgeNearGoalLeft(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.N,
                            Direction.NW,
                            Direction.W);
                    tempListPoint.add(point);
                }
                else if (isBottomEdgeOfPitch(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.E,
                            Direction.SE,
                            Direction.S,
                            Direction.SW,
                            Direction.W);
                    tempListPoint.add(point);
                } else if (isBottomEdgeNearGoalRight(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.S,
                            Direction.SE,
                            Direction.E);
                    tempListPoint.add(point);
                } else if (isBottomEdgeNearGoalLeft(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.S,
                            Direction.SW,
                            Direction.W);
                    tempListPoint.add(point);
                } else if (isLeftEdgeOfPitch(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.S,
                            Direction.SW,
                            Direction.W,
                            Direction.NW,
                            Direction.N);
                    tempListPoint.add(point);
                } else if (isRightEdgeOfPitch(i)) {
                    Point point = new Point(i);
                    point.notAvailableDirection(Direction.S,
                            Direction.N,
                            Direction.NE,
                            Direction.E,
                            Direction.SE);
                    tempListPoint.add(point);
                }
                else
                    tempListPoint.add(new Point(i));
            }


            return Collections.unmodifiableList(tempListPoint);
        }


        private static boolean isNullPoint(final int position) {
            return position == 0 || position == 1 || position == 2
                    || position == 6 || position == 7 || position == 8
                    || position == 108 || position == 109 || position == 110
                    || position == 114 || position == 115 || position == 116;
        }
        private static boolean isInSideGoal(final int position) {
            return position == 3 || position == 4 || position == 5
                    || position == 111 || position == 112 || position == 113;
        }
        private static boolean isTopGoal(final int position) {
            return position == 3 || position == 4 || position == 5;
        }
        private static boolean isCorner(final int position) {
            return position == 9 || position == 17 || position == 99 || position == 107;
        }
        private static boolean isTopEdgeOfPitch(final int position) {
            return position == 10 || position == 11
                    || position == 15 || position == 16;
        }
        private static boolean isTopEdgeNearGoalRight(final int position) {
            return position == 14;
        }
        private static boolean isTopEdgeNearGoalLeft(final int position) {
            return position == 12;
        }
        private static boolean isBottomEdgeOfPitch(final int position) {
            return position == 101 || position == 100
                    || position == 105 || position == 106;
        }
        private static boolean isBottomEdgeNearGoalRight(final int position) {
            return position == 104;
        }
        private static boolean isBottomEdgeNearGoalLeft(final int position) {
            return position == 102;
        }
        private static boolean isLeftEdgeOfPitch(final int position) {
            return position == 18
                    || position == 27
                    || position == 36
                    || position == 45
                    || position == 54
                    || position == 63
                    || position == 72
                    || position == 81
                    || position == 90;
        }
        private static boolean isRightEdgeOfPitch(final int position) {
            return position == 26
                    || position == 35
                    || position == 44
                    || position == 53
                    || position == 62
                    || position == 71
                    || position == 80
                    || position == 89
                    || position == 98;
        }

    }

}
