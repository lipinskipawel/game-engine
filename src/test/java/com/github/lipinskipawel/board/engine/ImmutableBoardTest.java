package com.github.lipinskipawel.board.engine;

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
    @DisplayName("sanity -- recursively")
    class SanityTest {

        @Test
        @DisplayName("three moves with undo inside")
        void shouldBeThreeMoves() {
            final ImmutableBoard afterMoves = (ImmutableBoard) board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.W)
                    .executeMove(Direction.N)
                    .undo()
                    .executeMove(Direction.N)
                    .executeMove(Direction.W);

            final ImmutableBoard undo = (ImmutableBoard) afterMoves.undoPlayerMove();

            Assertions.assertThat(afterMoves).isEqualToComparingFieldByFieldRecursively(undo);
        }
    }

    @Nested
    @DisplayName("allLegalMoves")
    class LegalMoves {

        @Test
        @DisplayName("zero moves")
        void allLegalMoves() {
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

            final var allMoves = board.allLegalMoves();

            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("two moves, one undo")
        void shouldReturnTheSameMoveAsOnlyOneMoveHadBeenMade() {
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(Direction.SW)),
                    new Move(Collections.singletonList(Direction.N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(Direction.S)),
                    new Move(Collections.singletonList(Direction.W)),
                    new Move(Collections.singletonList(Direction.NW)),
                    new Move(Collections.singletonList(Direction.E))
            );
            final var afterMoves = board
                    .executeMove(Direction.NW)
                    .executeMove(Direction.N)
                    .undo();

            final var allMoves = afterMoves.allLegalMoves();

            Assertions.assertThat(allMoves).containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("two moves, one point of contact")
        void allLegalMovesAfterSomeMoves() {
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

            final var allMoves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.E)
                    .allLegalMoves();

            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("four moves, close to corner")
        void allLegalMoveCloseToCorner() {
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

            final var allMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NE)
                    .executeMove(Direction.N)
                    .executeMove(Direction.NE)
                    .allLegalMoves();

            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("two moves, 5 threads")
        void allLegalMovesAfterSomeMovesMultiThread() {
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

            final var afterTwoMoves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.E);

            final var legalMoves1 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves2 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves3 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves4 = executor.submit(afterTwoMoves::allLegalMoves);
            final var legalMoves5 = executor.submit(afterTwoMoves::allLegalMoves);

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
        @DisplayName("four moves, 5 threads")
        void allLegalMoveCloseToCornerMultiThread() {
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
        @DisplayName("one move, one move in opposite direction to first one")
        void shouldThrowRuntimeException() {
            final var throwable = Assertions.catchThrowable(
                    () -> board
                            .executeMove(Direction.NW)
                            .executeMove(Direction.SE)
            );

            Assertions.assertThat(throwable).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Make a proper full move towards North")
        void makeAMoveN() {
            final var afterMove = board.executeMove(Direction.N);

            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_N_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards South")
        void makeAMoveS() {
            final var afterMove = board.executeMove(Direction.S);

            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_S_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards East, North and check allowed moves")
        void makeAMoveEN() {
            final var afterMove = board.executeMove(Direction.E)
                    .executeMove(Direction.N);

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
            final var afterFirstMove = board.executeMove(Direction.N);
            BoardInterface afterSecondMove = null;
            if (afterFirstMove.isMoveAllowed(Direction.S)) {
                afterSecondMove = board.executeMove(Direction.S);
            }

            assertNull(afterSecondMove);
        }

        @Test
        @DisplayName("Can't follow executed moves")
        void makeTwoMovesAndTryFollowExecutedMoves() {
            final var afterMoves = board.executeMove(Direction.N)
                    .executeMove(Direction.E)
                    .executeMove(Direction.SW);

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
            final var afterMoves = board.executeMove(Direction.N)
                    .executeMove(Direction.E)
                    .executeMove(Direction.SW)
                    .executeMove(Direction.SW)
                    .executeMove(Direction.E)
                    .executeMove(Direction.N);

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

        @Test
        @DisplayName("four moves (inside is one small move)")
        void shouldBePlayerFirstToMove() {
            final var afterMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NW)
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(Direction.W);

            Assertions.assertThat(afterMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }
    }

    @Nested
    @DisplayName("undoMove")
    class UndoAMove {

        @Test
        @DisplayName("Try to undo move when no move has been done yet")
        void undoMoveWhenGameJustBegun() {

            assertThrows(RuntimeException.class,
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
        @DisplayName("make 4 moves and undo 4 moves")
        void undoOneMoves() {
            final var temo = board
                    .executeMove(new Move(List.of(Direction.NE)));

            final var afterThreeMoves = board
                    .executeMove(new Move(List.of(Direction.NE)))
                    .executeMove(Direction.N)
                    .undo();

            Assertions.assertThat(afterThreeMoves).isEqualToComparingFieldByFieldRecursively(temo);
        }

        @Test
        @DisplayName("sadasdasf ")
        void saundoOneMoves() {
            final var afterThreeMoves = board
                    .executeMove(new Move(List.of(Direction.NE)))
                    .executeMove(Direction.N);

            final var second = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.N);

            Assertions.assertThat(second).isEqualToComparingFieldByFieldRecursively(afterThreeMoves);
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
        @DisplayName("should undo when small move has been played")
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
                    .undoPlayerMove();

            final var boardInterface = smallMoveAndUndo.undoPlayerMove();

            Assertions.assertThat(boardInterface).isEqualToComparingFieldByFieldRecursively(smallMoveAndUndo);
        }
    }

    @Nested
    @DisplayName("getPlayer")
    class GetPlayerTest {

        @Test
        @DisplayName("zero moves")
        void shouldBeTheFirstPlayerToMoveEmptyBoard() {
            Assertions.assertThat(board.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("one move")
        void shouldBeSecondPlayerToMove() {
            final var afterMove = board.executeMove(Direction.E);

            assertEquals(Player.SECOND, afterMove.getPlayer());
        }

        @Test
        @DisplayName("two moves")
        void shouldBeFirstPlayerToMove() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.S);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("two moves, small move")
        void shouldBeTheFirstPlayerToMove() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.S)
                    .executeMove(Direction.NE);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("two moves, one undo")
        void shouldBeTheSecondPlayer() {
            final var afterTwoMoves = board
                    .executeMove(Direction.W)
                    .executeMove(Direction.NW);
            final var afterUndoMove = afterTwoMoves.undo();

            Assertions.assertThat(afterUndoMove.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("two moves, one small move, one undo, small move")
        void shouldBeThatSamePlayer() {
            final var firstToMove = board
                    .executeMove(Direction.E)
                    .executeMove(Direction.N);

            final var afterMoveAndUndo = firstToMove
                    .executeMove(Direction.SW)
                    .undo()
                    .executeMove(Direction.SW);

            Assertions.assertThat(afterMoveAndUndo.getPlayer()).isEqualByComparingTo(Player.FIRST);
        }

        @Test
        @DisplayName("5 moves to north goal")
        void fiveMovesToNorthGoal() {
            final var thisIsGoal = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(new Move(List.of(Direction.NW, Direction.NE)));

            Assertions.assertThat(thisIsGoal.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("5 moves to south goal")
        void fiveMovesToSouthGoal() {
            final var thisIsGoal = board
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(new Move(List.of(Direction.SE, Direction.SW)));

            Assertions.assertThat(thisIsGoal.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("moves to SE corner")
        void movesToSeCorner() {
            final var ballInTheCorner = board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.S)
                    .executeMove(Direction.SE);

            Assertions.assertThat(ballInTheCorner.getPlayer()).isEqualByComparingTo(Player.SECOND);
        }

        @Test
        @DisplayName("15 moves and hit inner corner")
        void fifteenMovesAndHitTheInnerCorner() {
            final var afterMoves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.W)
                    .executeMove(Direction.NE)
                    .executeMove(Direction.W)
                    .executeMove(Direction.SW)
                    .executeMove(Direction.E)
                    .executeMove(Direction.NW)
                    .executeMove(Direction.S)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.N)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.W)
                    .executeMove(Direction.W)
                    .executeMove(Direction.NE);

            Assertions.assertThat(afterMoves.allLegalMoves().isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("moveHistory")
    class MoveHistoryTest {

        @Test
        @DisplayName("5 moves to north goal")
        void fiveMovesToNorthGoal() {
            final var thisIsGoal = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(new Move(List.of(Direction.NW, Direction.NE)));

            Assertions.assertThat(thisIsGoal.moveHistory().size()).isEqualTo(5);
        }

        @Test
        @DisplayName("5 moves to south goal")
        void fiveMovesToSouthGoal() {
            final var thisIsGoal = board
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(Direction.S)
                    .executeMove(new Move(List.of(Direction.SE, Direction.SW)));

            Assertions.assertThat(thisIsGoal.moveHistory().size()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("isGameOver")
    class IsGameOver {

        @Test
        @DisplayName("0 moves")
        void shouldNotEndTheGameAfter0Moves() {
            Assertions.assertThat(board.isGameOver()).isFalse();
        }

        @Test
        @DisplayName("5 moves to north goal")
        void shouldBeGameOverWhenPlayerScoreAGoal() {
            final var afterMoves = board
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.N)
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NW);

            Assertions.assertThat(afterMoves.isGameOver()).isTrue();
        }

        @Test
        @DisplayName("moves to SE corner")
        void shouldEndedTheGameWhenPlayerHitsTheCorner() {
            final var afterMoves = board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.S)
                    .executeMove(Direction.SE);

            Assertions.assertThat(afterMoves.isGameOver()).isTrue();
        }
    }
}
