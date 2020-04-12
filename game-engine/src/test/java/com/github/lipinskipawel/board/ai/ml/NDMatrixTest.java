package com.github.lipinskipawel.board.ai.ml;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Internal -- NDMatrix")
class NDMatrixTest {

    @Test
    @Disabled
    void multiply() {
        final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
        final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}});
        final var prepared = new NDMatrix(new double[][]{{14, 19}, {6, 7}});

        final var result = first.multiply(second);

        Assertions.assertThat(result).isEqualTo(prepared);
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
}