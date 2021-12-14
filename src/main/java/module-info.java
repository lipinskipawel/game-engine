module game.engine {
    // optional requirements
    requires static org.slf4j;

    exports com.github.lipinskipawel.board.engine;
    exports com.github.lipinskipawel.board.ai;
    exports com.github.lipinskipawel.board.ai.bruteforce;
    exports com.github.lipinskipawel.board.engine.exception;

    opens com.github.lipinskipawel.board.engine;
    opens com.github.lipinskipawel.board.ai.bruteforce;
}
