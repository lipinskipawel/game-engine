package com.github.lipinskipawel.board.ai.ml.activation;

import com.github.lipinskipawel.board.ai.ml.Matrix;

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
