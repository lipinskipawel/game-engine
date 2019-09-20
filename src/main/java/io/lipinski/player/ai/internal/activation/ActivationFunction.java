package io.lipinski.player.ai.internal.activation;

import io.lipinski.player.ai.internal.Matrix;

abstract public class ActivationFunction {


    abstract public Matrix compute(final Matrix matrix);

    abstract public Matrix derivative(final Matrix matrix);

}
