package io.lipinski.board;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import static io.lipinski.board.Direction.E;
import static io.lipinski.board.Direction.N;
import static io.lipinski.board.Direction.NE;
import static io.lipinski.board.Direction.NW;
import static io.lipinski.board.Direction.S;
import static io.lipinski.board.Direction.SE;
import static io.lipinski.board.Direction.SW;
import static io.lipinski.board.Direction.W;

/**
 * Thread unsafe class.
 */
public class Point implements Serializable {

    private Map<Direction, Integer> availableDirections;
    private final int position;

    Point(final int position) {
        this.position = position;
        this.availableDirections = BoardUtils.createDirection();
    }

    Point(final Point point) {
        this.position = point.position;
        this.availableDirections = point.availableDirections;
    }




    boolean containsKey(final Direction direction) {
        return this.availableDirections.containsKey(direction);
    }

    boolean isAvailableDirection(final Direction direction) {
        return this.availableDirections.containsKey(direction);
    }
    Direction[] getAvailableDirection() {
        Direction[] dir = new Direction[this.availableDirections.size()];
        int i = 0;
        for (Direction direction : this.availableDirections.keySet()) {
            dir[i] = direction;
            i++;
        }
        return dir;
    }


    void notAvailableDirection(Direction direction) {
        this.availableDirections.remove(direction);
    }
    void notAvailableDirection(Direction... directions) {
        for (Direction direction : directions) {
            notAvailableDirection(direction);
        }
    }
    void setAvailableDirection(final Direction direction) {
        this.availableDirections.put(direction, BoardUtils.changeDirectionToInt(direction));
    }




    boolean isCanMoveAgain() {
        return this.availableDirections.size() != 8;
    }
    public boolean isZeroMovesAvailable() {
        return this.availableDirections.size() == 0;
    }
    int getPosition() {
        return this.position;
    }




    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Direction d : this.availableDirections.keySet()) {
            stringBuilder.append(d).append(" ");
        }
        return "Position: " + this.position;
                //+ " " + stringBuilder.toString();


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (position != point.position) return false;
        return availableDirections != null ? availableDirections.equals(point.availableDirections) : point.availableDirections == null;
    }

    @Override
    public int hashCode() {
        int result = availableDirections != null ? availableDirections.hashCode() : 0;
        result = 31 * result + position;
        return result;
    }


    private static Map<Direction, Integer> createDirections() {
        Map<Direction, Integer> temp = new EnumMap<>(Direction.class);
        temp.put(S, 9);
        temp.put(SW, 8);
        temp.put(W, -1);
        temp.put(NW, -10);
        temp.put(N, -9);
        temp.put(NE, -8);
        temp.put(E, 1);
        temp.put(SE, 10);
        return temp;
    }

}
