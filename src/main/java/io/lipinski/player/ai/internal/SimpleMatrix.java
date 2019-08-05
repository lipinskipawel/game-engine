package io.lipinski.player.ai.internal;

final class SimpleMatrix implements Matrix {

    private double[][] data;


    SimpleMatrix(final double[][] data) {
        super();
        this.data = data;
    }

    SimpleMatrix(final int numberOfRow, final int numberOfColumns) {
        this.data = new double[numberOfRow][numberOfColumns];
    }

    /**
     * This constructor create a matrix with one column.
     *
     */
    SimpleMatrix(final int[] column) {
        this.data = new double[column.length][1];
        for (int i = 0; i < column.length; i++)
            this.data[i][0] = column[i];
    }

    /**
     * This constructor create a matrix with one column.
     *
     */
    SimpleMatrix(final double[] column) {
        this.data = new double[column.length][1];
        for (int i = 0; i < column.length; i++)
            this.data[i][0] = column[i];
    }


    @Override
    public Matrix multiply(final Matrix another) {
        int aRows = this.data.length;
        int aColumns = this.data[0].length;
        int bRows = another.rawData().length;
        int bColumns = another.rawData()[0].length;

        if (aColumns != bRows) {
            throw new ArithmeticException("SimpleMatrix's has improper shapes to multiply " +
                    aColumns + ", another " + bRows);
        }

        double[][] resultMatrix = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {

                var sum = 0.0;
                for (int k = 0; k < aColumns; k++) {
                    sum += this.data[i][k] * another.rawData()[k][j];
                }
                resultMatrix[i][j] = sum;
            }
        }
        return new SimpleMatrix(resultMatrix);
    }

    @Override
    public Matrix add(final Matrix another) {
        if (this.data.length != another.rawData().length)
            throw new ArithmeticException("SimpleMatrix's has different numbers of row this " +
                    this.data.length + ", another " + another.rawData().length);

        final var result = new SimpleMatrix(this.data);

        for (int i = 0; i < another.rawData().length; i++) {
            for (int j = 0; j < another.rawData()[0].length; j++) {
                result.rawData()[i][j] = this.data[i][j] + another.rawData()[i][j];
            }
        }
        return result;
    }

    @Override
    public Matrix transpose() {

        final var numOfRows = this.data.length;
        final var numOfColumns = this.data[0].length;

        final var doubleResult = new double[numOfColumns][numOfRows];

        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                doubleResult[j][i] = this.data[i][j];
            }
        }
        return new SimpleMatrix(doubleResult);
    }

    @Override
    public Matrix forEach(final Func map) {
        final var result = new SimpleMatrix(this.data);

        for (int i = 0; i < this.data.length; i++) {
            for (int j = 0; j < this.data[0].length; j++) {
                this.data[i][j] = map.apply(this.data[i][j]);
            }
        }
        return result;
    }

    @Override
    public double[][] rawData() {
        return this.data;
    }

    @Override
    public int numberOfRows() {
        return this.data.length;
    }

    void prettyPrinting(final String title) {
        StringBuilder builder = new StringBuilder(" ------ Start of " + title + " ------ \n");

        for (final double[] datum : this.data) {
            for (int j = 0; j < this.data[0].length; j++) {
                builder.append(datum[j]).append(" ");
            }
            builder.append("\n");
        }
        builder.append(" ------- End of SimpleMatrix ------- \n");
        System.out.println(builder.toString());
    }
}
