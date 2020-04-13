package com.github.lipinskipawel.board.ai.ml;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Internal -- NDMatrix")
class NDMatrixTest {

    @Test
    @DisplayName("multiply")
    void multiply() {
        final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
        final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}});
        final var prepared = new NDMatrix(new double[][]{{14, 19}, {6, 7}});

        final var result = first.multiply(second);

        Assertions.assertThat(result).isEqualTo(prepared);
    }

    @Nested
    @DisplayName("fromCModel")
    class FromCModelToNDMatrix {

        @Test
        @DisplayName("2x1")
        void shouldCreateNDMatrixFrom2x1() {
            final var prepared = new NDMatrix(new double[][]{{1}, {3}});

            final var result = NDMatrix.fromCModel(new double[]{1, 3}, 2);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x2")
        void shouldCreateNDMatrixFrom2x2() {
            final var prepared = new NDMatrix(new double[][]{{1, 2}, {3, 4}});

            final var result = NDMatrix.fromCModel(new double[]{1, 2, 3, 4}, 2);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x3")
        void shouldCreateNDMatrixFrom2x3() {
            final var prepared = new NDMatrix(new double[][]{{1, 2, 7}, {3, 8, 4}});

            final var result = NDMatrix.fromCModel(new double[]{1, 2, 7, 3, 8, 4}, 2);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("1x1")
        void shouldCreateNDMatrixFrom1x1() {
            final var prepared = new NDMatrix(new double[][]{{1}});

            final var result = NDMatrix.fromCModel(new double[]{1}, 1);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("1x2")
        void shouldCreateNDMatrixFrom1x2() {
            final var prepared = new NDMatrix(new double[][]{{9, 2}});

            final var result = NDMatrix.fromCModel(new double[]{9, 2}, 1);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("1x3")
        void shouldCreateNDMatrixFrom1x3() {
            final var prepared = new NDMatrix(new double[][]{{1, 2, 7}});

            final var result = NDMatrix.fromCModel(new double[]{1, 2, 7}, 1);

            Assertions.assertThat(result).isEqualTo(prepared);
        }
    }

    @Test
    void rawDataTest() {
        final var ndMatrix = new NDMatrix(new double[][]{{1, 2}, {3, 4}});

        Assertions.assertThat(ndMatrix.rawData()).containsSequence(new double[]{1, 2}, new double[]{3, 4});
    }

    @Test
    @DisplayName("should compute cModel and fModel")
    void cModelAndFModel() {
        final var ndMatrix = new NDMatrix(new double[][]{{1, 2, 3}, {6, 7, 8}});
        final var preparedCModel = new double[]{1, 2, 3, 6, 7, 8};
        final var preparedFModel = new double[]{1, 6, 2, 7, 3, 8};

        final var cModel = ndMatrix.cModel();
        final var fModel = ndMatrix.fModel();

        assertAll("cModel or fModel does not work properly",
                () -> Assertions.assertThat(cModel).containsSequence(preparedCModel),
                () -> Assertions.assertThat(fModel).containsSequence(preparedFModel));
    }

    @Test
    @DisplayName("should create NDMatrix from cModel")
    void shouldCreateNDMatrixFromCModel() {
        final var preparedMatrix = new NDMatrix(new double[][]{{1, 0}, {2, 3}});

        final var cModel = NDMatrix.fromCModel(new double[]{1, 0, 2, 3}, 2);

        Assertions.assertThat(cModel).isEqualTo(preparedMatrix);
    }
}