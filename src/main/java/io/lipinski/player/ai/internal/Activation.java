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
    TANH {
        @Override
        double compute(final double value) {
            return Math.tanh(value);
        }

        @Override
        double derivative(final double value) {
            return 1 - (value * value);
        }
    };

    abstract double compute(final double value);
    abstract double derivative(final double value);
}
