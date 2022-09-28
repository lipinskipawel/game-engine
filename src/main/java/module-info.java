module game.engine {
    exports com.github.lipinskipawel.board.spi;
    exports com.github.lipinskipawel.board.engine;
    exports com.github.lipinskipawel.board.ai;
    exports com.github.lipinskipawel.board.ai.bruteforce;
    exports com.github.lipinskipawel.board.engine.exception;

    opens com.github.lipinskipawel.board.engine;
    opens com.github.lipinskipawel.board.ai.bruteforce;
}
