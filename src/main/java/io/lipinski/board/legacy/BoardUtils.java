package io.lipinski.board.legacy;

import io.lipinski.board.engine.Direction;

import java.util.EnumMap;
import java.util.Map;

@Deprecated
public class BoardUtils {


    public static Map<Direction, Integer> createDirection() {
        Map<Direction, Integer> temp = new EnumMap<>(Direction.class);
        temp.put(Direction.S, 9);
        temp.put(Direction.SW, 8);
        temp.put(Direction.W, -1);
        temp.put(Direction.NW, -10);
        temp.put(Direction.N, -9);
        temp.put(Direction.NE, -8);
        temp.put(Direction.E, 1);
        temp.put(Direction.SE, 10);
        return temp;
    }
    static Direction changeIntToDirection(final int integerDirection) {
        switch (integerDirection) {
            case 9:
                return Direction.S;
            case 8:
                return Direction.SW;
            case -1:
                return Direction.W;
            case -10:
                return Direction.NW;
            case -9:
                return Direction.N;
            case -8:
                return Direction.NE;
            case 1:
                return Direction.E;
            case 10:
                return Direction.SE;

            default:
                return null;
        }
    }
    static int changeDirectionToInt(final Direction direction) {
        switch (direction) {
            case S:
                return 9;
            case SW:
                return 8;
            case W:
                return -1;
            case NW:
                return -10;
            case N:
                return -9;
            case NE:
                return -8;
            case E:
                return 1;
            case SE:
                return 10;

            default:
                return 0;
        }
    }
}
