package com.github.lipinskipawel.board.ai.ml.lossfunction;

import com.github.lipinskipawel.board.ai.ml.Matrix;

final public class MSE extends LossFunction {

    @Override
    public Matrix compute(final Matrix target, final Matrix output) {
        return target.subtract(output);
    }
}
