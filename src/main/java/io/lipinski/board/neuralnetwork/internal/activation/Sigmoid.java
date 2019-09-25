package io.lipinski.board.neuralnetwork.internal.activation;

import io.lipinski.board.neuralnetwork.internal.Matrix;

import java.util.Arrays;

final public class Sigmoid extends ActivationFunction {

    @Override
    public Matrix compute(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .map(row -> row[0])
                .mapToDouble(value -> 1 / (1 + Math.exp(-value)))
                .toArray()
        );
    }

    @Override
    public Matrix derivative(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .map(row -> row[0])
                .mapToDouble(value -> value * (1 - value))
                .toArray()
        );
    }
}
