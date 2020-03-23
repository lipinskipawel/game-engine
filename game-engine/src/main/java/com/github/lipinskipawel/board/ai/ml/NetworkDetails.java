package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.ai.ml.activation.ActivationFunction;
import com.github.lipinskipawel.board.ai.ml.lossfunction.LossFunction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class NetworkDetails {

    private final List<LayerInternal> layers;

    NetworkDetails(List<Layer> layers) {
        var architecture = layers
                .stream()
                .mapToInt(Layer::getNumberOfNodes)
                .toArray();
        var activationFunctions = layers
                .stream()
                .map(Layer::getActivationFunction)
                .collect(Collectors.toList());
        this.layers = new ArrayList<>(architecture.length);
        for (int i = 0; i < architecture.length; i++) {
            if (i == 0) {
                this.layers.add(new LayerInternal(
                        Matrix.of(architecture[i], architecture[i]),
                        Matrix.of(architecture[i], 1),
                        activationFunctions.get(i)));
            } else {
                this.layers.add(new LayerInternal(
                        Matrix.of(architecture[i], architecture[i - 1]),
                        Matrix.of(architecture[i], 1),
                        activationFunctions.get(i)));
            }
        }
        randomize();
    }

    NetworkDetails(List<Matrix> weights, List<Matrix> biases, List<ActivationFunction> activations) {
        this.layers = new ArrayList<>();
        if (weights.size() == biases.size() && weights.size() == activations.size()) {
            for (var index = 0; index < weights.size(); index++) {
                this.layers.add(new LayerInternal(weights.get(index), biases.get(index), activations.get(index)));
            }
        }
    }

    NetworkDetails(final NetworkDetails networkDetails) {
        this.layers = networkDetails.layers;
    }

    /**
     * @param inputData shape of matrix nx1
     * @return
     */
    List<Matrix> feedForward(final Matrix inputData) {
        if (wrongShape(inputData))
            throw new RuntimeException("Wrong shape of input data");

        final var outputOnEachLayer = new ArrayDeque<Matrix>();
        outputOnEachLayer.add(inputData);
        layers.forEach(layer -> outputOnEachLayer.add(layer.forward(outputOnEachLayer.getLast())));
        outputOnEachLayer.removeFirst();
        return new ArrayList<>(outputOnEachLayer);
    }

    void backpropagation(final Matrix inputData,
                         final Matrix labels,
                         final LossFunction lossFunction,
                         final double lr) {
        final var outputOnLayers = feedForward(inputData);
        final var deltas = computeDeltas(inputData, outputOnLayers);
        final var errors = new ArrayList<Matrix>();
        var indexError = 0;
        for (var index = layers.size() - 1; index >= 0; index--) {

            final var error = computeError(index, labels, outputOnLayers.get(outputOnLayers.size() - 1), errors, indexError - 1, lossFunction);
            errors.add(error);

            final var gradient = layers.get(index).gradient(outputOnLayers.get(index), errors.get(indexError), lr);
            final var delta = layers.get(index).delta(gradient, deltas.get(index));
            indexError++;

            layers.get(index).updateWeight(delta);
            layers.get(index).updateBias(gradient);
        }
    }

    private List<Matrix> computeDeltas(final Matrix data,
                                       final List<Matrix> outputOnLayers) {
        final var deltas = new ArrayList<Matrix>();
        deltas.add(data.transpose());
        for (Matrix some : outputOnLayers) {
            deltas.add(some.transpose());
        }
        return deltas;
    }

    private Matrix computeError(final int index,
                                final Matrix labels,
                                final Matrix outputs,
                                final List<Matrix> computedErrors,
                                final int j,
                                final LossFunction lossFunction) {
        if (index == layers.size() - 1) {
            return lossFunction.compute(labels, outputs);
        }
        return layers.get(index + 1).error(computedErrors.get(j));
    }

    void randomize() {
        for (var layer : layers) {
            Func func = x -> Math.random() - .5;
            layer.randomize(func);
        }
    }

    List<LayerDTO> layers() {
        return this.layers
                .stream()
                .map(LayerInternal::toDTO)
                .collect(Collectors.toList());
    }

    private boolean wrongShape(final Matrix inputData) {
        return inputData.rawData()[0].length != 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NetworkDetails that = (NetworkDetails) o;
        return Objects.equals(layers, that.layers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers);
    }
}
