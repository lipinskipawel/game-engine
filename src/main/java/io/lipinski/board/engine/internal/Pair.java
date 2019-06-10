package io.lipinski.board.engine.internal;

public final class Pair<F, S> {

    public Pair(final F first,
                final S second) {
        this.first = first;
        this.second = second;
    }


    public F first;
    public S second;


    public static <F, S> Pair<F, S> pair(final F first,
                                         final S second) {
        return new Pair<>(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
