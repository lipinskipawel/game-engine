package io.lipinski.player.ai.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.offset;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Neural network")
class NeuralNetworkTest {

    /**
     * This test has high margin of error due to illness of its nature.
     * Despite high threshold in assertions this test can still be flake.
     */
    @Disabled
    @RepeatedTest(5)
    @DisplayName("Silly neural network just for test domain and API usage")
    void sillyTest() {
        final var trainingDataset = new HashMap<int[], int[]>();
        trainingDataset.put(new int[]{1, 1}, new int[]{0});
        trainingDataset.put(new int[]{1, 0}, new int[]{1});
        trainingDataset.put(new int[]{0, 1}, new int[]{1});
        trainingDataset.put(new int[]{0, 0}, new int[]{0});

        final var model = new NeuralNetworkFactory.Builder()
                .addLayer(new Layer(2, Activation.SIGMOID))
                .addLayer(new Layer(2, Activation.SIGMOID))
                .addLayer(new Layer(1, Activation.SIGMOID))
                .output(double[].class)
                .compile()
                .noBatching()
                .build();

        var keys = toList(trainingDataset.keySet());
        for (int i = 0; i < 3_000; i++) {
            for (int[] key : keys) {
                model.train(key, trainingDataset.get(key)[0]);
            }
            Collections.shuffle(keys);
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

    private static List<int[]> toList(Set<int[]> set) {
        return new ArrayList<>(set);
    }
}
