package io.lipinski.player.ai.internal;

import java.nio.file.Path;

/**
 * This is high level interface. Define basic operations
 * that can be performed on neural network.
 */
interface NeuralNetwork {


    Result predict(final Matrix data);

    Result predict(final int[] data);

    Result predict(final double[] data);

    void train(final Matrix data, final Matrix labels);

    void train(final int[] data, final int labels);

    static NeuralNetwork loadModel(final Path pathToFilename) {
        return null;
    }

}
