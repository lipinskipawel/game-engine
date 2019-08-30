package io.lipinski.player.ai.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static io.lipinski.player.ai.internal.Activation.SIGMOID;
import static org.assertj.core.api.Java6Assertions.offset;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Neural network")
class NeuralNetworkTest {

    /**
     * This test has high margin of error due to illness of its nature.
     */
    @Test
    @DisplayName("Silly neural network just for test domain and API usage")
    void sillyTest() {
        final var trainingDataset = new ArrayList<int[][]>();
        trainingDataset.add(new int[][]{new int[]{1, 1}, new int[]{0}});
        trainingDataset.add(new int[][]{new int[]{1, 0}, new int[]{1}});
        trainingDataset.add(new int[][]{new int[]{0, 0}, new int[]{0}});
        trainingDataset.add(new int[][]{new int[]{0, 1}, new int[]{1}});

        final var model = new NeuralNetworkFactory.Builder()
                .addLayer(new Layer(2))
                .addLayer(new Layer(4))
                .addLayer(new Layer(1))
                .output(double[].class)
                .activationOnLayers(SIGMOID)
                .compile()
                .noBatching()
                .build();

        for (int i = 0; i < 30_000; i++) {
            var pick = new Random().nextInt(4);
            model.train(trainingDataset.get(pick)[0], trainingDataset.get(pick)[1][0]);
        }

        final var output1 = (double) model.predict(new int[]{1, 0}).getBestValue();
        final var output2 = (double) model.predict(new int[]{1, 1}).getBestValue();
        final var output3 = (double) model.predict(new int[]{0, 0}).getBestValue();
        final var output4 = (double) model.predict(new int[]{0, 1}).getBestValue();

        assertAll("Assert all OXR operations",
                () -> Assertions.assertThat(output1).isCloseTo(1.0, withPercentage(30)),
                () -> Assertions.assertThat(output2).isCloseTo(0.0, offset(0.30)),
                () -> Assertions.assertThat(output3).isCloseTo(0.0, offset(0.30)),
                () -> Assertions.assertThat(output4).isCloseTo(1.0, withPercentage(30))
        );
    }
}
