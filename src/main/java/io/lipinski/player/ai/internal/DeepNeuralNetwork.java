package io.lipinski.player.ai.internal;

import io.lipinski.board.engine.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

final class DeepNeuralNetwork {

    private final List<Layer> layers;
    final Activation activation;
    private Result result;
    double learningRate;
    private boolean isBatchingEnable;
    private int batch;

    int[] architecture;


    private DeepNeuralNetwork(final Builder builder) {
        if (builder.layers.size() == 1)
            throw new RuntimeException("You defined only one layer (input layer). You have to declare at least 2 layers");
        this.layers = builder.layers;
        this.activation = builder.activation;
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

    NeuralNetwork build() {
        this.architecture = this.layers
                .stream()
                .mapToInt(Layer::getNumberOfNodes)
                .toArray();
        return SimpleNeuralNetwork.factory(this);
    }


    public static final class Builder {
        private List<Layer> layers;
        private Activation activation;
        private Result result;
        private Class<?> output;

        public Builder() {
        }

        public Builder addLayer(final Layer layer) {
            if (this.layers == null) this.layers = new ArrayList<>();
            this.layers.add(layer);
            return this;
        }

        public Builder activationOnLayers(final Activation activation) {
            this.activation = activation;
            return this;
        }

        <T extends Enum & ResultInterface> Builder output(final List<T> directionJavaDoc) {
            final var collect = directionJavaDoc.stream()
                    .sorted(Comparator.comparingInt(ResultInterface::order))
                    .collect(Collectors.toList());
//            this.result = new Result<>(collect);
            this.output = Direction.class;
            return this;
        }

        /**
         * @param clazz it has to be int[] or double[] or float[] or byte[]
         * @return
         */
        Builder output(final Class<?> clazz) {
            if (!clazz.isArray())
                throw new RuntimeException("Only arrays is allowed in this method");
            final Class<?> type = clazz.getComponentType();
            if (!type.isPrimitive())
                throw new RuntimeException("Only primitives types is allowed in this method");
            if (type.getName().equals("int")) {
                this.output = int.class;
                return this;
            }
            if (type.getName().equals("double")) {
                this.output = double.class;
                return this;
            }
            if (type.getName().equals("float")) {
                this.output = float.class;
                return this;
            }
            if (type.getName().equals("byte")) {
                this.output = byte.class;
                return this;
            }

            throw new RuntimeException("Only array of int or double or float is allowed");
        }

        public DeepNeuralNetwork compile() {
            requireNonNull(this.layers, "You have to add layer");
            requireNonNull(this.layers, "You have to configure output");
            return new DeepNeuralNetwork(this);
        }
    }
}
