package com.github.lipinskipawel.board.ai.ml;

import java.util.Arrays;
import java.util.stream.IntStream;

final class NDMatrix implements Matrix {

    private final double[] cModel;
    private final double[] fModel;
    private final int[] shape;

    public NDMatrix(final int rows, final int columns) {
        this(new double[rows][columns]);
    }

    public NDMatrix(final double[][] array) {
        this.shape = new int[]{array.length, array[0].length};
        this.cModel = Arrays.stream(array)
                .flatMapToDouble(Arrays::stream)
                .toArray();
        this.fModel = new double[array[0].length * array.length];
        var counter = 0;
        for (int row = 0; row < array[0].length; row++) {
            for (final double[] doubles : array) {
                this.fModel[counter] = doubles[row];
                counter++;
            }
        }
    }

    @Override
    public double[][] rawData() {
        return IntStream.range(0, shape[0])
                .mapToObj(this::takeRow)
                .toArray(double[][]::new);
    }

    private double[] takeRow(final int which) {
        final var lengthOfOneRow = shape[1];
        final var starting = lengthOfOneRow * which;
        return Arrays.copyOfRange(cModel, starting, starting + lengthOfOneRow);
    }

    @Override
    public Matrix multiply(final Matrix another) throws ArithmeticException {
        return null;
    }

    @Override
    public Matrix add(final Matrix another) throws ArithmeticException {
        return null;
    }

    @Override
    public Matrix subtract(final Matrix another) {
        return null;
    }

    @Override
    public Matrix transpose() {
        return null;
    }

    @Override
    public Matrix forEach(final Func func) {
        return null;
    }

    @Override
    public int numberOfRows() {
        return 0;
    }

    double[] cModel() {
        return Arrays.copyOf(this.cModel, this.cModel.length);
    }

    double[] fModel() {
        return Arrays.copyOf(this.fModel, this.fModel.length);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(this.rawData()) + "\tshape: " + Arrays.toString(this.shape);
    }
}
