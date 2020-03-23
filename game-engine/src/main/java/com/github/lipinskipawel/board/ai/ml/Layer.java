package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.ai.ml.activation.ActivationFunction;
import com.github.lipinskipawel.board.ai.ml.activation.Sigmoid;

public final class Layer {

    private final int numberOfNodes;
    private final ActivationFunction activationFunction;

    Layer(final int numberOfNodes,
          final ActivationFunction activationFunction) {
        this.numberOfNodes = numberOfNodes;
        this.activationFunction = activationFunction;
    }

    public Layer(final int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        this.activationFunction = new Sigmoid();
    }

    int getNumberOfNodes() {
        return this.numberOfNodes;
    }

    ActivationFunction getActivationFunction() {
        return this.activationFunction;
    }
}
