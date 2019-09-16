package io.lipinski.player.ai.internal;

import java.util.Arrays;

final public class Softmax extends ActivationFunction {

    @Override
    public Matrix compute(final Matrix matrix) {
        final var max = Arrays.stream(matrix.rawData())
                .mapToDouble(row -> row[0])
                .max()
                .getAsDouble();
        final var doubleValues = Arrays.stream(matrix.rawData())
                .mapToDouble(row -> row[0])
                .map(x -> x - max)
                .toArray();
        final var e_x = Arrays.stream(doubleValues).map(Math::exp).toArray();
        final var sum = Arrays.stream(e_x).sum();
        return Matrix.of(Arrays.stream(e_x)
                .map(x -> x / sum)
                .toArray()
        );
    }

    @Override
    public Matrix derivative(final Matrix matrix) {
        final var minuses = Arrays.stream(matrix.rawData())
                .mapToDouble(row -> row[0])
                .map(x -> 1.0 - x)
                .toArray();
        final var doubles = Arrays.stream(matrix.rawData())
                .mapToDouble(row -> row[0])
                .toArray();
        final var length = matrix.rawData().length;
        final double[] output = new double[length];
        for (int i = 0; i < length; i++) {
            output[i] = doubles[i] * minuses[i];
        }
        return Matrix.of(output);
    }
}
