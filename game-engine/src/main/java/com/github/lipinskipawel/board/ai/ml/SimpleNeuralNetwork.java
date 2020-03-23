package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.ai.ml.activation.ActivationFunction;
import com.github.lipinskipawel.board.ai.ml.lossfunction.LossFunction;
import com.github.lipinskipawel.board.ai.ml.lossfunction.MSE;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class SimpleNeuralNetwork implements NeuralNetwork {

    private final NetworkDetails networkDetails;

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
        try {
            final Matrix output = feedForwardInternal(data);
            final var doubles = Arrays.stream(output
                    .rawData())
                    .mapToDouble(raw -> raw[0])
                    .toArray();
            return Result.of(Arrays.stream(doubles).boxed().toArray(Double[]::new));
        } catch (ArithmeticException ee) {
            throw new RuntimeException("Prediction failed due to wrong data shape: " + data.rawData().length, ee);
        }
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
        try {
            this.networkDetails
                    .backpropagation(data, labels, lossFunction, learningRate);
        } catch (ArithmeticException ee) {
            throw new RuntimeException("Prediction failed due to wrong data shape: " + data.rawData().length, ee);
        }
    }

    @Override
    public void train(final int[] data, final int labels) {
        train(Matrix.of(data), Matrix.of(labels));
    }

    private Matrix feedForwardInternal(final Matrix data) {
        final var outputs = this.networkDetails.feedForward(data);
        return outputs.get(outputs.size() - 1);
    }

    private void randomize() {
        this.networkDetails.randomize();
    }

    NetworkDetails networkDetails() {
        return new NetworkDetails(networkDetails);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimpleNeuralNetwork that = (SimpleNeuralNetwork) o;
        return Double.compare(that.learningRate, learningRate) == 0 &&
                Objects.equals(networkDetails, that.networkDetails) &&
                Objects.equals(lossFunction, that.lossFunction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(networkDetails, learningRate, lossFunction);
    }
}
