package io.lipinski.board;


public enum Direction {

    S {
        @Override
        public Direction opposite() {
            return N;
        }

        @Override
        public int changeToInt() {
            return 9;
        }
    },
    SW {
        @Override
        public Direction opposite() {
            return NE;
        }

        @Override
        public int changeToInt() {
            return 8;
        }
    },
    W {
        @Override
        public Direction opposite() {
            return E;
        }

        @Override
        public int changeToInt() {
            return -1;
        }
    },
    NW {
        @Override
        public Direction opposite() {
            return SE;
        }

        @Override
        public int changeToInt() {
            return -10;
        }
    },
    N {
        @Override
        public Direction opposite() {
            return S;
        }

        @Override
        public int changeToInt() {
            return -9;
        }
    },
    NE {
        @Override
        public Direction opposite() {
            return SW;
        }

        @Override
        public int changeToInt() {
            return -8;
        }
    },
    E {
        @Override
        public Direction opposite() {
            return W;
        }

        @Override
        public int changeToInt() {
            return 1;
        }
    },
    SE {
        @Override
        public Direction opposite() {
            return NW;
        }

        @Override
        public int changeToInt() {
            return 10;
        }
    };


    @Override
    public String toString() {
        return this.name();
    }


    public abstract Direction opposite();
    public abstract int changeToInt();

}
