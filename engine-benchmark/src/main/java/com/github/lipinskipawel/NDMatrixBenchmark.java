package com.github.lipinskipawel;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class NDMatrixBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void multiplication(NDMatrixEnvironment matrixEnvironment,
                               Blackhole blackhole) {
        final var a = matrixEnvironment.a100x100();
        final var c = matrixEnvironment.c100x101();

        blackhole.consume(a.multiply(c));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void multiplicationElementWise(NDMatrixEnvironment matrixEnvironment,
                                          Blackhole blackhole) {
        final var a = matrixEnvironment.a100x100();
        final var b = matrixEnvironment.b100x100();

        blackhole.consume(a.multiply(b));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void add(NDMatrixEnvironment matrixEnvironment,
                    Blackhole blackhole) {
        final var a = matrixEnvironment.b100x100();
        final var b = matrixEnvironment.b100x100();

        blackhole.consume(a.add(b));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void subtract(NDMatrixEnvironment matrixEnvironment,
                         Blackhole blackhole) {
        final var a = matrixEnvironment.a100x100();
        final var b = matrixEnvironment.b100x100();

        blackhole.consume(a.subtract(b));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void transpose(NDMatrixEnvironment matrixEnvironment,
                          Blackhole blackhole) {
        final var a = matrixEnvironment.a100x100();

        blackhole.consume(a.transpose());
    }
}
