package com.github.lipinskipawel.board.ai.ml;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class NDMatrix {

    private final double[] cModel;
    private final double[] fModel;
    private final int[] shape;

    public NDMatrix(final int rows, final int columns) {
        this(new double[rows][columns]);
    }

    NDMatrix(final double[] cModel, final double[] fModel, final int[] shape) {
        this.shape = shape;
        this.cModel = cModel;
        this.fModel = fModel;
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

    public static NDMatrix fromCModel(final double[] cModel, int numberOfRows) {
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
        if (shapesAreCoolForElementWiseOperation(another)) {
            return elementWiseMultiply(another);
        }
        var result = new double[this.shape[0] * another.shape[1]];
        for (int number = 0; number < another.shape[0]; number++) {
            final var a = computePseudoCModelForGivenRow(another, number);
            result = add(result, a);
        }
        return NDMatrix.fromCModel(result, this.shape[0]);
    }

    private NDMatrix elementWiseMultiply(final NDMatrix another) {
        final var resultCModel = cModel();
        final var resultFModel = fModel();
        for (int i = 0; i < resultCModel.length; i++) {
            resultCModel[i] = this.cModel()[i] * another.cModel()[i];
            resultFModel[i] = this.fModel()[i] * another.fModel()[i];
        }
        return new NDMatrix(resultCModel, resultFModel, this.shape);
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

        var indexForAGivenCycle = 0 + factorOrARowNumber;
        var miniCycleCounter = 0;
        var lengthOfCycle = another.shape[1];

        //        while (indexIteration != another.shape[0]) {
        for (int index = 0; index < pseudoResult.length; index++) {
            if (miniCycleCounter < lengthOfCycle) {
                miniCycleCounter++;
            } else {
                indexForAGivenCycle = indexForAGivenCycle + another.shape[0];
                miniCycleCounter = 1;
            }
            pseudoResult[index] = this.cModel()[indexForAGivenCycle];
        }

        var lengthOfTheCycle = another.shape[1];
        var numberInTheCycle = 0;
        var magicIndex = -another.shape[0] + factorOrARowNumber;
        for (int index = 0; index < pseudoResult.length; index++) {
            if (numberInTheCycle < lengthOfTheCycle) {
                magicIndex = magicIndex + another.shape[0];
                numberInTheCycle++;
            } else {
                magicIndex = 0 + factorOrARowNumber;
                numberInTheCycle = 1;
            }
            pseudoResult[index] = pseudoResult[index] * another.fModel()[magicIndex];
        }
        return pseudoResult;
    }

    public NDMatrix add(final NDMatrix another) {
        if (shapesAreCoolForElementWiseOperation(another)) {
            final var resultCModel = cModel();
            final var resultFModel = fModel();
            for (int i = 0; i < resultCModel.length; i++) {
                resultCModel[i] = this.cModel()[i] + another.cModel()[i];
                resultFModel[i] = this.fModel()[i] + another.fModel()[i];
            }
            return new NDMatrix(resultCModel, resultFModel, this.shape);
        }
        return throwExceptionNotTheSameSize(another, "add");
    }

    private boolean shapesAreCoolForElementWiseOperation(final NDMatrix another) {
        return this.shape[0] != 0 && this.shape[0] == another.shape[0] && this.shape[1] == another.shape[1];
    }

    public NDMatrix subtract(final NDMatrix another) {
        if (shapesAreCoolForElementWiseOperation(another)) {
            final var resultCModel = cModel();
            final var resultFModel = fModel();
            for (int i = 0; i < resultCModel.length; i++) {
                resultCModel[i] = this.cModel()[i] - another.cModel()[i];
                resultFModel[i] = this.fModel()[i] - another.fModel()[i];
            }
            return new NDMatrix(resultCModel, resultFModel, this.shape);
        }
        return throwExceptionNotTheSameSize(another, "subtract");
    }

    private NDMatrix throwExceptionNotTheSameSize(final NDMatrix another, final String operation) {
        throw new ArithmeticException("Can't " + operation + " matrix's with different shapes: " +
                this.shape[0] + "x" + this.shape[1] + " with " +
                another.shape[0] + "x" + another.shape[1]
        );
    }

    public NDMatrix transpose() {
        final var newShape = new int[]{this.shape[1], this.shape[0]};
        return new NDMatrix(this.fModel(), this.cModel(), newShape);
    }

    public NDMatrix forEach(final Func func) {
        final var resultCModel = cModel();
        final var resultFModel = fModel();
        for (int i = 0; i < resultCModel.length; i++) {
            resultCModel[i] = func.apply(cModel()[i]);
            resultFModel[i] = func.apply(fModel()[i]);
        }
        return new NDMatrix(resultCModel, resultFModel, this.shape);
    }

    public int numberOfRows() {
        return this.shape[0];
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
