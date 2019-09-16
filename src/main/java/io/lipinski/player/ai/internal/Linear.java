package io.lipinski.player.ai.internal;

import java.util.Arrays;

final public class Linear extends ActivationFunction {

    @Override
    public Matrix compute(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .mapToDouble(row -> row[0])
                .toArray()
        );
    }

    @Override
    public Matrix derivative(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .mapToDouble(row -> 1)
                .toArray()
        );
    }
}
