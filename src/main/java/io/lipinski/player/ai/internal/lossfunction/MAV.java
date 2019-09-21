package io.lipinski.player.ai.internal.lossfunction;

import io.lipinski.player.ai.internal.Matrix;

final public class MAV extends LossFunction {

    @Override
    public Matrix compute(final Matrix target, final Matrix output) {
        return target.subtract(output);
    }
}
