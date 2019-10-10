package com.github.lipinskipawel.board.neuralnetwork;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;

interface MoveStrategy {

    Move execute(BoardInterface board, int depth);

}
