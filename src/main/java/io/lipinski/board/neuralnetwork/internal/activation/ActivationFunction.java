package io.lipinski.board.neuralnetwork.internal.activation;

import io.lipinski.board.neuralnetwork.internal.Matrix;

abstract public class ActivationFunction {


    abstract public Matrix compute(final Matrix matrix);

    abstract public Matrix derivative(final Matrix matrix);

}
