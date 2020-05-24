package com.github.lipinskipawel.board.ai.ml;

import java.util.ArrayList;
import java.util.List;

final class WrapperNeuralNetwork {

    private final List<LayerDTO> layers;
    private final Class<? extends NeuralNetwork> neuralNetworkClass;

    private WrapperNeuralNetwork(final List<LayerDTO> layers,
                                 final Class<? extends NeuralNetwork> neuralNetworkClass) {
        this.layers = layers;
        this.neuralNetworkClass = neuralNetworkClass;
    }

    static WrapperNeuralNetwork of(final NeuralNetwork model) {
        List<LayerDTO> layers = new ArrayList<>();
        Class<? extends NeuralNetwork> neuralNetworkClass = NeuralNetwork.class;
        if (model instanceof SimpleNeuralNetwork) {
            final var simple = (SimpleNeuralNetwork) model;
            layers = simple.networkDetails().layers();
            neuralNetworkClass = simple.getClass();
        }

        return new WrapperNeuralNetwork(layers, neuralNetworkClass);
    }

    List<LayerDTO> layers() {
        return this.layers;
    }

    Class<? extends NeuralNetwork> neuralNetworkClass() {
        return this.neuralNetworkClass;
    }
}
