package io.lipinski.player.ai.internal;

public enum Activation {

    SIGMOID {
        @Override
        double compute(final double value) {
            return 1 / (1 + Math.exp(-value));
        }

        @Override
        double derivative(final double value) {
            return value * (1 - value);
        }
    },
    RELU {
        @Override
        double compute(final double value) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        double derivative(final double value) {
            throw new RuntimeException("Not implemented");
        }
    };

    abstract double compute(final double value);
    abstract double derivative(final double value);
}
