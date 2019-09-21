package io.lipinski.player.ai.internal.lossfunction;

import io.lipinski.player.ai.internal.Matrix;

abstract public class LossFunction {


    abstract public Matrix compute(Matrix target, Matrix output);
}
