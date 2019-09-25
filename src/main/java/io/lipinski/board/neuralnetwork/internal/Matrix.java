package io.lipinski.board.neuralnetwork.internal;

/**
 * This interface describe set of methods that can be done
 * on two dimension implementation of array.
 */
public interface Matrix {

    double[][] rawData();

    /**
     * This method is pure function. It does not mutate input
     * and produce new object.
     *
     * Multiply two matrix'es. If both matrix'es has the same size
     * the element wise matrix multiplication is done if not
     * then the dot product is computed.
     *
     * @param another matrix to multiply with `this`
     * @return the instance of Matrix interface
     * @throws ArithmeticException when is impossible to either
     *      compute the dot product or element wise
     */
    Matrix multiply(final Matrix another) throws ArithmeticException;

    /**
     * This method will add element wise two matrix'es.
     *
     * @param another
     * @return the instance of Matrix interface
     * @throws ArithmeticException when `this` and `another` has different shape
     */
    Matrix add(final Matrix another) throws ArithmeticException;
    Matrix subtract(final Matrix another);
    Matrix transpose();
    Matrix forEach(final Func func);

    int numberOfRows();

    /**
     * This static method will convert array of integers to Matrix
     * instance. Matrix instance will have one column and the same
     * amount rows as elements in passed arrays.
     *
     * @param column
     * @return instance of Matrix interface
     */
    static Matrix of(final int[] column) {
        final var data = new double[column.length][1];
        for (int i = 0; i < column.length; i++)
            data[i][0] = column[i];
        return new SimpleMatrix(data);
    }

    /**
     * This static method will convert array of doubles to Matrix
     * instance. Matrix instance will have one column and the same
     * amount rows as elements in passed arrays.
     *
     * @param column
     * @return instance of Matrix interface
     */
    static Matrix of(final double[] column) {
        final var data = new double[column.length][1];
        for (int i = 0; i < column.length; i++)
            data[i][0] = column[i];
        return new SimpleMatrix(data);
    }

    /**
     * This static method will convert 2d array into Matrix instance.
     *
     * @param data
     * @return instance of Matrix interface
     */
    static Matrix of(final double[][] data) {
        return new SimpleMatrix(data);
    }

    /**
     * This static method will convert 2d array into Matrix instance.
     * @param data
     * @return instance of Matrix interface
     */
    static Matrix of(final int[][] data) {
        final var doubles = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                doubles[i][j] = data[i][j];
            }
        }
        return new SimpleMatrix(doubles);
    }

    /**
     * This static method will convert double into Matrix instance.
     *
     * @param data
     * @return instance of Matrix interface
     */
    static Matrix of(final double data) {
        return Matrix.of(new double[] {data});
    }

    /**
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return instance of Matrix interface
     */
    static Matrix of(final int rows, final int columns) {
        return new SimpleMatrix(rows, columns);
    }
}
