package io.lipinski.player.ai.internal;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Wraps output of Neural Network.
 *
 * @param <T>
 */
final class Result<T extends Number & Comparable> {

    private List<T> list;


    private Result(final List<T> data) {
        sanityCheck(data);
        this.list = data;
    }

    static <T extends Number & Comparable> Result of(final T[] data) {
        sanityCheck(data);
        return new Result<>(Arrays.stream(data)
                .collect(toList()));
    }

    /**
     * It will return index of node in the output layer.
     *
     * @return
     */
    public final int getBestMatch() {
        return this.list
                .indexOf(getBestValue());
    }

    /**
     * It will return actual value which had best score
     * given by neural network.
     *
     * @return
     */
    public final T getBestValue() {
        return this.list
                .stream()
                .max(Comparable::compareTo)
                .orElseThrow(() -> new RuntimeException("This will never happen. " +
                        "It means that you pass list with 0 size"));
    }

    private static <T> void sanityCheck(final T[] data) {
        requireNonNull(data);
        if (data.length == 0) throw new RuntimeException("Size of output is 0");
    }

    private static <T> void sanityCheck(final List<T> data) {
        requireNonNull(data);
        if (data.size() == 0) throw new RuntimeException("Size of output is 0");
    }
}
