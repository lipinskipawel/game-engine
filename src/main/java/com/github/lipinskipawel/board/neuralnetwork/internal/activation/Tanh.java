package com.github.lipinskipawel.board.neuralnetwork.internal.activation;

import com.github.lipinskipawel.board.neuralnetwork.internal.Matrix;

import java.util.Arrays;

final public class Tanh extends ActivationFunction {

    @Override
    public Matrix compute(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .map(row -> row[0])
                .mapToDouble(Math::tanh)
                .toArray()
        );
    }

    @Override
    public Matrix derivative(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .map(row -> row[0])
                .mapToDouble(value -> 1 - (value * value))
                .toArray()
        );
    }
}
