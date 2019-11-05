package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.ai.ml.activation.ActivationFunction;
import com.github.lipinskipawel.board.ai.ml.activation.Sigmoid;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Internal -- NetworkDetails")
class NetworkDetailsTest {

    @Nested
    @DisplayName("feedForward")
    class FeedForwardTest {

        @Test
        @DisplayName("2x4 4x1")
        void twoLayers() {
            final var weights = List.of(
                    Matrix.of(new double[][]{
                            {0.6182193251734649, -0.2593965046772255},
                            {0.7540482146768275, 0.5188320839150631},
                            {-0.6013915372014442, -0.4232078262406409},
                            {-0.1582135991835112, 0.04432622322771618}
                    }),
                    Matrix.of(new double[][]{{-0.29932505662118336, -0.3419843835491587, 0.594767035651687, -0.0413124834358114}}));
            final var biases = List.of(
                    Matrix.of(new double[]{-0.735054828696033, -0.4718856580782149, -0.7110585509335476, -0.14594530108727977}),
                    Matrix.of(new double[]{0.08394521761217977}));
            final var activations = new ArrayList<ActivationFunction>(Collections.nCopies(weights.size(), new Sigmoid()));
            final var prepared = List.of(
                    Matrix.of(new double[]{0.29007392974740037, 0.8627900098258926, 0.07030108415090096, 0.4573091378355869}),
                    Matrix.of(new double[]{0.4316746937407203})
            );
            final var networkDetails = new NetworkDetails(weights, biases, activations);

            final var matrices = networkDetails.feedForward(Matrix.of(new int[]{1, 3}));

            assertAll("Compare outputs on each layer",
                    () -> Assertions.assertThat(matrices.size()).isEqualTo(prepared.size()),
                    () -> Assertions.assertThat(matrices.get(0).rawData()).containsExactly(prepared.get(0).rawData()),
                    () -> Assertions.assertThat(matrices.get(1).rawData()).containsExactly(prepared.get(1).rawData()));
        }
    }
}