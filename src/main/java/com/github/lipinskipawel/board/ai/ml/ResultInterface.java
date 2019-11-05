package com.github.lipinskipawel.board.ai.ml;

/**
 * This interface represents the output of neural network as objects.
 */
public interface ResultInterface {

    /**
     * It is necessary to set up proper order of labels
     *
     * @return by this value order is set up ascending
     */
    int order();

}
