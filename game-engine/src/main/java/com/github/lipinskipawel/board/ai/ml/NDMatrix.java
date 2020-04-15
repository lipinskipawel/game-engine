package com.github.lipinskipawel.board.ai.ml;

import java.util.Arrays;
import java.util.stream.IntStream;

final class NDMatrix {

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

    public static NDMatrix fromCModel(final double[] cModel,
                                      int numberOfRows) {
        final var result = new double[numberOfRows][];
        final var columns = cModel.length / numberOfRows;
        for (int i = 0; i < numberOfRows; i++) {
            result[i] = Arrays.copyOfRange(cModel, i * columns, i * columns + columns);
        }
        return new NDMatrix(result);
    }

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

    public NDMatrix multiply(final NDMatrix another) throws ArithmeticException {
        var result = new double[this.shape[0] * another.shape[1]];
        for (int number = 0; number < another.shape[0]; number++) {
            final var a = computePseudoCModelForGivenRow(another, number);
            result = add(result, a);
        }
        return NDMatrix.fromCModel(result, this.shape[0]);
    }

    private double[] add(final double[] first,
                         final double[] second) {
        final var result = new double[first.length];
        for (int i = 0; i < first.length; i++) {
            result[i] = first[i] + second[i];
        }
        return result;
    }

    private double[] computePseudoCModelForGivenRow(final NDMatrix another,
                                                    final int factorOrARowNumber) {
        var pseudoResult = new double[this.shape[0] * another.shape[1]];

        var indexOfAGivenCycle = 0 + factorOrARowNumber;
        var miniCycleCounter = 0;
        var lengthOfCycle = another.shape[1];

        //        while (indexIteration != another.shape[0]) {
        for (int index = 0; index < pseudoResult.length; index++) {
            if (miniCycleCounter < lengthOfCycle) {
                miniCycleCounter++;
            } else {
                indexOfAGivenCycle = indexOfAGivenCycle + another.shape[0];
                miniCycleCounter = 0;
            }
            pseudoResult[index] = this.cModel()[indexOfAGivenCycle];
        }

        var numberOfAllowedCycles = another.shape[1];
        var numberOfCycle = 0;
        var magicIndex = -another.shape[0] + factorOrARowNumber;
        for (int index = 0; index < pseudoResult.length; index++) {
            if (numberOfCycle < numberOfAllowedCycles) {
                magicIndex = magicIndex + another.shape[0];
                numberOfCycle++;
            } else {
                magicIndex = 0 + factorOrARowNumber;
                numberOfCycle = 0;
            }
            pseudoResult[index] = pseudoResult[index] * another.fModel()[magicIndex];
        }
        return pseudoResult;
    }

    public NDMatrix add(final NDMatrix another) throws ArithmeticException {
        return null;
    }

    public NDMatrix subtract(final NDMatrix another) {
        return null;
    }

    public NDMatrix transpose() {
        return null;
    }

    public NDMatrix forEach(final Func func) {
        return null;
    }

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NDMatrix ndMatrix = (NDMatrix) o;
        return Arrays.equals(cModel, ndMatrix.cModel) &&
                Arrays.equals(fModel, ndMatrix.fModel) &&
                Arrays.equals(shape, ndMatrix.shape);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(cModel);
        result = 31 * result + Arrays.hashCode(fModel);
        result = 31 * result + Arrays.hashCode(shape);
        return result;
    }
}
