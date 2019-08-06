package io.lipinski.player.ai.internal;

interface Matrix {

    double[][] rawData();
    Matrix multiply(Matrix another);
    Matrix add(Matrix another);
    Matrix transpose();
    Matrix forEach(Func func);

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

}
