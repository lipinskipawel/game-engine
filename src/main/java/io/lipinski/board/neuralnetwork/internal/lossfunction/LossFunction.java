package io.lipinski.board.neuralnetwork.internal.lossfunction;

import io.lipinski.board.neuralnetwork.internal.Matrix;

abstract public class LossFunction {


    abstract public Matrix compute(Matrix target, Matrix output);
}
