package com.github.lipinskipawel.board.engine;

import com.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;
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
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.lipinskipawel.board.engine.Direction.E;
import static com.github.lipinskipawel.board.engine.Direction.N;
import static com.github.lipinskipawel.board.engine.Direction.NE;
import static com.github.lipinskipawel.board.engine.Direction.NW;
import static com.github.lipinskipawel.board.engine.Direction.S;
import static com.github.lipinskipawel.board.engine.Direction.SE;
import static com.github.lipinskipawel.board.engine.Direction.SW;
import static com.github.lipinskipawel.board.engine.Direction.W;
import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static com.github.lipinskipawel.board.engine.Player.SECOND;
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
    @DisplayName("sanity")
    class SanityTest {

        @Test
        @DisplayName("0 moves, FIRST player to move")
        void noMovesFirstPlayerToMove() {
            Assertions.assertThat(board.getPlayer()).isEqualTo(FIRST);
        }

        @Test
        @DisplayName("three moves with undo inside")
        void shouldBeThreeMoves() {
            final ImmutableBoard afterMoves = (ImmutableBoard) board
                    .executeMove(Direction.SE)
                    .executeMove(W)
                    .executeMove(N)
                    .undo()
                    .executeMove(N)
                    .executeMove(W);

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
                    new Move(Collections.singletonList(N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(E)),
                    new Move(Collections.singletonList(Direction.SE)),
                    new Move(Collections.singletonList(S)),
                    new Move(Collections.singletonList(Direction.SW)),
                    new Move(Collections.singletonList(W)),
                    new Move(Collections.singletonList(NW))
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
                    new Move(Collections.singletonList(N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(S)),
                    new Move(Collections.singletonList(W)),
                    new Move(Collections.singletonList(NW)),
                    new Move(Collections.singletonList(E))
            );
            final var afterMoves = board
                    .executeMove(NW)
                    .executeMove(N)
                    .undo();

            final var allMoves = afterMoves.allLegalMoves();

            Assertions.assertThat(allMoves).containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("two moves, one point of contact")
        void allLegalMovesAfterSomeMoves() {
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(NW)),
                    new Move(Collections.singletonList(N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(E)),
                    new Move(Collections.singletonList(Direction.SE)),
                    new Move(Collections.singletonList(S)),
                    new Move(Arrays.asList(Direction.SW, E)),
                    new Move(Arrays.asList(Direction.SW, Direction.SE)),
                    new Move(Arrays.asList(Direction.SW, S)),
                    new Move(Arrays.asList(Direction.SW, Direction.SW)),
                    new Move(Arrays.asList(Direction.SW, W)),
                    new Move(Arrays.asList(Direction.SW, NW))
            );

            final var allMoves = board
                    .executeMove(N)
                    .executeMove(E)
                    .allLegalMoves();

            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("four moves, close to corner")
        void allLegalMoveCloseToCorner() {
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(W)),
                    new Move(Arrays.asList(NW, Direction.SW)),
                    new Move(Arrays.asList(NW, S)),
                    new Move(Arrays.asList(N, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, W, Direction.NE)),
                    new Move(Arrays.asList(N, Direction.SE, W, S)),
                    new Move(Arrays.asList(N, Direction.SE, W, Direction.SE, W)),
                    new Move(Arrays.asList(N, Direction.SE, W, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, W, NW, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, W, NW, S)),
                    new Move(Arrays.asList(N, Direction.SE, W, W)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Arrays.asList(E, Direction.SW)),
                    new Move(Arrays.asList(E, NW, Direction.SW)),
                    new Move(Arrays.asList(E, NW, S, W)),
                    new Move(Arrays.asList(E, NW, S, NW, Direction.SW)),
                    new Move(Arrays.asList(E, NW, S, NW, S)),
                    new Move(Arrays.asList(E, NW, S, Direction.NE)),
                    new Move(Arrays.asList(E, NW, S, Direction.SE, W)),
                    new Move(Arrays.asList(E, NW, S, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(E, NW, S, S)),
                    new Move(Arrays.asList(Direction.SE, W)),
                    new Move(Arrays.asList(Direction.SE, Direction.SW)),
                    new Move(Collections.singletonList(S))
            );

            final var allMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NE)
                    .executeMove(N)
                    .executeMove(Direction.NE)
                    .allLegalMoves();

            Assertions.assertThat(allMoves)
                    .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        @Test
        @DisplayName("two moves, 5 threads")
        void allLegalMovesAfterSomeMovesMultiThread() {
            final var preparedMoves = List.of(
                    new Move(Collections.singletonList(NW)),
                    new Move(Collections.singletonList(N)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Collections.singletonList(E)),
                    new Move(Collections.singletonList(Direction.SE)),
                    new Move(Collections.singletonList(S)),
                    new Move(Arrays.asList(Direction.SW, E)),
                    new Move(Arrays.asList(Direction.SW, Direction.SE)),
                    new Move(Arrays.asList(Direction.SW, S)),
                    new Move(Arrays.asList(Direction.SW, Direction.SW)),
                    new Move(Arrays.asList(Direction.SW, W)),
                    new Move(Arrays.asList(Direction.SW, NW))
            );

            final var afterTwoMoves = board
                    .executeMove(N)
                    .executeMove(E);

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
                    new Move(Collections.singletonList(W)),
                    new Move(Arrays.asList(NW, Direction.SW)),
                    new Move(Arrays.asList(NW, S)),
                    new Move(Arrays.asList(N, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, W, Direction.NE)),
                    new Move(Arrays.asList(N, Direction.SE, W, S)),
                    new Move(Arrays.asList(N, Direction.SE, W, Direction.SE, W)),
                    new Move(Arrays.asList(N, Direction.SE, W, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, W, NW, Direction.SW)),
                    new Move(Arrays.asList(N, Direction.SE, W, NW, S)),
                    new Move(Arrays.asList(N, Direction.SE, W, W)),
                    new Move(Collections.singletonList(Direction.NE)),
                    new Move(Arrays.asList(E, Direction.SW)),
                    new Move(Arrays.asList(E, NW, Direction.SW)),
                    new Move(Arrays.asList(E, NW, S, W)),
                    new Move(Arrays.asList(E, NW, S, NW, Direction.SW)),
                    new Move(Arrays.asList(E, NW, S, NW, S)),
                    new Move(Arrays.asList(E, NW, S, Direction.NE)),
                    new Move(Arrays.asList(E, NW, S, Direction.SE, W)),
                    new Move(Arrays.asList(E, NW, S, Direction.SE, Direction.SW)),
                    new Move(Arrays.asList(E, NW, S, S)),
                    new Move(Arrays.asList(Direction.SE, W)),
                    new Move(Arrays.asList(Direction.SE, Direction.SW)),
                    new Move(Collections.singletonList(S))
            );

            final var afterMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(Direction.NE)
                    .executeMove(N)
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
                            .executeMove(NW)
                            .executeMove(Direction.SE)
            );

            Assertions.assertThat(throwable).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Make a proper full move towards North")
        void makeAMoveN() {
            final var afterMove = board.executeMove(N);

            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_N_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards South")
        void makeAMoveS() {
            final var afterMove = board.executeMove(S);

            int actualBallPosition = afterMove.getBallPosition();
            assertEquals(POSITION_AFTER_S_MOVE, actualBallPosition);
        }

        @Test
        @DisplayName("Make a proper full move towards East, North and check allowed moves")
        void makeAMoveEN() {
            final var afterMove = board.executeMove(E)
                    .executeMove(N);

            assertAll(
                    () -> assertTrue(afterMove.isMoveAllowed(N)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.NE)),
                    () -> assertTrue(afterMove.isMoveAllowed(E)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.SE)),
                    () -> assertFalse(afterMove.isMoveAllowed(S)),
                    () -> assertTrue(afterMove.isMoveAllowed(Direction.SW)),
                    () -> assertTrue(afterMove.isMoveAllowed(W)),
                    () -> assertTrue(afterMove.isMoveAllowed(NW))
            );
        }

        @Test
        @DisplayName("Make a one full move and don't allow to move backwards")
        void notAllowToMakeAMove() {
            final var afterFirstMove = board.executeMove(N);
            BoardInterface afterSecondMove = null;
            if (afterFirstMove.isMoveAllowed(S)) {
                afterSecondMove = board.executeMove(S);
            }

            assertNull(afterSecondMove);
        }

        @Test
        @DisplayName("Can't follow executed moves")
        void makeTwoMovesAndTryFollowExecutedMoves() {
            final var afterMoves = board.executeMove(N)
                    .executeMove(E)
                    .executeMove(Direction.SW);

            assertAll(
                    () -> assertTrue(afterMoves.isMoveAllowed(NW)),
                    () -> assertFalse(afterMoves.isMoveAllowed(N)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.NE)),
                    () -> assertTrue(afterMoves.isMoveAllowed(E)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.SE)),
                    () -> assertTrue(afterMoves.isMoveAllowed(S)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.SW)),
                    () -> assertTrue(afterMoves.isMoveAllowed(W))
            );
        }

        @Test
        @DisplayName("Can't follow executed moves, move sample")
        void makeTwoMovesAndTryFollowExecutedMovesMoreSample() {
            final var afterMoves = board.executeMove(N)
                    .executeMove(E)
                    .executeMove(Direction.SW)
                    .executeMove(Direction.SW)
                    .executeMove(E)
                    .executeMove(N);

            assertAll(
                    () -> assertTrue(afterMoves.isMoveAllowed(NW)),
                    () -> assertFalse(afterMoves.isMoveAllowed(N)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.NE)),
                    () -> assertTrue(afterMoves.isMoveAllowed(E)),
                    () -> assertTrue(afterMoves.isMoveAllowed(Direction.SE)),
                    () -> assertFalse(afterMoves.isMoveAllowed(S)),
                    () -> assertFalse(afterMoves.isMoveAllowed(Direction.SW)),
                    () -> assertTrue(afterMoves.isMoveAllowed(W))
            );
        }

        @Test
        @DisplayName("four moves (inside is one small move)")
        void shouldBePlayerFirstToMove() {
            final var afterMoves = board
                    .executeMove(Direction.NE)
                    .executeMove(NW)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(W);

            Assertions.assertThat(afterMoves.getPlayer()).isEqualByComparingTo(FIRST);
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
            BoardInterface afterMove = board.executeMove(S);
            BoardInterface afterUndo = afterMove.undo();

            int actualBallPosition = afterUndo.getBallPosition();
            assertEquals(STARTING_BALL_POSITION, actualBallPosition);
        }

        @Test
        @DisplayName("Make a one simple S move and then undo")
        void makeAMoveSAndUndoMoveAndCheckSanity() {
            BoardInterface afterMove = board.executeMove(S);
            BoardInterface afterUndo = afterMove.undo();

            final var legalMoves = afterUndo.allLegalMoves();
            Assertions.assertThat(legalMoves.size()).isEqualTo(8);
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move")
        void makeAMoveNAndUndoMove() {
            final var afterOneMove = board.executeMove(N);
            final var afterSecondMove = afterOneMove.executeMove(E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertEquals(afterSecondMove.getBallPosition(), shouldBeAfterSubMove.getBallPosition(),
                    () -> "Ball should be in the same spot");
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move Another Check")
        void makeAMoveNAndUndoMoveAnotherCheck() {
            final var afterOneMove = board.executeMove(N);
            final var afterSecondMove = afterOneMove.executeMove(E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertTrue(shouldBeAfterSubMove.isMoveAllowed(Direction.SW),
                    () -> "Make a move in 'undo' direction must be possible");
        }

        @Test
        @DisplayName("make 4 moves and undo 4 moves")
        void undoAllMoves() {
            final var afterThreeMoves = board
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(Direction.NE)))
                    .executeMove(S)
                    .executeMove(W);

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
                    .executeMove(N)
                    .undo();

            Assertions.assertThat(afterThreeMoves).isEqualToComparingFieldByFieldRecursively(temo);
        }

        @Test
        @DisplayName("sadasdasf ")
        void saundoOneMoves() {
            final var afterThreeMoves = board
                    .executeMove(new Move(List.of(Direction.NE)))
                    .executeMove(N);

            final var second = board
                    .executeMove(Direction.NE)
                    .executeMove(N);

            Assertions.assertThat(second).isEqualToComparingFieldByFieldRecursively(afterThreeMoves);
        }

        @Test
        @DisplayName("Make a few moves and then complex one move and then undo sub move Another Check")
        void makeAMoveNAndUndoMoveAnotherCheckYetAnother() {
            final var afterOneMove = board.executeMove(N);
            final var afterSecondMove = afterOneMove.executeMove(E);

            final var afterSubMove = afterSecondMove.executeMove(Direction.SW);

            final var shouldBeAfterSubMove = afterSubMove.undo();
            assertEquals(FIRST, shouldBeAfterSubMove.getPlayer(),
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
                    .executeMove(N)
                    .executeMove(Direction.NE);

            final var undoPlayer = afterTwoMoves.undoPlayerMove();

            Assertions.assertThat(undoPlayer.getPlayer()).isEqualByComparingTo(afterTwoMoves.getPlayer());
        }

        @Test
        @DisplayName("should undo when small move has been played")
        void undoSmallMove() {
            final var afterTwoMoves = board
                    .executeMove(W)
                    .executeMove(N);

            final var smallMoveAndUndo = afterTwoMoves
                    .executeMove(Direction.SE)
                    .undoPlayerMove();

            Assertions.assertThat(smallMoveAndUndo).isEqualToComparingFieldByFieldRecursively(afterTwoMoves);
        }

        @Test
        @DisplayName("should undo one small move even executed twice")
        void undoSmallMoveTwo() {
            final var afterTwoMoves = board
                    .executeMove(W)
                    .executeMove(N);

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
            Assertions.assertThat(board.getPlayer()).isEqualByComparingTo(FIRST);
        }

        @Test
        @DisplayName("one move")
        void shouldBeSecondPlayerToMove() {
            final var afterMove = board.executeMove(E);

            assertEquals(SECOND, afterMove.getPlayer());
        }

        @Test
        @DisplayName("two moves")
        void shouldBeFirstPlayerToMove() {
            final var afterTwoMoves = board
                    .executeMove(W)
                    .executeMove(S);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(FIRST);
        }

        @Test
        @DisplayName("two moves, small move")
        void shouldBeTheFirstPlayerToMove() {
            final var afterTwoMoves = board
                    .executeMove(W)
                    .executeMove(S)
                    .executeMove(Direction.NE);

            Assertions.assertThat(afterTwoMoves.getPlayer()).isEqualByComparingTo(FIRST);
        }

        @Test
        @DisplayName("two moves, one undo")
        void shouldBeTheSecondPlayer() {
            final var afterTwoMoves = board
                    .executeMove(W)
                    .executeMove(NW);
            final var afterUndoMove = afterTwoMoves.undo();

            Assertions.assertThat(afterUndoMove.getPlayer()).isEqualByComparingTo(SECOND);
        }

        @Test
        @DisplayName("two moves, one small move, one undo, small move")
        void shouldBeThatSamePlayer() {
            final var firstToMove = board
                    .executeMove(E)
                    .executeMove(N);

            final var afterMoveAndUndo = firstToMove
                    .executeMove(Direction.SW)
                    .undo()
                    .executeMove(Direction.SW);

            Assertions.assertThat(afterMoveAndUndo.getPlayer()).isEqualByComparingTo(FIRST);
        }

        @Test
        @DisplayName("5 moves to north goal")
        void fiveMovesToNorthGoal() {
            final var thisIsGoal = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(new Move(List.of(NW, Direction.NE)));

            Assertions.assertThat(thisIsGoal.getPlayer()).isEqualByComparingTo(SECOND);
        }

        @Test
        @DisplayName("5 moves to south goal")
        void fiveMovesToSouthGoal() {
            final var thisIsGoal = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(new Move(List.of(Direction.SE, Direction.SW)));

            Assertions.assertThat(thisIsGoal.getPlayer()).isEqualByComparingTo(SECOND);
        }

        @Test
        @DisplayName("moves to SE corner")
        void movesToSeCorner() {
            final var ballInTheCorner = board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(S)
                    .executeMove(Direction.SE);

            Assertions.assertThat(ballInTheCorner.getPlayer()).isEqualByComparingTo(SECOND);
        }

        @Test
        @DisplayName("15 moves and hit inner corner")
        void fifteenMovesAndHitTheInnerCorner() {
            final var afterMoves = board
                    .executeMove(N)
                    .executeMove(Direction.SE)
                    .executeMove(W)
                    .executeMove(Direction.NE)
                    .executeMove(W)
                    .executeMove(Direction.SW)
                    .executeMove(E)
                    .executeMove(NW)
                    .executeMove(S)
                    .executeMove(Direction.SE)
                    .executeMove(N)
                    .executeMove(Direction.SE)
                    .executeMove(W)
                    .executeMove(W)
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
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(new Move(List.of(NW, Direction.NE)));

            Assertions.assertThat(thisIsGoal.moveHistory().size()).isEqualTo(5);
        }

        @Test
        @DisplayName("5 moves to south goal")
        void fiveMovesToSouthGoal() {
            final var thisIsGoal = board
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
                    .executeMove(S)
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
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(Direction.NE)
                    .executeMove(NW);

            Assertions.assertThat(afterMoves.isGameOver()).isTrue();
        }

        @Test
        @DisplayName("moves to SE corner")
        void shouldEndedTheGameWhenPlayerHitsTheCorner() {
            final var afterMoves = board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(S)
                    .executeMove(Direction.SE);

            Assertions.assertThat(afterMoves.isGameOver()).isTrue();
        }
    }

    @Nested
    @DisplayName("nextPlayerToMove")
    class NextPlayerToMove {

        @Test
        @DisplayName("0 moves, SECOND player to move")
        void changePlayerZeroMoves() {
            final var wantedPlayer = SECOND;

            final var changePlayer = board.nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(changePlayer.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("1 move, FIRST player to move")
        void oneMoveStillFirstPlayerToMove() {
            final var wantedPlayer = FIRST;

            final var afterMoveChangePlayer = board
                    .executeMove(N)
                    .nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(afterMoveChangePlayer.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("should not change player when set the same")
        void changePlayerOnTheSamePlayer() {
            final var player = board
                    .executeMove(E)
                    .executeMove(E)
                    .executeMove(E)
                    .nextPlayerToMove(SECOND)
                    .getPlayer();

            Assertions.assertThat(player).isEqualTo(SECOND);
        }

        @Test
        @DisplayName("5 moves to goal")
        void shouldChangePlayerInGoalArea() {
            final var wantedPlayer = FIRST;

            final var afterMoves = board
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(N)
                    .executeMove(NW)
                    .executeMove(Direction.NE)
                    .nextPlayerToMove(wantedPlayer);

            assertAll(
                    () -> Assertions.assertThat(afterMoves.isGoal()).isTrue(),
                    () -> Assertions.assertThat(afterMoves.getPlayer()).isEqualTo(wantedPlayer)
            );
        }

        @Test
        @DisplayName("5 moves to corner")
        void shouldChangePlayerInCornerKill() {
            final var wantedPlayer = FIRST;

            final var afterMoves = board
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(Direction.SE)
                    .executeMove(S)
                    .executeMove(Direction.SE)
                    .nextPlayerToMove(wantedPlayer);

            assertAll(
                    () -> Assertions.assertThat(afterMoves.isGameOver()).isTrue(),
                    () -> Assertions.assertThat(afterMoves.getPlayer()).isEqualTo(wantedPlayer)
            );
        }

        @Test
        @DisplayName("one move, one undo, change player")
        void shouldChangeOnUnchangedBoard() {
            final var wantedPlayer = SECOND;

            final var afterMove = board
                    .executeMove(Direction.SE)
                    .undo()
                    .nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(afterMove.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("undo small move, change player")
        void shouldChangePlayerAfterSmallUndo() {
            final var wantedPlayer = SECOND;

            final var afterMoves = board
                    .executeMove(Direction.SE)
                    .executeMove(W)
                    .executeMove(N)
                    .undoPlayerMove()
                    .nextPlayerToMove(wantedPlayer);

            Assertions.assertThat(afterMoves.getPlayer()).isEqualTo(wantedPlayer);
        }

        @Test
        @DisplayName("throw exception during small move")
        void shouldThrowExceptionDuringSmallMoveForTheSamePlayer() {
            final var exception = assertThrows(ChangePlayerIsNotAllowed.class,
                    () -> board
                            .executeMove(Direction.SE)
                            .executeMove(W)
                            .executeMove(N)
                            .nextPlayerToMove(FIRST),
                    () -> "Switching player during small moves is NOT acceptable"
            );

            Assertions.assertThat(exception).isInstanceOf(ChangePlayerIsNotAllowed.class);
        }

        @Test
        @DisplayName("throw exception during small move")
        void shouldThrowExceptionDuringSmallMoveForNextPlayer() {
            final var exception = assertThrows(ChangePlayerIsNotAllowed.class,
                    () -> board
                            .executeMove(Direction.SE)
                            .executeMove(W)
                            .executeMove(N)
                            .nextPlayerToMove(SECOND),
                    () -> "Switching player during small moves is NOT acceptable"
            );

            Assertions.assertThat(exception).isInstanceOf(ChangePlayerIsNotAllowed.class);
        }
    }

    @Nested
    @DisplayName("takeTheWinner")
    class TakeTheWinnerTest {

        @Test
        @DisplayName("0 move, no winner")
        void zeroMovesNoWinner() {
            assertThrows(NoSuchElementException.class,
                    () -> board
                            .takeTheWinner()
                            .orElseThrow()
            );
        }

        @Test
        @DisplayName("should give First player when upper goal by First")
        void upperGoalByFirstPlayer() {
            final var winner = board
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(NW, NE)))
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(FIRST);
        }

        @Test
        @DisplayName("should give First player when upper goal by Second")
        void upperGoalBySecondPlayer() {
            final var winner = board
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(N)))
                    .nextPlayerToMove(SECOND)
                    .executeMove(new Move(List.of(NW, NE)))
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(FIRST);
        }

        @Test
        @DisplayName("should give Second player when bottom goal by First")
        void bottomGoalByFirstPlayer() {
            final var winner = board
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(SW, SE)))
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(SECOND);
        }

        @Test
        @DisplayName("should give Second player when bottom goal by Second")
        void bottomGoalBySecondPlayer() {
            final var winner = board
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(S)))
                    .executeMove(new Move(List.of(S)))
                    .nextPlayerToMove(SECOND)
                    .executeMove(new Move(List.of(SW, SE)))
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(SECOND);
        }

        @Test
        @DisplayName("should give Second player when First hits the corner")
        void firstHitsTheCorner() {
            final var winner = board
                    .executeMove(new Move(List.of(NE)))
                    .executeMove(new Move(List.of(NE)))
                    .executeMove(new Move(List.of(NE)))
                    .executeMove(new Move(List.of(N)))
                    .executeMove(new Move(List.of(NE)))
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(SECOND);
        }

        @Test
        @DisplayName("should give First player when Second hits the corner")
        void secondHitsTheCorner() {
            final var winner = board
                    .executeMove(new Move(List.of(NE)))
                    .executeMove(new Move(List.of(NE)))
                    .executeMove(new Move(List.of(NE)))
                    .executeMove(new Move(List.of(N)))
                    .nextPlayerToMove(SECOND)
                    .executeMove(new Move(List.of(NE)))
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(FIRST);
        }

        @Test
        @DisplayName("should give Second player when First hits the corner in the center of board")
        void firstHitsTheCornerInTheCenterOfBoard() {
            final var winner = board
                    .executeMove(NE)
                    .executeMove(W)
                    .executeMove(SE)
                    .executeMove(W)
                    .executeMove(N)
                    .executeMove(SW)
                    .executeMove(E)
                    .executeMove(NW)
                    .executeMove(S)
                    .executeMove(SE)
                    .executeMove(N)
                    .executeMove(SW)
                    .executeMove(E)
                    .executeMove(E)
                    .executeMove(NW)
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(SECOND);
        }

        @Test
        @DisplayName("should give First player when Second hits the corner in the center of board")
        void secondHitsTheCornerInTheCenterOfBoard() {
            final var winner = board
                    .executeMove(NE)
                    .executeMove(W)
                    .executeMove(SE)
                    .executeMove(W)
                    .executeMove(N)
                    .executeMove(SW)
                    .executeMove(E)
                    .executeMove(NW)
                    .executeMove(S)
                    .executeMove(SE)
                    .executeMove(N)
                    .executeMove(SW)
                    .executeMove(E)
                    .executeMove(E)
                    .nextPlayerToMove(SECOND)
                    .executeMove(NW)
                    .takeTheWinner()
                    .get();

            Assertions.assertThat(winner).isEqualTo(FIRST);
        }
    }
}
