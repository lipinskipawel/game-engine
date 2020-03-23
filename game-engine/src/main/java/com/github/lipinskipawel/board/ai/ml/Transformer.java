package com.github.lipinskipawel.board.ai.ml;

interface Transformer<R> {

    R transform(final WrapperNeuralNetwork wrapper);
}
