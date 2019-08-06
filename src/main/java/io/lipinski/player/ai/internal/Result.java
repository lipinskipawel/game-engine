package io.lipinski.player.ai.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps output of Neural Network.
 *
 * @param <T>
 */
class Result<T extends Enum & ResultInterface> {

    private final List<T> data;


    Result(List<T> data) {
        this.data = new ArrayList<>(data);
    }

    public T getBest() {
        return data.get(0);
    }

}
