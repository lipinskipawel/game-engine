package io.lipinski.player.ai.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;

/**
 * Wraps output of Neural Network.
 *
 * @param <T>
 */
final class Result<T> {

    private final List<T> data;


    Result(List<T> data) {
        this.data = new ArrayList<>(data);
    }


    static Result of(final double[] data) {
        return new Result<>(DoubleStream.of(data)
                .boxed()
                .collect(toList()));
    }

    public final T getBest() {
        return data.get(0);
    }

}
