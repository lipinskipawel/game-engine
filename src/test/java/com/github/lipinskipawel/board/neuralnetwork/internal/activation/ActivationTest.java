package com.github.lipinskipawel.board.neuralnetwork.internal.activation;

import com.github.lipinskipawel.board.neuralnetwork.internal.Matrix;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("SPI -- Activation Function")
class ActivationTest {

    @Nested
    @DisplayName("SOFTMAX")
    class SoftmaxTest {

        @Test
        @DisplayName("Compute, 3 numbers")
        void compute() {
            final var inputs = new double[]{2.0, 1.0, 0.1};
            final var expected = new double[]{0.6590011388859679, 0.24243297070471392, 0.09856589040931818};

            final var softmax = new Softmax();
            final var compute = softmax.compute(Matrix.of(inputs));
            final var doubles = takeFirstColumn(compute);

            Assertions.assertThat(doubles).containsExactly(expected);
        }

        @Test
        @DisplayName("Compute, 4 numbers")
        void compute2() {
            final var inputs = new double[]{5, 2, -1, 3};
            final var expected = new double[]{0.842033572397458, 0.041922383036988954, 0.002087192550406349, 0.1139568520151468};

            final var softmax = new Softmax();
            final var compute = softmax.compute(Matrix.of(inputs));
            final var doubles = takeFirstColumn(compute);

            Assertions.assertThat(doubles).containsExactly(expected);
        }

        @Test
        @DisplayName("Derivative, 3 numbers")
        void derivative() {
            final var inputs = new double[]{2.0, 1.0, 0.12};
            final var expected = new double[]{-2.0, 0.0, 0.1056};

            final var softmax = new Softmax();
            final var compute = softmax.derivative(Matrix.of(inputs));
            final var doubles = takeFirstColumn(compute);

            Assertions.assertThat(doubles).containsExactly(expected);
        }
    }

    private double[] takeFirstColumn(final Matrix compute) {
        return Arrays.stream(compute.rawData())
                .mapToDouble(row -> row[0])
                .toArray();
    }
}
