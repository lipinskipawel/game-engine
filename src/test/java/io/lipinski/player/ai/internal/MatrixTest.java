package io.lipinski.player.ai.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("SimpleMatrix operations")
class MatrixTest {


    @Nested
    @DisplayName("Multiply operations")
    class Multiply {

        @Test
        @DisplayName("Multiply 2x2 with 2x1 matrix")
        void multiply2x2With2x1() {
            //Given:
            final var first = new SimpleMatrix(new double[][]{
                    {1, 2},
                    {1, 1}
            });
            final var second = Matrix.of(new double[]{2.0, 6.0});
            final var expected = new double[][]{
                    {14},
                    {8}
            };

            //When:
            final var result = first.multiply(second);

            //Then:
            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("Multiply 3x3 with 3x2 matrix")
        void multiply() {
            //Given:
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

            //When:
            final var result = first.multiply(second);

            //Then:
            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("Multiply 1x2 with 2x1 matrix")
        void multiplyDifferentShape() {
            //Given:
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

            //When:
            final var result = first.multiply(second);

            //Then:
            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("Should return 1x1 when multiply 1x2 with 2x1 matrix")
        void multiplyDifferentShapeGiveOneValueForOutput() {
            //Given:
            final var first = new SimpleMatrix(new double[][]{
                    {3, 5}
            });
            final var second = Matrix.of(new double[]{2, 1});

            //When:
            final var result = first.multiply(second);

            //Then:
            assertAll(
                    "In 2d array should be only one value",
                    () -> Assertions.assertThat(result.rawData().length).isEqualTo(1),
                    () -> Assertions.assertThat(result.rawData()[0].length).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("Throw exception when trying to multiply 2x1 with 2x2 matrix")
        void exception() {
            //Given:
            final var first = new SimpleMatrix(new double[][]{
                    {1},
                    {2}
            });
            final var second = Matrix.of(new double[][]{
                    {2, 3},
                    {1, 1}
            });

            //When:
            assertThrows(
                    ArithmeticException.class,
                    () -> first.multiply(second),
                    "Can't multiply matrix's with different ratio rows to columns"
            );
        }

        @Test
        @DisplayName("Use element wise multiplication instead of dot product. 2x1 and 2x1")
        void elementWise() {
            //Given:
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

            //When:
            final var actual = first.multiply(second);

            //Then:
            Assertions.assertThat(actual.rawData()).containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("Completely different dimension")
        void exceptionWhenBadDimension() {
            //Given:
            final var first = Matrix.of(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
            final var second = new SimpleMatrix(new double[][]{
                    {2, 5},
                    {5, 6}
            });

            //When:
            assertThrows(
                    ArithmeticException.class,
                    () -> first.multiply(second),
                    "Can't add matrix's with different shapes");
        }
    }

    @Nested
    @DisplayName("Add operations")
    class Add {

        @Test
        @DisplayName("Add element wise 2x2 with 2x2")
        void add() {
            //Given:
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

            //When:
            final var result = first.add(second);

            //Then:
            Assertions.assertThat(result.rawData()).containsExactly(expected);
        }

        @Test
        @DisplayName("Add element wise 2x2 with 1x2")
        void addNotTheSameShape() {
            //Given:
            final var first = new SimpleMatrix(new double[][]{
                    {1, 2}, {0, 1}
            });
            final var second = new SimpleMatrix(new double[][]{
                    {2, 5}
            });

            //When:
            assertThrows(
                    ArithmeticException.class,
                    () -> first.add(second),
                    "Can't add matrix's with different shapes");
        }

        @Test
        @DisplayName("Add 2x2 with 2x1, expect error")
        void add2x2With2x1() {
            //Given:
            final var first = Matrix.of(new double[][]{
                    {2, 2},
                    {3, 3}
            });
            final var second = Matrix.of(new double[][]{
                    {1},
                    {2.5}
            });

            //When:
            assertThrows(
                    ArithmeticException.class,
                    () -> first.add(second),
                    "Can't add matrix's with different shapes");
        }
    }

    @Nested
    @DisplayName("Subtract operations")
    class Subtract {

        @Test
        @DisplayName("Subtract 2x2 with 2x2")
        void subtract2x2With2x2() {
            //Given:
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

            //When:
            final var actual = first.subtract(second);

            //Then:
            Assertions.assertThat(actual.rawData()).containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("Subtract 2x2 with 2x1, expect error")
        void subtract2x2With2x1() {
            //Given:
            final var first = Matrix.of(new double[][]{
                    {2, 2},
                    {3, 3}
            });
            final var second = Matrix.of(new double[][]{
                    {1},
                    {2.5}
            });

            //When:
            assertThrows(
                    ArithmeticException.class,
                    () -> first.subtract(second),
                    "Can't add matrix's with different shapes");
        }
    }

    @Nested
    @DisplayName("Transpose operations")
    class Transpose {

        @Test
        @DisplayName("Transpose 2x2")
        void transpose() {
            //Given:
            final var matrix = new SimpleMatrix(new double[][]{
                    {2, 3},
                    {1, 4}
            });
            final var expected = new SimpleMatrix(new double[][]{
                    {2, 1},
                    {3, 4}
            });

            //When:
            Assertions.assertThat(matrix.transpose().rawData())
                    .containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("Transpose 1x3")
        void transposeSecond() {
            //Given:
            final var matrix = new SimpleMatrix(new double[][]{
                    {2, 3, 4}
            });
            final var expected = Matrix.of(new int[]{2, 3, 4});

            //When:
            Assertions.assertThat(matrix.transpose().rawData())
                    .containsExactly(expected.rawData());
        }

        @Test
        @DisplayName("Transpose 3x1")
        void transposeThird() {
            //Given:
            final var matrix = Matrix.of(new double[][]{
                    {2},
                    {3},
                    {4}
            });
            final var expected = Matrix.of(new int[][]{{2, 3, 4}});

            //When:
            Assertions.assertThat(matrix.transpose().rawData())
                    .containsExactly(expected.rawData());
        }
    }
}
