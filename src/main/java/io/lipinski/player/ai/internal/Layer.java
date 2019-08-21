package io.lipinski.player.ai.internal;

public final class Layer {

    private final int numberOfNodes;
    private final Activation activationFunction;

    public Layer(final int numberOfNodes,
                 final Activation activationFunction) {
        this.numberOfNodes = numberOfNodes;
        this.activationFunction = activationFunction;
    }

    int getNumberOfNodes() { return this.numberOfNodes; }
    Activation getActivationFunction() { return this.activationFunction; }
}
