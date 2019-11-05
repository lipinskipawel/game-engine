package com.github.lipinskipawel.board.ai.ml.lossfunction;

import com.github.lipinskipawel.board.ai.ml.Matrix;

abstract public class LossFunction {


    abstract public Matrix compute(Matrix target, Matrix output);
}
