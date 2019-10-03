module game.engine {

    exports io.lipinski.board.engine;
    exports io.lipinski.board.engine.exceptions;

    opens io.lipinski.board.engine;
    opens io.lipinski.board.neuralnetwork;
    opens io.lipinski.board.neuralnetwork.internal;
    opens io.lipinski.board.neuralnetwork.internal.activation;

}