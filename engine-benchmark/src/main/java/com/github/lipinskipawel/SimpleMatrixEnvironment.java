package com.github.lipinskipawel;

import com.github.lipinskipawel.board.ai.ml.Matrix;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class SimpleMatrixEnvironment {

    private final Matrix a = Matrix.of(100, 100);
    private final Matrix b = Matrix.of(100, 100);


    public Matrix a() {
        return a;
    }

    public Matrix b() {
        return b;
    }
}
