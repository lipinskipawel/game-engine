package io.lipinski.board.engine;

import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("API -- ImmutableBoard")
class ImmutableBoardTest {

    private BoardInterface board;
    private ExecutorService executor;
    private static int STARTING_BALL_POSITION;
    private static int POSITION_AFTER_N_MOVE;
    private static int POSITION_AFTER_S_MOVE;


    @BeforeAll
    static void setUpVariable() {
        STARTING_BALL_POSITION = 58;
        POSITION_AFTER_N_MOVE = 49;
        POSITION_AFTER_S_MOVE = 67;
    }

    @BeforeEach
    void setUp() {
        this.board = new ImmutableBoard();
        this.executor = Executors.newFixedThreadPool(5);
    }

    @AfterEach
    void cleanUp() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    @Nested
    @DisplayName("allLegalMoves")
    class LegalMoves {

        @Test
        @DisplayName("List of legal moves with clean board")
        void allLegalMoves() {
            //Given:
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(Direction.N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(Direction.E)),
                    new Move(Collections.singletonList(Direction.SE)),
                    new Move(Collections.singletonList(Direction.S)),
                    new Move(Collections.singletonList(Direction.SW)),
                    new Move(Collections.singletonList(Direction.W)),
                    new Move(Collections.singletonList(Direction.NW))
            );

            //When:
            final var allMoves = board.allLegalMoves();

            //Then:
            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("List of legal moves after N, E")
        void allLegalMovesAfterSomeMoves() {
            //Given:
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(Direction.NW)),
                    new Move(Collections.singletonList(Direction.N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(Direction.E)),
                    new Move(Collections.singletonList(Direction.SE)),
                    new Move(Collections.singletonList(Direction.S)),
                    new Move(Arrays.asList(Direction.SW, Direction.E)),
                    new Move(Arrays.asList(Direction.SW, Direction.SE)),
                    new Move(Arrays.asList(Direction.SW, Direction.S)),
                    new Move(Arrays.asList(Direction.SW, Direction.SW)),
                    new Move(Arrays.asList(Direction.SW, Direction.W)),
                    new Move(Arrays.asList(Direction.SW, Direction.NW))
            );

            //When:
            final var afterTwoMoves = board.executeMove(Direction.N)
                    .executeMove(Direction.E);
            final var allMoves = afterTwoMoves.allLegalMoves();

            //Then:
            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("List of legal moves after NE, NE, N, NE. Ball close to corner")
        void allLegalMoveCloseToCorner() {
            //Given:
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(Direction.W)),
                    new Move(Arrays.asList(Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.NW, Direction.S)),
                    new Move(Arrays.asList(Direction.N, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.NE)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.S)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.SE, Direction.W)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.NW, Direction.S)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.W)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Arrays.asList(Direction.E, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.W)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.NW, Direction.S)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.NE)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.SE, Direction.W)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.S)),
                    new Move(Arrays.asList(Direction.SE, Direction.W)),
                    new Move(Arrays.asList(Direction.SE, Direction.SW)),
                    new Move(Collections.singletonList(Direction.S))
            );

            //When:
            final var afterMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NE)
                    .executeMove(Direction.N)
                    .executeMove(Direction.NE);
            final var allMoves = afterMoves.allLegalMoves();

            //Then:
            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("List of legal moves after N, E. MultiThreaded")
        void allLegalMovesAfterSomeMovesMultiThread() {
            //Given:
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(Direction.NW)),
                    new Move(Collections.singletonList(Direction.N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(Direction.E)),
                    new Move(Collections.singletonList(Direction.SE)),
                    new Move(Collections.singletonList(Direction.S)),
                    new Move(Arrays.asList(Direction.SW, Direction.E)),
                    new Move(Arrays.asList(Direction.SW, Direction.SE)),
                    new Move(Arrays.asList(Direction.SW, Direction.S)),
                    new Move(Arrays.asList(Direction.SW, Direction.SW)),
                    new Move(Arrays.asList(Direction.SW, Direction.W)),
                    new Move(Arrays.asList(Direction.SW, Direction.NW))
            );

            //When:
            final var afterTwoMoves = board.executeMove(Direction.N).executeMove(Direction.E);

            final var legalMoves1 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves2 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves3 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves4 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves5 = executor.submit(afterTwoMoves::allLegalMoves);

            //Then:
            try {
                final var results = List.of(
                        legalMoves1.get(),
                        legalMoves2.get(),
                        legalMoves3.get(),
                        legalMoves4.get(),
                        legalMoves5.get()
                );

                assertAll(
                        () -> Assertions.assertThat(results.get(0))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(1))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(2))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(3))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(4))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves)
                );
            } catch (Exception e) {
                fail("Can't get result from all 5 threads");
            }
        }

        @Test
        @DisplayName("List of legal moves after NE, NE, N, NE. Ball close to corner. MultiThreaded")
        void allLegalMoveCloseToCornerMultiThread() {
            //Given:
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(Direction.W)),
                    new Move(Arrays.asList(Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.NW, Direction.S)),
                    new Move(Arrays.asList(Direction.N, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.NE)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.S)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.SE, Direction.W)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.NW, Direction.S)),
                    new Move(Arrays.asList(Direction.N, Direction.SE, Direction.W, Direction.W)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Arrays.asList(Direction.E, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.W)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.NW, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.NW, Direction.S)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.NE)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.SE, Direction.W)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(Direction.E, Direction.NW, Direction.S, Direction.S)),
                    new Move(Arrays.asList(Direction.SE, Direction.W)),
                    new Move(Arrays.asList(Direction.SE, Direction.SW)),
                    new Move(Collections.singletonList(Direction.S))
            );

            //When:
            final var afterMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NE)
                    .executeMove(Direction.N)
                    .executeMove(Direction.NE);

            final var legalMoves1 = executor.submit(afterMoves::allLegalMoves);
            final var legalMoves2 = executor.submit(afterMoves::allLegalMoves);
            final var legalMoves3 = executor.submit(afterMoves::allLegalMoves);
            final var legalMoves4 = executor.submit(afterMoves::allLegalMoves);
            final var legalMoves5 = executor.submit(afterMoves::allLegalMoves);

            //Then:
            try {
                final var results = List.of(
                        legalMoves1.get(),
                        legalMoves2.get(),
                        legalMoves3.get(),
                        legalMoves4.get(),
                        legalMoves5.get()
                );

                assertAll(
                        () -> Assertions.assertThat(results.get(0))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(1))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(2))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(3))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves),
                        () -> Assertions.assertThat(results.get(4))
                                .containsExactlyInAnyOrderElementsOf(preparedMoves)
                );
            } catch (Exception e) {
                fail("Can't get result from all 5 threads");
            }
        }

    }

    @Nested
    @DisplayName("executeMove")
    class MakeAMove {

        @Test
        @DisplayName("Make a proper full move towards North")
        void makeAMoveN() {
            //When:
            BoardInterface afterMove = board.executeMove(Direction.N);

            //Then:
            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_N_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards South")
        void makeAMoveS() {
            //When:
            BoardInterface afterMove = board.executeMove(Direction.S);

            //Then:
            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_S_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards East, North and check allowed moves")
        void makeAMoveEN() {
            //When:
            BoardInterface afterMove = board.executeMove(Direction.E)
                    .executeMove(Direction.N);

            //Then:
            assertAll(
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.N)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.NE)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.E)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.SE)),
                    () -> assertFalse(afterMove.isMoveAllowed(Direction.S)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.SW)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.W)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.NW))
            );
        }

        @Test
        @DisplayName("Make a one full move and don't allow to move backwards")
        void notAllowToMakeAMove() {
            //When:
            BoardInterface afterFirstMove = board.executeMove(Direction.N);
            BoardInterface afterSecondMove = null;
            if (afterFirstMove.isMoveAllowed(Direction.S)) {
                afterSecondMove = board.executeMove(Direction.S);
            }

            //Then:
            assertNull(afterSecondMove);
        }

        @Test
        @DisplayName("Can't follow executed moves")
        void makeTwoMovesAndTryFollowExecutedMoves() {
            //When:
            final var afterMoves = board.executeMove(Direction.N)
                    .executeMove(Direction.E)
                    .executeMove(Direction.SW);

            //Then:
            assertAll(
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.NW)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.N)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.NE)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.E)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.SE)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.S)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.SW)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.W))
            );
        }

        @Test
        @DisplayName("Can't follow executed moves, move sample")
        void makeTwoMovesAndTryFollowExecutedMovesMoreSample() {
            //When:
            final var afterMoves = board.executeMove(Direction.N)
                    .executeMove(Direction.E)
                    .executeMove(Direction.SW)
                    .executeMove(Direction.SW)
                    .executeMove(Direction.E)
                    .executeMove(Direction.N);

            //Then:
            assertAll(
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.NW)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.N)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.NE)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.E)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.SE)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.S)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.SW)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.W))
            );
        }
    }

    @Nested
    @DisplayName("undoMove")
    class UndoAMove {

        @Test
        @DisplayName("Try to undo move when no move has been done yet")
        void undoMoveWhenGameJustBegun() {

            assertThrows(IllegalUndoMoveException.class,
                    () -> board.undo(),
                    () -> "Can't undo move when no move has been done");
        }

        @Test
        @DisplayName("Make a one simple S move and then undo")
        void makeAMoveSAndUndoMove() {
            BoardInterface afterMove = board.executeMove(Direction.S);
            BoardInterface afterUndo = afterMove.undo();

            int actualBallPosition = afterUndo.getBallPosition();
            assertEquals(STARTING_BALL_POSITION, actualBallPosition);
        }

        @Test
        @DisplayName("Make a one simple S move and then undo")
        void makeAMoveSAndUndoMoveAndCheckSanity() {
            BoardInterface afterMove = board.executeMove(Direction.S);
            BoardInterface afterUndo = afterMove.undo();

            final var legalMoves = afterUndo.allLegalMoves();
            Assertions.assertThat(legalMoves.size()).isEqualTo(8);
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move")
        void makeAMoveNAndUndoMove() {
            final var afterOneMove = board.executeMove(Direction.N);
            final var afterSecondMove = afterOneMove.executeMove(Direction.E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertEquals(afterSecondMove.getBallPosition(), shouldBeAfterSubMove.getBallPosition(),
                    () -> "Ball should be in the same spot");
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move Another Check")
        void makeAMoveNAndUndoMoveAnotherCheck() {
            final var afterOneMove = board.executeMove(Direction.N);
            final var afterSecondMove = afterOneMove.executeMove(Direction.E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertTrue(shouldBeAfterSubMove.isMoveAllowed(Direction.SW),
                    () -> "Make a move in 'undo' direction must be possible");
        }

        @Test
        @DisplayName("make 4 moves and undo 4 moves")
        void undoAllMoves() {
            final var afterThreeMoves = board
                    .executeMove(new Move(List.of(Direction.N)))
                    .executeMove(new Move(List.of(Direction.NE)))
                    .executeMove(Direction.S)
                    .executeMove(Direction.W);

            final var undoAllMoves = afterThreeMoves
                    .undo()
                    .undo()
                    .undo()
                    .undo();

            Assertions.assertThat(undoAllMoves).isEqualToComparingFieldByFieldRecursively(board);
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move Another Check")
        void makeAMoveNAndUndoMoveAnotherCheckYetAnother() {
            final var afterOneMove = board.executeMove(Direction.N);
            final var afterSecondMove = afterOneMove.executeMove(Direction.E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertEquals(Player.FIRST, shouldBeAfterSubMove.getPlayer(),
                    () -> "Not change player");
        }

    }

    @Nested
    @DisplayName("undoPlayerMove")
    class UndoPlayerMoveTest {

        @Test
        @DisplayName("should not undo when no small moves are made")
        void noUndoNoSmallMoves() {
            final var afterTwoMoves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.NE);

            final var undoPlayer = afterTwoMoves.undoPlayerMove();

            Assertions.assertThat(undoPlayer.getPlayer()).isEqualByComparingTo(afterTwoMoves.getPlayer());
        }

        @Test
        @DisplayName("should undo when on small move has been played")
        void undoSmallMove() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.N);

            final var smallMoveAndUndo = afterTwoMoves
                    .executeMove(Direction.SE)
                    .undoPlayerMove();

            Assertions.assertThat(smallMoveAndUndo).isEqualToComparingFieldByFieldRecursively(afterTwoMoves);
        }

        @Test
        @DisplayName("should undo one small move even executed twice")
        void undoSmallMoveTwo() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.N);

            final var smallMoveAndUndo = afterTwoMoves
                    .executeMove(Direction.SE)
                    .undoPlayerMove()
                    .undoPlayerMove();

            Assertions.assertThat(smallMoveAndUndo).isEqualToComparingFieldByFieldRecursively(afterTwoMoves);
        }
    }

    @Nested
    @DisplayName("Make a move, and check player")
    class MakeAMoveAndCheckPlayer {

        @Test
        @DisplayName("Make a move as FIRST player and check if this is SECOND player turn")
        void secondPlayerNeedToMove() {
            final var afterMove = board.executeMove(Direction.E);

            assertEquals(Player.SECOND, afterMove.getPlayer());
        }

        @Test
        @DisplayName("After two moves First player to move")
        void afterTwoMoves() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.S);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("After two moves and small First player to move")
        void afterTwoMovesAndSmallMove() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.S)
                    .executeMove(Direction.NE);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("Make a move, undo and check player turn")
        void makeTwoMovesAndCheckPlayerTurn() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.NW);
            final var afterUndoMove = afterTwoMoves.undo();

            assertEquals(Player.SECOND, afterUndoMove.getPlayer());
        }

        @Test
        @DisplayName("Make a couple of moves and undo, should be the same player to move")
        void makeMovesAndUndo() {
            final var firstToMove = board
                    .executeMove(Direction.E)
                    .executeMove(Direction.N);

            final var afterMoveAndUndo = firstToMove
                    .executeMove(Direction.SW)
                    .undo()
                    .executeMove(Direction.SW);

            Assertions.assertThat(afterMoveAndUndo.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }
    }
}
