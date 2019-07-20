package io.lipinski.board.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;

class MoveHistoryTest {

    private MoveHistory moveHistory;


    @BeforeEach
    void setUp() {
        this.moveHistory = new MoveHistory();
    }

    @Test
    void deepCopy() {

        //When:
        final var oneMove = moveHistory.add(Direction.W);
        final var twoMoves = oneMove.add(Direction.N);
        final var oneMoveAgain = twoMoves.subtract();

        //Then:
        assertNotSame(oneMove, oneMoveAgain);
    }

}
