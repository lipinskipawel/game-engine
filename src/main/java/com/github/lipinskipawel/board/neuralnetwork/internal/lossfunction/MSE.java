package com.github.lipinskipawel.board.neuralnetwork.internal.lossfunction;

import com.github.lipinskipawel.board.neuralnetwork.internal.Matrix;

final public class MSE extends LossFunction {

    @Override
    public Matrix compute(final Matrix target, final Matrix output) {
        return target.subtract(output);
    }
}
