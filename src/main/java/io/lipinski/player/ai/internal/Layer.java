package io.lipinski.player.ai.internal;

public final class Layer {

    private final int numberOfNodes;
    private final ActivationFunction activationFunction;

    private Layer(final int numberOfNodes,
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
