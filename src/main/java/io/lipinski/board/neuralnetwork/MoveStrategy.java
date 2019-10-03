package io.lipinski.board.neuralnetwork;

import io.lipinski.board.engine.BoardInterface;
import io.lipinski.board.engine.Move;

interface MoveStrategy {

    Move execute(BoardInterface board, int depth);

}
