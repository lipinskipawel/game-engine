package io.lipinski.board.legacy;

import io.lipinski.board.engine.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Running board test")
class MutableBoardTest {

    private BoardInterface board;

    @BeforeEach
    void setUp() throws Exception {
        this.board = new MutableBoard();
    }


    @Test
    @DisplayName("Make a one full move using points")
    void moveBallToNorthUsingPoint() {

        //Given:
        Point destBallPosition = new Point(49);

        //When:
        board.tryMakeMove(destBallPosition);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(destBallPosition.getPosition(), actualBallPosition);

    }

    @Test
    @DisplayName("Make a one full move using directions")
    void moveBallToNorthUsingDirections() {

        //Given:
        Point destBallPosition = new Point(49);

        //When:
        board.tryMakeMove(Direction.N);

        //Then:
        int actualBallPosition = board.getBallPosition();
        assertEquals(destBallPosition.getPosition(), actualBallPosition);

    }


    @Test
    @DisplayName("Make a full move and then undo this move")
    void moveBallAndUndoTheMove() {

        //Given:
        int ballPositionAtStart = board.getBallPosition();
        Point point = new Point(60);

        //When:
        board.tryMakeMove(point);
        board.undoMove();

        //Then:
        assertEquals(ballPositionAtStart, 58);
    }


}
