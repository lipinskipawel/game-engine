package io.lipinski.board.neuralnetwork.internal;

import io.lipinski.board.neuralnetwork.internal.activation.ActivationFunction;
import io.lipinski.board.neuralnetwork.internal.lossfunction.LossFunction;
import io.lipinski.board.neuralnetwork.internal.lossfunction.MSE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SimpleNeuralNetwork implements NeuralNetwork {

    final NetworkDetails networkDetails;

    private final double learningRate;
    private final LossFunction lossFunction;

    private SimpleNeuralNetwork(final NetworkDetails networkDetails,
                                final double learningRate,
                                final LossFunction lossFunction) {
        this.networkDetails = networkDetails;
        this.learningRate = learningRate;
        this.lossFunction = lossFunction;
        randomize();
    }

    SimpleNeuralNetwork(final List<Matrix> weights,
                        final List<Matrix> biases,
                        final List<ActivationFunction> activations,
                        final double learningRate) {
        this.networkDetails = new NetworkDetails(weights, biases, activations);
        this.learningRate = learningRate;
        this.lossFunction = new MSE();
    }

    static NeuralNetwork factory(final DeepNeuralNetwork factory) {
        return new SimpleNeuralNetwork(factory.networkDetails, factory.learningRate, factory.lossFunction);
    }

    @Override
    public Result predict(final Matrix data) {
        final Matrix output = feedForwardInternal(data);
        final var doubles = Arrays.stream(output
                .rawData())
                .mapToDouble(raw -> raw[0])
                .toArray();
        return Result.of(Arrays.stream(doubles).boxed().toArray(Double[]::new));
    }

    @Override
    public Result predict(final int[] data) {
        return predict(Matrix.of(data));
    }

    @Override
    public Result predict(final double[] data) {
        return predict(Matrix.of(data));
    }

    @Override
    public void train(final Matrix data, final Matrix labels) {
        for (var temp : data.transpose().rawData()) {
            var oneColumnOfData = Matrix.of(temp);
            final var outputOnLayers = new ArrayList<Matrix>();

            for (int i = 0; i < networkDetails.nodes.size(); i++) {
                final var compute = computeOutputOnLayer(i, oneColumnOfData, outputOnLayers);

                outputOnLayers.add(compute);
            }
            // var computedErrors = outputErrors, hiddenErrors, secondHidden....
            var computedErrors = new ArrayList<Matrix>();
            var valuesToDeltas = computeDeltas(oneColumnOfData, outputOnLayers);

            var j = 0;
            for (int i = networkDetails.nodes.size() - 1; i >= 0; i--) {
                final var outputErrorComputed = computeError(i, labels, outputOnLayers.get(outputOnLayers.size() - 1), computedErrors, j - 1);
                computedErrors.add(outputErrorComputed);

                final var matrix = outputOnLayers.get(i);
                final var gradient = networkDetails.activations.get(i).derivative(matrix)
                        .multiply(computedErrors.get(j))
                        .forEach(x -> x * learningRate);
                final var deltaaa = gradient.multiply(valuesToDeltas.get(i));

                networkDetails.nodes.set(i, networkDetails.nodes.get(i).add(deltaaa));
                networkDetails.biases.set(i, networkDetails.biases.get(i).add(gradient));
                j++;
            }
        }
    }

    @Override
    public void train(final int[] data, final int labels) {
        train(Matrix.of(data), Matrix.of(labels));
    }

    private Matrix computeError(final int index,
                                final Matrix labels,
                                final Matrix outputs,
                                final List<Matrix> computedErrors,
                                final int j) {
        if (index == networkDetails.nodes.size() - 1) {
            return lossFunction.compute(labels, outputs);
        }
        final var who_t = networkDetails.nodes.get(index + 1).transpose(); // 2x4
        return who_t.multiply(computedErrors.get(j));
    }

    // data.T(input data), hidden.T(first output from FF), secondHidden.T(second output from FF)
    private List<Matrix> computeDeltas(final Matrix data,
                                       final List<Matrix> outputOnLayers) {
        final var deltas = new ArrayList<Matrix>();
        deltas.add(data.transpose());
        for (Matrix some : outputOnLayers) {
            deltas.add(some.transpose());
        }
        return deltas;
    }

    private Matrix feedForwardInternal(final Matrix data) {
        if (networkDetails.nodes.get(0).rawData()[0].length != data.numberOfRows())
            throw new InvalidInputFormatException("Shape of input " + data.numberOfRows()
                    + " must be the same as input for neural network " + networkDetails.nodes.get(0).numberOfRows());
        final var outputOnLayers = new ArrayList<Matrix>();

        for (int i = 0; i < networkDetails.nodes.size(); i++) {
            var compute = computeOutputOnLayer(i, data, outputOnLayers);
            outputOnLayers.add(compute);
        }
        return outputOnLayers.get(outputOnLayers.size() - 1);
    }

    private Matrix computeOutputOnLayer(final int i,
                                        final Matrix oneColumnOfData,
                                        final List<Matrix> outputOnLayers) {
        final var weight = networkDetails.nodes.get(i);
        final var bias = networkDetails.biases.get(i);
        var tempData = oneColumnOfData;
        if (i != 0)
            tempData = outputOnLayers.get(i - 1);
        return networkDetails.activations.get(i)
                .compute(weight
                        .multiply(tempData)
                        .add(bias)
                );
    }

    private void randomize() {
        for (int i = 0; i < networkDetails.nodes.size(); i++) {
            final Func func = x -> Math.random() - .5;
            networkDetails.nodes.set(i, networkDetails.nodes.get(i).forEach(func));
            networkDetails.biases.set(i, networkDetails.biases.get(i).forEach(func));
        }
    }
}
