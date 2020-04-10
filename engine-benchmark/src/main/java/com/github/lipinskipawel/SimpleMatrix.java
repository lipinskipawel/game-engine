package com.github.lipinskipawel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class SimpleMatrix {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void multiplication(SimpleMatrixEnvironment matrixEnvironment,
                               Blackhole blackhole) {
        final var a = matrixEnvironment.a();
        final var b = matrixEnvironment.b();

        blackhole.consume(a.multiply(b));
    }
}
