package io.lipinski.player.ai.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Simple neural network operations")
class SimpleNeuralNetworkTest {

    @Nested
    @DisplayName("3 layers")
    class ThreeLayers {

        @Test
        @DisplayName("backpropagation")
        void backpropagation() {
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
            final var model = new SimpleNeuralNetwork(weights, biases, new Sigmoid(), 0.1);

            model.train(new int[]{1, 0}, 1);

            final var expected_weights_ih = Matrix.of(new double[][]{
                    {0.6143074578636372, -0.2593965046772255},
                    {0.7496572814331723, 0.5188320839150631},
                    {-0.596041394240618, -0.4232078262406409},
                    {-0.15868029978024437, 0.04432622322771618}
            });
            final var expected_weights_ho = Matrix.of(new double[][]{{-0.2930509470154622, -0.334387661591508, 0.5975931328495021, -0.0356551341881891}});
            final var expected_bias_h = Matrix.of(new double[]{-0.7389666960058607, -0.47627659132187006, -0.7057084079727214, -0.14641200168401294});
            final var expected_bias_o = Matrix.of(new double[]{0.09727101565638073});


            final var simple = (SimpleNeuralNetwork) model;
            final var after_weights_ih = simple.nodes.get(0);
            final var after_weights_ho = simple.nodes.get(1);
            final var after_biasHidden = simple.biases.get(0);
            final var after_biasOutput = simple.biases.get(1);
            Assertions.assertThat(after_weights_ih.rawData()).containsExactly(expected_weights_ih.rawData());
            Assertions.assertThat(after_weights_ho.rawData()).containsExactly(expected_weights_ho.rawData());
            Assertions.assertThat(after_biasHidden.rawData()).containsExactly(expected_bias_h.rawData());
            Assertions.assertThat(after_biasOutput.rawData()).containsExactly(expected_bias_o.rawData());
        }
    }
}
