package com.github.lipinskipawel.board.ai;

import com.github.lipinskipawel.board.engine.Board;
import com.github.lipinskipawel.board.engine.Player;

public interface BoardEvaluator {

    double evaluate(Board<Player> board);

}
