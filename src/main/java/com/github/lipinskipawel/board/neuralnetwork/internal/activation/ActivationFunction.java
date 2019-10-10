package com.github.lipinskipawel.board.neuralnetwork.internal.activation;

import com.github.lipinskipawel.board.neuralnetwork.internal.Matrix;

abstract public class ActivationFunction {


    abstract public Matrix compute(final Matrix matrix);

    abstract public Matrix derivative(final Matrix matrix);

}
