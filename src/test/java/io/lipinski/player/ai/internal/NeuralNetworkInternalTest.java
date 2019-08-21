package io.lipinski.player.ai.internal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Neural Network test")
class NeuralNetworkInternalTest {


    @Nested
    @DisplayName("FeedForward operation")
    class FeedForward {

        @Test
        @DisplayName("Throws InvalidInputFormatException when provided improper input shape to neural network")
        void exception() {
            //Given:
            final var model = new SimpleNeuralNetwork(
                    new SimpleMatrix(new double[][]{
                            {0.5, 0.5},
                            {0.5, 0.5}
                    }),
                    new SimpleMatrix(new double[][]{
                            {0.2, 0.2}
                    })
            );

            //When:
            assertThrows(
                    InvalidInputFormatException.class,
                    () -> model.feedForward(new SimpleMatrix(new double[][]{{1, 0}})),
                    "Input to neural network has to be that same as input layer");
        }
    }
}
