package com.github.lipinskipawel;

import com.github.lipinskipawel.board.ai.ml.NDMatrix;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class NDMatrixEnvironment {

    private final NDMatrix a100x100 = new NDMatrix(100, 100);
    private final NDMatrix b100x100 = new NDMatrix(100, 100);
    private final NDMatrix c100x101 = new NDMatrix(100, 101);

    NDMatrix a100x100() {
        return a100x100;
    }

    NDMatrix b100x100() {
        return b100x100;
    }

    NDMatrix c100x101() {
        return c100x101;
    }
}
