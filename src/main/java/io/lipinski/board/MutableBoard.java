package io.lipinski.board;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MutableBoard implements BoardInterface, Serializable {


    private final MoveLog moveLog;
    private Point ballPosition;

    private Player currentPlayer;

    public MutableBoard() {
        this.moveLog = new MoveLog();
        this.ballPosition = this.moveLog.getBall();
        this.currentPlayer = Player.FIRST;
    }


    @Override
    public boolean tryMakeMove(final Point destination) {
        return makeMoveHelper(destination);
    }

    @Override
    public boolean tryMakeMove(final Direction direction) {
        Point point = calculatePointPosition(direction, ballPosition);
//        return makeMoveHelper(this.moveLog.getPoint(direction.changeToInt()));
        return makeMoveHelper(point);
    }

    private Point calculatePointPosition(Direction direction, Point currentBallPosition) {

        // some logic, that's need to be shift somewhere else when refactor of code will be executed
        if (direction.changeToInt() > 0)
            return new Point(currentBallPosition.getPosition() - direction.changeToInt());
        else return new Point(currentBallPosition.getPosition() + direction.changeToInt());

    }

    @Override
    public boolean undoMove() {
        return undoMoveHelper(false);
    }

    @Override
    public boolean undoMove(final boolean canIgoBack) {
        return undoMoveHelper(canIgoBack);
    }


    @Override
    public List<List<Integer>> getMoveList() {
        return Collections.unmodifiableList(this.moveLog.getListOfMoves());
    }

    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public Player getOppositePlayer() {
        return getOppositePlayerr();
    }

    @Override
    public int getBallPosition() {
        return this.moveLog.getBall().getPosition();
    }

    @Override
    public Point getPoint(final int position) {
        return new Point(this.moveLog.getPoint(position));
    }

    @Override
    public boolean isThisGoal(final Point point) {
        return this.moveLog.isThisGoal(point.getPosition());
    }

    @Override
    public Player winnerIs(final int goalCandidatePosition) {
        if (!this.moveLog.isThisGoal(goalCandidatePosition))
            throw new IllegalArgumentException("This point " + goalCandidatePosition + "\nisn't a winner point");
        return this.moveLog.winnerIs(goalCandidatePosition);
    }


    public Direction[] getAvailableDirection() {
        return this.moveLog.getAvailableDirection();
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<List<Integer>> tempMoveList = this.moveLog.getListOfMoves();
        List<Integer> tempSmallMove;
        for (List<Integer> aTempMoveList : tempMoveList) {
            tempSmallMove = aTempMoveList;
            stringBuilder.append("\n");
            for (Integer aTempSmallMove : tempSmallMove) {
                stringBuilder.append(aTempSmallMove).append(" ");
            }
            stringBuilder.append(" --- ");
        }
        return stringBuilder.toString();
    }

    /**
     * This method operates on field points in the if statement.
     *
     * @param destination Point that user wants to reach.
     * @return boolean true if the move is executed or false if doesn't.
     */
    private boolean makeMoveHelper(final Point destination) {
        if (this.moveLog.addMove(this.ballPosition, destination)) {
            this.ballPosition = this.moveLog.getBall();
            if (this.moveLog.isNewPlayerRequire()) {
                this.currentPlayer = getOppositePlayerr();
            }
            return true;
        }
        return false;
    }

    private boolean undoMoveHelper(final boolean canIgoBack) {
        if (this.moveLog.undoMove(canIgoBack)) {
            this.ballPosition = this.moveLog.getBall();
            return true;
        }
        return false;
    }


    private Player getOppositePlayerr() {
        if (this.currentPlayer == Player.FIRST)
            return Player.SECOND;
        return Player.FIRST;
    }

}
