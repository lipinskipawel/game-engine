package com.github.lipinskipawel.board.neuralnetwork.internal.lossfunction;

import com.github.lipinskipawel.board.neuralnetwork.internal.Matrix;

abstract public class LossFunction {


    abstract public Matrix compute(Matrix target, Matrix output);
}
