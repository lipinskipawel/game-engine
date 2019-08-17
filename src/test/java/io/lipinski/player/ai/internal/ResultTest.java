package io.lipinski.player.ai.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Result output of neural network")
class ResultTest {

    @Test
    @DisplayName("Basic best match test")
    void oneOutput() {
        final var res = Result.of(List.of(3.0).toArray(new Double[1]));

        final var actual = res.getBestMatch();

        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("Find best match across five distinct numbers")
    void fiveNumberTestDistinct() {
        final var res = Result.of(
                List.of(1, 2, 1, 5, 2).toArray(Integer[]::new));

        final var actual = res.getBestMatch();

        Assertions.assertThat(actual).isEqualTo(3);
    }

    @Test
    @DisplayName("Find best match across five numbers")
    void fiveNumberTest() {
        final var res = Result.of(
                List.of(5, 2, 1, 5, 2).toArray(Integer[]::new));

        final var actual = res.getBestMatch();

        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("Find best Value across 8 distinct numbers")
    void findBestValueDistinct() {
        final var res = Result.of(
                List.of(1, 2, 1, 5, 7, 2, 1, 1).toArray(Integer[]::new));

        final var actual = res.getBestValue();

        Assertions.assertThat(actual).isEqualTo(7);
    }

    @Test
    @DisplayName("Find best Value across 8 numbers")
    void findBestValueNoDistinct() {
        final var res = Result.of(
                List.of(1, 2, 7, 5, 7, 2, -10, 1).toArray(Integer[]::new));

        final var actual = res.getBestValue();

        Assertions.assertThat(actual).isEqualTo(7);
    }

}