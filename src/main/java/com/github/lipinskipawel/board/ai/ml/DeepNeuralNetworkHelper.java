package com.github.lipinskipawel.board.ai.ml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class DeepNeuralNetworkHelper {

    private final List<LayerDTO> layers;

    DeepNeuralNetworkHelper() {
        this.layers = new ArrayList<>();
    }

    void addLayer(final LayerDTO layerDTO) {
        this.layers.add(layerDTO);
    }

    NeuralNetwork compile() {
        final var weight = layers
                .stream()
                .map(LayerDTO::getWeight)
                .collect(Collectors.toList());
        final var biases = layers
                .stream()
                .map(LayerDTO::getBiases)
                .collect(Collectors.toList());
        final var activationFunction = layers
                .stream()
                .map(LayerDTO::getActivationFunction)
                .collect(Collectors.toList());
        return new SimpleNeuralNetwork(weight, biases, activationFunction, 0.1);
    }
}
