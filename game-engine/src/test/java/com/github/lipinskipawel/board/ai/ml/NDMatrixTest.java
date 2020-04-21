package com.github.lipinskipawel.board.ai.ml;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Internal -- NDMatrix")
class NDMatrixTest {

    @Nested
    @DisplayName("multiply")
    class Multiply {

        @Test
        @DisplayName("1x3 with 3x1")
        void multiply1x3With3x1() {
            final var first = new NDMatrix(new double[][]{{1, 7, 3}});
            final var second = new NDMatrix(new double[][]{{-2}, {6}, {2}});
            final var expected = new NDMatrix(new double[][]{{46}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("1x3 with 3x2")
        void multiply1x3With3x2() {
            final var first = new NDMatrix(new double[][]{{1, 2, 0}});
            final var second = new NDMatrix(new double[][]{{-2, 5}, {6, -5}, {-2, 9}});
            final var prepared = new NDMatrix(new double[][]{{10, -5}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("1x3 with 3x3")
        void multiply1x3With3x3() {
            final var first = new NDMatrix(new double[][]{{8, 1, -6}});
            final var second = new NDMatrix(new double[][]{{-2, 5, 2}, {6, 1, 3}, {-2, 9, 3}});
            final var prepared = new NDMatrix(new double[][]{{2, -13, 1}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x2 with 2x1")
        void multiply2x2With2x1() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {1, 1}});
            final var second = new NDMatrix(new double[][]{{2.0}, {6.0}});
            final var expected = new NDMatrix(new double[][]{{14}, {8}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        @Disabled
        @DisplayName("2x2 with 2x2")
        void multiply2x2With2x2() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
            final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}});
            final var prepared = new NDMatrix(new double[][]{{14, 19}, {6, 7}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x2 with 2x2")
        void multiply2x2With2x2ElementWise() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
            final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}});
            final var prepared = new NDMatrix(new double[][]{{2, 10}, {0, 7}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x2 with 2x3")
        void multiply2x2With2x3() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {1, 1}});
            final var second = new NDMatrix(new double[][]{{2, 3, 2}, {6, 0, 7}});
            final var prepared = new NDMatrix(new double[][]{{14, 3, 16}, {8, 3, 9}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x3 with 3x1")
        void multiply2x3With3x1() {
            final var first = new NDMatrix(new double[][]{{1, 2, 1}, {0, 1, 1}});
            final var second = new NDMatrix(new double[][]{{2.0}, {6.0}, {1.0}});
            final var expected = new NDMatrix(new double[][]{{15}, {7}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("2x3 with 3x2")
        void multiply2x3With3x2() {
            final var first = new NDMatrix(new double[][]{{1, 2, 8}, {-3, 1, 6}});
            final var second = new NDMatrix(new double[][]{{-2, 5}, {6, -5}, {2, 9}});
            final var prepared = new NDMatrix(new double[][]{{26, 67}, {24, 34}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("2x3 with 3x3")
        void multiply2x3With3x3() {
            final var first = new NDMatrix(new double[][]{{0, 7, 3}, {-3, 1, 6}});
            final var second = new NDMatrix(new double[][]{{-2, 5, 0}, {6, -5, -2}, {2, 3, 1}});
            final var prepared = new NDMatrix(new double[][]{{48, -26, -11}, {24, -2, 4}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(prepared);
        }

        @Test
        @DisplayName("3x3 with 3x2")
        void multiply3x3With3x2() {
            final var first = new NDMatrix(new double[][]{{1, 2, 1}, {0, 1, 0}, {2, 3, 4}});
            final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}, {1, 8}});
            final var expected = new NDMatrix(new double[][]{{15, 27}, {6, 7}, {26, 63}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("1x2 with 2x1")
        void multiply1x2With2x1() {
            final var first = new NDMatrix(new double[][]{{3, 5}});
            final var second = new NDMatrix(new double[][]{{2}, {1}});
            final var expected = new NDMatrix(new double[][]{{11}});

            final var result = first.multiply(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("add")
    class Add {

        @Test
        @DisplayName("2x2 with 2x2")
        void add2x2With2x2() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
            final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}});
            final var expected = new NDMatrix(new double[][]{{3, 7}, {6, 8}});

            final var result = first.add(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("2x2 with 1x2, error expected")
        void add2x2With1x2() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
            final var second = new NDMatrix(new double[][]{{2, 5}});

            assertThrows(
                    ArithmeticException.class,
                    () -> first.add(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("2x2 with 2x1, error expected")
        void add2x2With2x1() {
            final var first = new NDMatrix(new double[][]{{2, 2}, {3, 3}});
            final var second = new NDMatrix(new double[][]{{1}, {2.5}});

            assertThrows(
                    ArithmeticException.class,
                    () -> first.add(second),
                    "Can't add matrix's with different shapes");
        }
    }

    @Nested
    @DisplayName("subtract")
    class Subtract {

        @Test
        @DisplayName("2x2 with 2x2")
        void subtract2x2With2x2() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
            final var second = new NDMatrix(new double[][]{{2, 5}, {6, 7}});
            final var expected = new NDMatrix(new double[][]{{-1, -3}, {-6, -6}});

            final var result = first.subtract(second);

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("2x2 with 1x2, error expected")
        void subtract2x2With1x2() {
            final var first = new NDMatrix(new double[][]{{1, 2}, {0, 1}});
            final var second = new NDMatrix(new double[][]{{2, 5}});

            assertThrows(
                    ArithmeticException.class,
                    () -> first.subtract(second),
                    "Can't subtract matrix's with different shapes");
        }

        @Test
        @DisplayName("2x2 with 2x1, error expected")
        void subtract2x2With2x1() {
            final var first = new NDMatrix(new double[][]{{2, 2}, {3, 3}});
            final var second = new NDMatrix(new double[][]{{1}, {2.5}});

            assertThrows(
                    ArithmeticException.class,
                    () -> first.subtract(second),
                    "Can't subtract matrix's with different shapes");
        }
    }

    @Nested
    @DisplayName("transpose")
    class Transpose {

        @Test
        @DisplayName("2x2 into 2x2")
        void transpose() {
            final var matrix = new NDMatrix(new double[][]{{2, 3}, {1, 4}});
            final var expected = new NDMatrix(new double[][]{{2, 1}, {3, 4}});

            Assertions.assertThat(matrix.transpose()).isEqualTo(expected);
        }

        @Test
        @DisplayName("1x3 into 3x1")
        void transposeSecond() {
            final var matrix = new NDMatrix(new double[][]{{2, 3, 4}});
            final var expected = new NDMatrix(new double[][]{{2}, {3}, {4}});

            Assertions.assertThat(matrix.transpose()).isEqualTo(expected);
        }

        @Test
        @DisplayName("3x1 into 1x3")
        void transposeThird() {
            final var matrix = new NDMatrix(new double[][]{{2}, {3}, {4}});
            final var expected = new NDMatrix(new double[][]{{2, 3, 4}});

            Assertions.assertThat(matrix.transpose()).isEqualTo(expected);
        }
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

    @Nested
    @DisplayName("backport - forEach, numberOfRows")
    class BackportMatrix {

        @Test
        @DisplayName("add 2 forEach element")
        void add2ForEachElement() {
            final var first = new NDMatrix(new double[][]{{2, 3}, {4, 5}});
            final var expected = new NDMatrix(new double[][]{{4, 5}, {6, 7}});

            final var actual = first.forEach(element -> element + 2);

            Assertions.assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("numberOfRows")
        void numberOfRows() {
            Assertions
                    .assertThat(new NDMatrix(new double[][]{{0}, {1}}).numberOfRows())
                    .isEqualTo(2);
        }

        @Test
        @DisplayName("rawData")
        void rawDataTest() {
            final var ndMatrix = new NDMatrix(new double[][]{{1, 2}, {3, 4}});

            Assertions.assertThat(ndMatrix.rawData()).containsSequence(new double[]{1, 2}, new double[]{3, 4});
        }
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