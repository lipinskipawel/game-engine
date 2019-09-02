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
    },
    RELU {
        @Override
        double compute(final double value) {
            return Math.max(0, value);
        }

        /**
         * This implementation is based on tf.nn.relu()
         */
        @Override
        double derivative(final double value) {
            if (value > 0) return 1;
            return 0;
        }
    };

    abstract double compute(final double value);
    abstract double derivative(final double value);
}
