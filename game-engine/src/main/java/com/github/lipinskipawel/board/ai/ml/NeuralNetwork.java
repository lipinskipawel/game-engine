package com.github.lipinskipawel.board.ai.ml;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    default <T> T transform(final Transformer<T> transformer) {
        return transformer.transform(WrapperNeuralNetwork.of(this));
    }

    static Transformer<String> fromModelToString() {
        return transformer -> {
            return transformer.neuralNetworkClass().getCanonicalName() + "\n" +
                    transformer.layers()
                            .stream()
                            .map(ly -> Arrays.deepToString(ly.getWeight().rawData()) + ":" +
                                    Arrays.deepToString(ly.getBiases().rawData()) + ":" +
                                    ly.getActivationFunction().getClass().getCanonicalName())
                            .collect(Collectors.joining("\n"));
        };
    }

    static NeuralNetwork fromStringToModel(final String transformedModel) {
        final var linesToParse = transformedModel
                .lines()
                .collect(Collectors.toList());
        if (linesToParse.get(0).equals(SimpleNeuralNetwork.class.getCanonicalName())) {
            final var builder = new DeepNeuralNetworkHelper();
            linesToParse.remove(0);
            linesToParse
                    .stream()
                    .map(LayerDTO::parseStringDoubleWeightDoubleBiasClassActivationFunction)
                    .forEach(builder::addLayer);
            return builder.compile();
        }
        return null;
    }
}
