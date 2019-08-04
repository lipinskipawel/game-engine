module game.engine {

    exports io.lipinski.board.legacy;

    opens io.lipinski.board.engine;
    opens io.lipinski.board.legacy;
    opens io.lipinski.player.ai;
    opens io.lipinski.player.ai.internal;

}