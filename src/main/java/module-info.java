module game.engine {

    exports com.github.lipinskipawel.board.engine;
    exports com.github.lipinskipawel.board.engine.exceptions;

    opens com.github.lipinskipawel.board.engine;
    opens com.github.lipinskipawel.board.neuralnetwork;
    opens com.github.lipinskipawel.board.neuralnetwork.internal;
    opens com.github.lipinskipawel.board.neuralnetwork.internal.activation;

}