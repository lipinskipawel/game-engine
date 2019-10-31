package com.github.lipinskipawel.board.neuralnetwork.internal;

import com.github.lipinskipawel.board.neuralnetwork.internal.activation.ActivationFunction;

final class LayerInternal {

    private Matrix weight;
    private Matrix bias;
    private final ActivationFunction activation;

    LayerInternal(final Matrix weight, final Matrix bias, final ActivationFunction activation) {
        this.weight = weight;
        this.bias = bias;
        this.activation = activation;
    }

    Matrix forward(final Matrix data) {
        return this.activation
                .compute(weight
                        .multiply(data)
                        .add(bias)
                );
    }

    Matrix gradient(final Matrix outputFromPreviousLayer,
                    final Matrix error,
                    final double lr) {
        return this.activation
                .derivative(outputFromPreviousLayer)
                .multiply(error)
                .forEach(x -> x * lr);
    }

    Matrix delta(final Matrix gradient,
                 final Matrix deltaValue) {
        return gradient.multiply(deltaValue);
    }

    Matrix error(final Matrix error) {
        return this.weight
                .transpose()
                .multiply(error);
    }

    void updateWeight(final Matrix delta) {
        this.weight = this.weight.add(delta);
    }

    void updateBias(final Matrix gradient) {
        this.bias = this.bias.add(gradient);
    }

    void randomize(final Func fun) {
        this.weight = this.weight.forEach(fun);
        this.bias = this.bias.forEach(fun);

    }
}
