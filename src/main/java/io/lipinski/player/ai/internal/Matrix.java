package io.lipinski.player.ai.internal;

interface Matrix {

    double[][] rawData();

    /**
     * This method is pure function. It does not mutate input
     * and produce new object.
     *
     * Multiply two matrix'es. If matrix has the same size
     * the element wise matrix multiplication is done if not
     * then the dot product is computed.
     *
     * @param another matrix to multiply with `this`
     * @return the result matrix of multiply
     */
    Matrix multiply(final Matrix another);
    Matrix add(final Matrix another);
    Matrix subtract(final Matrix another);
    Matrix transpose();
    Matrix forEach(final Func func);

    int numberOfRows();

    static Matrix of(final int[] column) {
        final var data = new double[column.length][1];
        for (int i = 0; i < column.length; i++)
            data[i][0] = column[i];
        return new SimpleMatrix(data);
    }

    static Matrix of(final double[] column) {
        final var data = new double[column.length][1];
        for (int i = 0; i < column.length; i++)
            data[i][0] = column[i];
        return new SimpleMatrix(data);
    }

    static Matrix of(final double[][] data) {
        return new SimpleMatrix(data);
    }

    static Matrix of(final int[][] data) {
        final var doubles = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                doubles[i][j] = data[i][j];
            }
        }
        return new SimpleMatrix(doubles);
    }

    static Matrix of(final double data) {
        return Matrix.of(new double[] {data});
    }
}
