package io.lipinski.board.engine;

import io.lipinski.player.ai.internal.ResultInterface;

public enum Direction implements ResultInterface {

    S(4) {
        @Override
        public Direction opposite() {
            return N;
        }

        @Override
        public int changeToInt() {
            return 9;
        }
    },
    SW(5) {
        @Override
        public Direction opposite() {
            return NE;
        }

        @Override
        public int changeToInt() {
            return 8;
        }
    },
    W(6) {
        @Override
        public Direction opposite() {
            return E;
        }

        @Override
        public int changeToInt() {
            return -1;
        }
    },
    NW(7) {
        @Override
        public Direction opposite() {
            return SE;
        }

        @Override
        public int changeToInt() {
            return -10;
        }
    },
    N(0) {
        @Override
        public Direction opposite() {
            return S;
        }

        @Override
        public int changeToInt() {
            return -9;
        }
    },
    NE(1) {
        @Override
        public Direction opposite() {
            return SW;
        }

        @Override
        public int changeToInt() {
            return -8;
        }
    },
    E(2) {
        @Override
        public Direction opposite() {
            return W;
        }

        @Override
        public int changeToInt() {
            return 1;
        }
    },
    SE(3) {
        @Override
        public Direction opposite() {
            return NW;
        }

        @Override
        public int changeToInt() {
            return 10;
        }
    };


    private final int order;

    Direction(final int order) {
        this.order = order;
    }

    @Override
    public int order() {
        return this.order;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public abstract Direction opposite();
    public abstract int changeToInt();

}
