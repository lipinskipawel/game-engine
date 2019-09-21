package io.lipinski.player.ai.internal;

import io.lipinski.player.ai.internal.activation.ActivationFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class NetworkDetails {

    final List<Matrix> nodes;
    final List<Matrix> biases;
    final List<ActivationFunction> activations;

    NetworkDetails(List<Layer> layers) {
        var architecture = layers
                .stream()
                .mapToInt(Layer::getNumberOfNodes)
                .toArray();
        var activationFunctions = layers
                .stream()
                .map(Layer::getActivationFunction)
                .collect(Collectors.toList());
        this.nodes = new ArrayList<>(architecture.length);
        this.biases = new ArrayList<>(architecture.length);
        this.activations = new ArrayList<>(architecture.length);

        for (int i = 0; i < architecture.length; i++) {
            if (i == 0)
                this.nodes.add(Matrix.of(architecture[i], architecture[i]));
            else
                this.nodes.add(Matrix.of(architecture[i], architecture[i - 1]));
            this.biases.add(Matrix.of(architecture[i], 1));
            this.activations.add(activationFunctions.get(i));
        }
    }

    NetworkDetails(List<Matrix> weights, List<Matrix> biases, List<ActivationFunction> activations) {
        this.nodes = new ArrayList<>(weights);
        this.biases = new ArrayList<>(biases);
        this.activations = activations;
    }

}
