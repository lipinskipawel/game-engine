package com.github.lipinskipawel;

import com.github.lipinskipawel.board.ai.ml.Matrix;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SimpleMatrixEnvironment {

    private final Matrix a100x100 = Matrix.of(100, 100);
    private final Matrix b100x100 = Matrix.of(100, 100);
    private final Matrix c100x101 = Matrix.of(100, 101);

    Matrix a100x100() {
        return a100x100;
    }

    Matrix b100x100() {
        return b100x100;
    }

    Matrix c100x101() {
        return c100x101;
    }
}
