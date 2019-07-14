package io.lipinski.player.ai.internal;

interface Matrix {

    double[][] rawData();
    Matrix multiply(Matrix another);
    Matrix add(Matrix another);
    Matrix transpose();
    Matrix forEach(Func func);

    int numberOfRows();

}
