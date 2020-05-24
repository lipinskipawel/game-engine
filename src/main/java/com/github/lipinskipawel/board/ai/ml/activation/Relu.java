package com.github.lipinskipawel.board.ai.ml.activation;

import com.github.lipinskipawel.board.ai.ml.Matrix;

import java.util.Arrays;

final public class Relu extends ActivationFunction {

    @Override
    public Matrix compute(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .map(row -> row[0])
                .mapToDouble(x -> Math.max(0, x))
                .toArray()
        );
    }

    @Override
    public Matrix derivative(final Matrix matrix) {
        return Matrix.of(Arrays.stream(matrix.rawData())
                .map(row -> row[0])
                .mapToDouble(val -> val > 0 ? 1 : 0)
                .toArray()
        );
    }
}
