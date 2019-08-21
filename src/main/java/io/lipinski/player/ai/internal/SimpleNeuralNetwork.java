package io.lipinski.player.ai.internal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public final class SimpleNeuralNetwork implements NeuralNetwork {

    // ideally this class should have list of Matrix
    private Matrix weights_ih;
    private Matrix weights_ho;

    private Matrix biasHidden;
    private Matrix biasOutput;

    // this should be some Strategy pattern instead of Result
    // Result can be and in fact should be created on the fly
    private double learningRate;

    private List<Matrix> nodes;


    private SimpleNeuralNetwork(final int[] architecture) {
//        List<Matrix> matrices = new ArrayList<>();
//
//        for (int node = 0; node < architecture.length - 1; node++) {
//            matrices.add(new SimpleMatrix(node, node++));
//        }

        this.weights_ih = new SimpleMatrix(2, 2);
        this.weights_ho = new SimpleMatrix(1, 2);

        this.biasHidden = new SimpleMatrix(new double[][]{{0.8}, {0.5}});
        this.biasOutput = Matrix.of(new double[]{0.8});
        this.learningRate = 0.1;
        this.nodes = new ArrayList<>(architecture.length);
        randomize();
    }

    private SimpleNeuralNetwork(final int[] a,
                                final Result result) {
        this.weights_ih = new SimpleMatrix(2, 2);
        this.weights_ho = new SimpleMatrix(1, 2);

        this.biasHidden = new SimpleMatrix(new double[][]{{0.8}, {0.5}});
        this.biasOutput = Matrix.of(new double[]{0.8});
        this.learningRate = 0.1;
        randomize();
    }

    SimpleNeuralNetwork(final Matrix first,
                        final Matrix second) {
        this.weights_ih = first;
        this.weights_ho = second;

        this.biasHidden = new SimpleMatrix(new double[][]{{0.8}, {0.5}});
        this.biasOutput = new SimpleMatrix(new double[][]{{0.8}});
        this.learningRate = 0.1;
        randomize();
    }

    static NeuralNetwork factory(NeuralNetworkFactory factory) {
        return new SimpleNeuralNetwork(factory.architecture);
    }

    @Override
    public double feedForward(final Matrix data) {
        final Matrix output = feedForwardInternal(data);
        return output.rawData()[0][0];
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
        // TODO it is implemented as no batching is set up
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void train(final int[] data, final int labels) {
        final var hidden = this.weights_ih
                .multiply(Matrix.of(data))
                .add(biasHidden)
                .forEach(x -> 1 / (1 + Math.exp(-x)));

        final var outputs = this.weights_ho
                .multiply(hidden)
                .add(biasOutput)
                .forEach(x -> 1 / (1 + Math.exp(-x)));
        // end of feedforward


        final var outputErrors = Matrix.of(labels).subtract(outputs);
        final var deltaSecond = outputs.forEach(x -> x * (1 - x))
                .multiply(outputErrors)
                .forEach(x -> x * learningRate)
                .multiply(hidden.transpose());

        // update second weight !!!!!!!!!!
        this.weights_ho = this.weights_ho.add(deltaSecond);
        // update bias output
        final var intermediate = outputs.forEach(x -> x * (1 - x))
                .multiply(outputErrors)
                .forEach(x -> x * learningRate);
        this.biasOutput = this.biasOutput.add(intermediate);


        // calculate hidden errors
        final var who_t = this.weights_ho.transpose();
        final var hiddenErrors = who_t.multiply(outputErrors);

        final var deltaFirst = hidden.forEach(x -> x * (1 - x))
                .multiply(hiddenErrors) // error, THIS HAS TO BE ELEMENT WISE
                .forEach(x -> x * learningRate)
                .multiply(Matrix.of(data).transpose());

        // update first weight
        this.weights_ih = this.weights_ih.add(deltaFirst);
        this.biasHidden = this.biasHidden.add(
                hidden.forEach(x -> x * (1 - x))
                        .multiply(hiddenErrors)
                        .forEach(x -> x * learningRate)
        );
    }

    @Override
    public NeuralNetwork loadModel(final Path pathToFilename) {
        throw new RuntimeException("Not implemented yet");
    }

    private Matrix feedForwardInternal(final Matrix data) {
        if (this.weights_ih.numberOfRows() != data.numberOfRows())
            throw new InvalidInputFormatException("Shape of input " + data.numberOfRows()
                    + " must be the same as input for neural network " + this.weights_ih.numberOfRows());

        final var hidden = this.weights_ih
                .multiply(data)
                .add(biasHidden)
                .forEach(x -> 1 / (1 + Math.exp(-x)));

        return this.weights_ho
                .multiply(hidden)
                .add(biasOutput)
                .forEach(x -> 1 / (1 + Math.exp(-x)));
    }

    private void randomize() {
        this.weights_ih = this.weights_ih.forEach(x -> Math.random());
        this.weights_ho = this.weights_ho.forEach(x -> Math.random());
        this.biasHidden = this.biasHidden.forEach(x -> Math.random());
        this.biasOutput = this.biasOutput.forEach(x -> Math.random());
    }
}
