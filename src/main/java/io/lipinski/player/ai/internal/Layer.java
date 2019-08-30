package io.lipinski.player.ai.internal;

import static io.lipinski.player.ai.internal.Activation.SIGMOID;

public final class Layer {

    private final int numberOfNodes;
    private final Activation activationFunction;

    private Layer(final int numberOfNodes,
                  final Activation activationFunction) {
        this.numberOfNodes = numberOfNodes;
        this.activationFunction = activationFunction;
    }

    public Layer(final int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        this.activationFunction = SIGMOID;
    }

    int getNumberOfNodes() {
        return this.numberOfNodes;
    }

    Activation getActivationFunction() {
        return this.activationFunction;
    }
}
