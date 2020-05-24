package com.github.lipinskipawel.board.ai.ml.activation;

import com.github.lipinskipawel.board.ai.ml.Matrix;

abstract public class ActivationFunction {


    abstract public Matrix compute(final Matrix matrix);

    abstract public Matrix derivative(final Matrix matrix);

}
