package io.lipinski.player.ai.internal;

import io.lipinski.board.engine.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

final class NeuralNetworkFactory {

    private final List<Layer> layers;
    private Result result;
    private double learningRate;
    private boolean isBatchingEnable;
    private int batch;

    int[] architecture;


    private NeuralNetworkFactory(final Builder builder) {
        this.layers = builder.layers;
        this.result = builder.result;
        this.isBatchingEnable = true;
        this.batch = 32;
    }

    NeuralNetworkFactory learningRate(final double lr) {
        this.learningRate = lr;
        return this;
    }

    NeuralNetworkFactory noBatching() {
        this.isBatchingEnable = false;
        return this;
    }

    NeuralNetworkFactory batch(final int batch) {
        this.batch = batch;
        return this;
    }

    NeuralNetwork build() {
        this.architecture = this.layers
                .stream()
                .mapToInt(Layer::getNumberOfNodes)
                .toArray();


        final List<Activation> collect = this.layers
                .stream()
                .map(Layer::getActivationFunction)
                .collect(
                        ArrayList::new,
                        ArrayList::add,
                        ArrayList::addAll
                );


        final Activation[] collect2 = this.layers
                .stream()
                .map(Layer::getActivationFunction)
                .collect(
                        ArrayList::new,
                        ArrayList::add,
                        ArrayList::addAll)
                .toArray(Activation[]::new);

        return SimpleNeuralNetwork.factory(this);
    }


    public static final class Builder {
        private List<Layer> layers;
        private Result result;
        private Class<?> output;

        public Builder() {
        }

        public Builder addLayer(final Layer layer) {
            if (this.layers == null) this.layers = new ArrayList<>();
            this.layers.add(layer);
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

        public NeuralNetworkFactory compile() {
            requireNonNull(this.layers, "You have to add layer");
            requireNonNull(this.layers, "You have to configure output");
            return new NeuralNetworkFactory(this);
        }
    }
}
