package io.lipinski.player.ai.internal;

import io.lipinski.player.ai.internal.lossfunction.LossFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

final class DeepNeuralNetwork {

    private final List<Layer> layers;
    private Result result;
    double learningRate;
    LossFunction lossFunction;
    private boolean isBatchingEnable;
    private int batch;
    private int[] architecture;

    NetworkDetails networkDetails;


    private DeepNeuralNetwork(final Builder builder) {
        this.layers = builder.layers;
        this.result = builder.result;
        this.isBatchingEnable = true;
        this.batch = 32;
        this.learningRate = 0.1;
    }

    DeepNeuralNetwork learningRate(final double lr) {
        this.learningRate = lr;
        return this;
    }

    DeepNeuralNetwork noBatching() {
        this.isBatchingEnable = false;
        return this;
    }

    DeepNeuralNetwork batch(final int batch) {
        this.batch = batch;
        return this;
    }

    DeepNeuralNetwork lossFunction(final LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        return this;
    }

    NeuralNetwork build() {
        if (this.lossFunction == null)
            throw new RuntimeException("Loss function is not defined");
        this.networkDetails = new NetworkDetails(layers);
        return SimpleNeuralNetwork.factory(this);
    }


    public static final class Builder {
        private List<Layer> layers;
        private Result result;
        private List<?> output;

        public Builder() {
        }

        public Builder addLayer(final Layer layer) {
            if (this.layers == null)
                this.layers = new ArrayList<>();
            this.layers.add(layer);
            return this;
        }

        /**
         * By default output of neural network is double type. Either double
         * or double[].
         *
         * @param type
         * @param <T>
         * @return Builder
         */
        <T extends Enum & ResultInterface> Builder output(final Class<T> type) {
            final T[] enumConstants = type.getEnumConstants();

            this.output = Arrays.stream(enumConstants)
                    .sorted(Comparator.comparingInt(T::order))
                    .collect(toList());
            return this;
        }

        public DeepNeuralNetwork compile() {
            requireNonNull(this.layers, "You have to add layer");
            requireNonNull(this.layers, "You have to configure output");
            return new DeepNeuralNetwork(this);
        }
    }
}
