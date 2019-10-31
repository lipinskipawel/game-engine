package com.github.lipinskipawel.board.neuralnetwork.internal;

import com.github.lipinskipawel.board.neuralnetwork.internal.activation.ActivationFunction;
import com.github.lipinskipawel.board.neuralnetwork.internal.lossfunction.LossFunction;
import com.github.lipinskipawel.board.neuralnetwork.internal.lossfunction.MSE;

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
            this.networkDetails.backpropagation(data, labels, lossFunction, learningRate);
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
}
