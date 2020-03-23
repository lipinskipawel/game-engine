package com.github.lipinskipawel.board.ai.ml;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("API -- Matrix")
class MatrixTest {

    @Nested
    @DisplayName("Multiply, the dot product")
    class Multiply {

        @Test
        @DisplayName("2x2 with 2x1")
        void multiply2x2With2x1() {
            final var first = new SimpleMatrix(new double[][]{
                    {1, 2},
                    {1, 1}
            });
            final var second = Matrix.of(new double[]{2.0, 6.0});
            final var expected = new double[][]{
                    {14},
                    {8}
            };

            final var result = first.multiply(second);

            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("3x3 with 3x2")
        void multiply() {
            final var first = new SimpleMatrix(new double[][]{
                    {1, 2, 1},
                    {0, 1, 0},
                    {2, 3, 4}
            });
            final var second = Matrix.of(new int[][]{
                    {2, 5},
                    {6, 7},
                    {1, 8}
            });
            final var expected = new double[][]{
                    {15, 27},
                    {6, 7},
                    {26, 63}
            };

            final var result = first.multiply(second);

            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("1x2 with 2x1")
        void multiplyDifferentShape() {
            final var first = new SimpleMatrix(new double[][]{
                    {3, 5}
            });
            final var second = Matrix.of(new int[][]{
                    {2},
                    {1}
            });
            final var expected = new double[][]{
                    {11}
            };

            final var result = first.multiply(second);

            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("1x2 with 2x1 return 1x1")
        void multiplyDifferentShapeGiveOneValueForOutput() {
            final var first = new SimpleMatrix(new double[][]{
                    {3, 5}
            });
            final var second = Matrix.of(new double[]{2, 1});

            final var result = first.multiply(second);

            assertAll(
                    "In 2d array should be only one value",
                    () -> Assertions.assertThat(result.rawData().length).isEqualTo(1),
                    () -> Assertions.assertThat(result.rawData()[0].length).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("2x1 with 2x2, error expected")
        void exception() {
            final var first = new SimpleMatrix(new double[][]{
                    {1},
                    {2}
            });
            final var second = Matrix.of(new double[][]{
                    {2, 3},
                    {1, 1}
            });

            assertThrows(
                    ArithmeticException.class,
                    () -> first.multiply(second),
                    "Can't multiply matrix's with different ratio rows to columns"
            );
        }

        @Test
        @DisplayName("Element-wise multiplication 2x1 and 2x1")
        void elementWise() {
            final var first = new SimpleMatrix(new double[][]{
                    {4},
                    {8}
            });
            final var second = Matrix.of(new double[][]{
                    {2},
                    {8}
            });
            final var expected = Matrix.of(new double[][]{
                    {8},
                    {64}
            });

            final var actual = first.multiply(second);

            Assertions.assertThat(actual.rawData()).containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("10x1 with 2x2, error expected")
        void exceptionWhenBadDimension() {
            final var first = Matrix.of(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
            final var second = new SimpleMatrix(new double[][]{
                    {2, 5},
                    {5, 6}
            });

            assertThrows(
                    ArithmeticException.class,
                    () -> first.multiply(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("Immutability check")
        void immutability() {
            final var first = Matrix.of(new double[]{2.3, 3.3});

            final var multiply = first.multiply(Matrix.of(new double[]{1, 1}));

            Assertions.assertThat(first).isNotSameAs(multiply);
            Assertions.assertThat(first.rawData().length).isEqualTo(2);
            Assertions.assertThat(first.rawData()[0].length).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Add")
    class Add {

        @Test
        @DisplayName("2x2 with 2x2")
        void add() {
            final var first = new SimpleMatrix(new double[][]{
                    {1, 2},
                    {0, 1}
            });
            final var second = Matrix.of(new double[][]{
                    {2, 5},
                    {6, 7}
            });
            final var expected = new double[][]{
                    {3, 7},
                    {6, 8}
            };

            final var result = first.add(second);

            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("2x2 with 1x2, error expected")
        void addNotTheSameShape() {
            final var first = new SimpleMatrix(new double[][]{
                    {1, 2}, {0, 1}
            });
            final var second = new SimpleMatrix(new double[][]{
                    {2, 5}
            });

            assertThrows(
                    ArithmeticException.class,
                    () -> first.add(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("2x2 with 2x1, error expected")
        void add2x2With2x1() {
            final var first = Matrix.of(new double[][]{
                    {2, 2},
                    {3, 3}
            });
            final var second = Matrix.of(new double[][]{
                    {1},
                    {2.5}
            });

            assertThrows(
                    ArithmeticException.class,
                    () -> first.add(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("Immutability check")
        void immutability() {
            final var first = Matrix.of(new int[][]{{1, 1}, {2, 3}});

            final var add = first.add(Matrix.of(new int[][]{{3, 4}, {7, 7}}));

            Assertions.assertThat(first).isNotSameAs(add);
        }
    }

    @Nested
    @DisplayName("Subtract")
    class Subtract {

        @Test
        @DisplayName("2x2 with 2x2")
        void subtract2x2With2x2() {
            final var first = Matrix.of(new double[][]{
                    {2, 2},
                    {3, 3}
            });
            final var second = Matrix.of(new double[][]{
                    {1, 0.5},
                    {2.5, 2}
            });
            final var expected = Matrix.of(new double[][]{
                    {1, 1.5},
                    {0.5, 1}
            });

            final var actual = first.subtract(second);

            Assertions.assertThat(actual.rawData()).containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("2x2 with 2x1, error expected")
        void subtract2x2With2x1() {
            final var first = Matrix.of(new double[][]{
                    {2, 2},
                    {3, 3}
            });
            final var second = Matrix.of(new double[][]{
                    {1},
                    {2.5}
            });

            assertThrows(
                    ArithmeticException.class,
                    () -> first.subtract(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("2x2 with 1x2, error expected")
        void subtract2x2With1x2() {
            final var first = Matrix.of(new double[][]{
                    {2, 2},
                    {3, 3}
            });
            final var second = Matrix.of(new double[][]{
                    {1, 3}
            });

            assertThrows(
                    ArithmeticException.class,
                    () -> first.subtract(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("Immutability check")
        void immutability() {
            final var first = Matrix.of(2.0);

            final var subtract = first.subtract(Matrix.of(1.0));

            Assertions.assertThat(first).isNotSameAs(subtract);
        }
    }

    @Nested
    @DisplayName("Transpose")
    class Transpose {

        @Test
        @DisplayName("2x2 into 2x2")
        void transpose() {
            final var matrix = new SimpleMatrix(new double[][]{
                    {2, 3},
                    {1, 4}
            });
            final var expected = new SimpleMatrix(new double[][]{
                    {2, 1},
                    {3, 4}
            });

            Assertions.assertThat(matrix.transpose().rawData())
                    .containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("1x3 into 3x1")
        void transposeSecond() {
            final var matrix = new SimpleMatrix(new double[][]{
                    {2, 3, 4}
            });
            final var expected = Matrix.of(new int[]{2, 3, 4});

            Assertions.assertThat(matrix.transpose().rawData())
                    .containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("3x1 into 1x3")
        void transposeThird() {
            final var matrix = Matrix.of(new double[][]{
                    {2},
                    {3},
                    {4}
            });
            final var expected = Matrix.of(new int[][]{{2, 3, 4}});

            Assertions.assertThat(matrix.transpose().rawData())
                    .containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("Immutability check")
        void immutability() {
            final var first = Matrix.of(new int[]{1, 2, 3});

            final var transpose = first.transpose().transpose();

            Assertions.assertThat(first).isNotSameAs(transpose);
        }
    }

    @Nested
    @DisplayName("Foreach")
    class ForEach {

        @Test
        @DisplayName("Map Matrix with only one element")
        void map() {
            final var first = Matrix.of(3.3);
            final var expected = Matrix.of(3.0);

            final var matrix = first.forEach(x -> x - 0.3);

            Assertions.assertThat(matrix.rawData()).isEqualTo(expected.rawData());
        }

        @Test
        @DisplayName("Immutability check")
        void immutability() {
            final var first = Matrix.of(new double[]{3.3, 2});

            final var matrix = first.forEach(x -> x * 2);

            Assertions.assertThat(first).isNotSameAs(matrix);
        }
    }
}
