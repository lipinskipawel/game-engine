package com.github.lipinskipawel.board.engine;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.lipinskipawel.board.engine.Direction.E;
import static com.github.lipinskipawel.board.engine.Direction.N;
import static com.github.lipinskipawel.board.engine.Direction.NW;
import static com.github.lipinskipawel.board.engine.Direction.S;
import static com.github.lipinskipawel.board.engine.Direction.W;
import static org.junit.jupiter.api.Assertions.assertAll;

final class LegalMovesFutureTest implements WithAssertions {

    private final Board<Player> board = Boards.immutableBoard();
    private final Board<Player> complicatedBoard = complicatedBoard();

    private Board<Player> complicatedBoard() {
        return Boards.immutableBoard()
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(W)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(S)
            .executeMove(E)
            .executeMove(E)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N)
            .executeMove(N);
    }

    @Test
    void shouldCancelTask() {
        final var shouldNotComplete = complicatedBoard.allLegalMovesFuture();
        final var shouldComplete = complicatedBoard.allLegalMovesFuture();
        shouldNotComplete.start(Duration.ofSeconds(2));
        shouldComplete.start(Duration.ofSeconds(2));

        final List<Move> globalForNotComplete = new ArrayList<>();
        // wait for start of the computation
        while (shouldNotComplete.isRunning()) {
            final var moves = shouldNotComplete.partialResult();
            if (moves.size() > 0) {
                globalForNotComplete.addAll(moves);
                break;
            }
        }
        shouldNotComplete.cancel();
        // should we save new partial results after cancel or discard them?
        globalForNotComplete.addAll(shouldNotComplete.partialResult());

        while (shouldComplete.isRunning()) {
            Thread.onSpinWait();
        }
        final var completed = shouldComplete.partialResult().size();
        assertThat(globalForNotComplete.size()).isLessThan(completed);
    }

    @Nested
    @DisplayName("allLegalMoves")
    class LegalMoves {
        private ExecutorService executor;

        @BeforeEach
        void setUp() {
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
            final var legalMovesFuture = board.allLegalMovesFuture();

            final var allMoves = findAllLegalMoves(legalMovesFuture);

            assertThat(allMoves)
                .containsExactlyInAnyOrderElementsOf(preparedMoves);
        }

        private List<Move> findAllLegalMoves(LegalMovesFuture legalMovesFuture) {
            legalMovesFuture.start(Duration.ofSeconds(1));
            final var allMoves = new ArrayList<Move>();

            while (legalMovesFuture.isRunning()) {
                allMoves.addAll(legalMovesFuture.partialResult());
            }
            allMoves.addAll(legalMovesFuture.partialResult());
            return allMoves;
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
            final var legalMovesFuture = afterMoves.allLegalMovesFuture();

            final var allMoves = findAllLegalMoves(legalMovesFuture);

            assertThat(allMoves).containsExactlyInAnyOrderElementsOf(preparedMoves);
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
            final var afterMove = board
                .executeMove(N)
                .executeMove(E);
            final var legalMovesFuture = afterMove.allLegalMovesFuture();

            final var allMoves = findAllLegalMoves(legalMovesFuture);

            assertThat(allMoves)
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
            final var afterMove = board
                .executeMove(Direction.NE)
                .executeMove(Direction.NE)
                .executeMove(N)
                .executeMove(Direction.NE);

            final var legalMovesFuture = afterMove.allLegalMovesFuture();

            final var allMoves = findAllLegalMoves(legalMovesFuture);

            assertThat(allMoves)
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
            final var legalMovesFuture1 = afterTwoMoves.allLegalMovesFuture();
            final var legalMovesFuture2 = afterTwoMoves.allLegalMovesFuture();
            final var legalMovesFuture3 = afterTwoMoves.allLegalMovesFuture();
            final var legalMovesFuture4 = afterTwoMoves.allLegalMovesFuture();
            final var legalMovesFuture5 = afterTwoMoves.allLegalMovesFuture();

            final var legalMoves1 = executor.submit(() -> findAllLegalMoves(legalMovesFuture1));
            final var legalMoves2 = executor.submit(() -> findAllLegalMoves(legalMovesFuture2));
            final var legalMoves3 = executor.submit(() -> findAllLegalMoves(legalMovesFuture3));
            final var legalMoves4 = executor.submit(() -> findAllLegalMoves(legalMovesFuture4));
            final var legalMoves5 = executor.submit(() -> findAllLegalMoves(legalMovesFuture5));

            try {
                final var results = List.of(
                    legalMoves1.get(),
                    legalMoves2.get(),
                    legalMoves3.get(),
                    legalMoves4.get(),
                    legalMoves5.get()
                );

                assertAll(
                    () -> assertThat(results.get(0))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(1))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(2))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(3))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(4))
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
            final var legalMovesFuture1 = afterMoves.allLegalMovesFuture();
            final var legalMovesFuture2 = afterMoves.allLegalMovesFuture();
            final var legalMovesFuture3 = afterMoves.allLegalMovesFuture();
            final var legalMovesFuture4 = afterMoves.allLegalMovesFuture();
            final var legalMovesFuture5 = afterMoves.allLegalMovesFuture();

            final var legalMoves1 = executor.submit(() -> findAllLegalMoves(legalMovesFuture1));
            final var legalMoves2 = executor.submit(() -> findAllLegalMoves(legalMovesFuture2));
            final var legalMoves3 = executor.submit(() -> findAllLegalMoves(legalMovesFuture3));
            final var legalMoves4 = executor.submit(() -> findAllLegalMoves(legalMovesFuture4));
            final var legalMoves5 = executor.submit(() -> findAllLegalMoves(legalMovesFuture5));

            try {
                final var results = List.of(
                    legalMoves1.get(),
                    legalMoves2.get(),
                    legalMoves3.get(),
                    legalMoves4.get(),
                    legalMoves5.get()
                );

                assertAll(
                    () -> assertThat(results.get(0))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(1))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(2))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(3))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves),
                    () -> assertThat(results.get(4))
                        .containsExactlyInAnyOrderElementsOf(preparedMoves)
                );
            } catch (Exception e) {
                fail("Can't get result from all 5 threads");
            }
        }
    }
}
