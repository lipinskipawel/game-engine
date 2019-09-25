package io.lipinski.board.neuralnetwork;

import io.lipinski.board.engine.BoardInterface2;
import io.lipinski.board.engine.Move;

interface MoveStrategy {

    Move execute(BoardInterface2 board, int depth);

}
