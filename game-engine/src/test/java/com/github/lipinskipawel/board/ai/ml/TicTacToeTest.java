package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMax;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMaxAlphaBeta;
import com.github.lipinskipawel.board.ai.ml.activation.Relu;
import com.github.lipinskipawel.board.ai.ml.activation.Sigmoid;
import com.github.lipinskipawel.board.ai.ml.lossfunction.MSE;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Direction;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.board.engine.Point;
import com.github.lipinskipawel.board.engine.exception.ChangePlayerIsNotAllowed;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

@DisplayName("Internal -- TicTacToe")
class TicTacToeTest {

    @Test
    void agentTraining() {
        var game = TicTacToe.createGame();
        var ticTacToeBruteForce = new MiniMaxAlphaBeta(new TicTacToeEvaluator());

        final var nnEvaluator = new NeuralNetworkEvaluator(game);
        final var agentStrategy = new MiniMax(nnEvaluator, 1);

        MoveStrategy currentStrategy = ticTacToeBruteForce;

        do {
            final var move = currentStrategy.execute(game, 9);
            System.out.println("Best move for player " + game.getPlayer() + " is: " + move.getMove());
            game = game.executeMove(move);
            currentStrategy = game.getPlayer() == Player.FIRST ? ticTacToeBruteForce : agentStrategy;
        } while (!game.isGameOver());

        final WinnerKeeper state = new WinnerKeeper();
        game
                .takeTheWinner()
                .ifPresentOrElse(player -> {
                            System.out.println("The winner is: " + player);
                            if (player == Player.FIRST) {
                                state.didSecondWinTheGame = false;
                            }
                        },
                        () -> {
                            System.out.println("No winner");
                            state.didSecondWinTheGame = false;
                        });
        if (state.didSecondWinTheGame) {
            System.out.println(new TicTacToeEvaluator().evaluate(game));
            System.out.println(game);
            Assertions.fail("SECOND won the game");
        }
    }
}

final class WinnerKeeper {
    boolean didSecondWinTheGame = true;
}

final class NeuralNetworkEvaluator implements BoardEvaluator {

    private final NeuralNetwork agent;

    NeuralNetworkEvaluator(final BoardInterface game) {
        this.agent = new DeepNeuralNetwork.Builder()
                .addLayer(new Layer(game.nonBinaryTransformation().length, new Relu()))
                .addLayer(new Layer(5, new Relu()))
                .addLayer(new Layer(1, new Sigmoid()))
                .compile()
                .lossFunction(new MSE())
                .build();
    }

    @Override
    public double evaluate(final BoardInterface board) {
        final var predict = this.agent.predict(board.nonBinaryTransformation()).getBestValue().doubleValue();
        System.out.println("NN predicted: " + predict);
        return predict;
    }
}

final class TicTacToe implements BoardInterface {

    // 0.2 - this is empty field
    // 0.5 - this is taken by X
    // 0.9 - this is taken by O
    private final double[][] board;

    // 0.5 - first
    // 0.9 - second
    private final double currentPlayer;

    // 0.2 - none
    // 0.5 - first
    // 0.9 - second
    private double winner;

    private TicTacToe() {
        this.board = new double[][]{{0.2, 0.2, 0.2}, {0.2, 0.2, 0.2}, {0.2, 0.2, 0.2}};
        this.currentPlayer = 0.5;
        this.winner = 0.2;
    }

    public TicTacToe(final double[][] board, final double currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.winner = 0.2;
    }

    @Override
    public String toString() {
        return String.join("\n", Arrays.deepToString(this.board).split("],"));
    }

    public static BoardInterface createGame() {
        return new TicTacToe();
    }

    @Override
    public BoardInterface executeMove(final Direction direction) {
        if (notTaken(direction)) {
            final var copy = copyBoard(this.board);
            switch (direction) {
                case S:
                    copy[2][1] = currentPlayer;
                    break;
                case SW:
                    copy[2][0] = currentPlayer;
                    break;
                case W:
                    copy[1][0] = currentPlayer;
                    break;
                case NW:
                    copy[0][0] = currentPlayer;
                    break;
                case N:
                    copy[0][1] = currentPlayer;
                    break;
                case NE:
                    copy[0][2] = currentPlayer;
                    break;
                case E:
                    copy[1][2] = currentPlayer;
                    break;
                case SE:
                    copy[2][2] = currentPlayer;
                    break;
                default:
                    copy[1][1] = currentPlayer;
            }
            final var afterMove = new TicTacToe(copy, currentPlayer == 0.5 ? 0.9 : 0.5);
            if (afterMove.noMoreMoves()) {
                final double player = afterMove.getPlayer() == Player.FIRST ? 0.9 : 0.5;
                return new TicTacToe(afterMove.board, player);
            }
            return afterMove;
        }
        throw new IllegalStateException(direction + " is taken!!");
    }

    private double[][] copyBoard(final double[][] board) {
        final var current = new double[3][3];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                current[i][j] = board[i][j];
        return current;
    }

    private boolean notTaken(final Direction direction) {
        var canMove = false;
        switch (direction) {
            case S:
                canMove = this.board[2][1] == 0.2;
                break;
            case SW:
                canMove = this.board[2][0] == 0.2;
                break;
            case W:
                canMove = this.board[1][0] == 0.2;
                break;
            case NW:
                canMove = this.board[0][0] == 0.2;
                break;
            case N:
                canMove = this.board[0][1] == 0.2;
                break;
            case NE:
                canMove = this.board[0][2] == 0.2;
                break;
            case E:
                canMove = this.board[1][2] == 0.2;
                break;
            case SE:
                canMove = this.board[2][2] == 0.2;
                break;
        }
        return canMove;
    }

    @Override
    public BoardInterface executeMove(final Move move) {
        final var oneMove = move.getMove();
        if (oneMove.size() == 0) {
            System.out.println(Arrays.deepToString(this.board));
            System.out.println("bug? : " + isGameOver());
            throw new IllegalStateException("TicTacToe is not allowing 0 as a direction");
        }
        if (oneMove.size() == 2) {
            if (isCenterTaken()) {
                this.board[1][1] = currentPlayer;
                return new TicTacToe(this.board, currentPlayer == 0.5 ? 0.9 : 0.5);
            }
            return this;
        }
        return executeMove(oneMove.get(0));
    }

    private boolean isCenterTaken() {
        return this.board[1][1] == 0.2;
    }

    @Override
    public BoardInterface undoPlayerMove() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public BoardInterface undo() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public List<Move> allLegalMoves() {
        final var moves = new ArrayList<Move>();
        if (this.board[0][0] == 0.2) {
            moves.add(new Move(List.of(Direction.NW)));
        }
        if (this.board[0][1] == 0.2) {
            moves.add(new Move(List.of(Direction.N)));
        }
        if (this.board[0][2] == 0.2) {
            moves.add(new Move(List.of(Direction.NE)));
        }
        if (this.board[1][0] == 0.2) {
            moves.add(new Move(List.of(Direction.W)));
        }
        if (this.board[1][1] == 0.2) {
            moves.add(new Move(List.of(Direction.N, Direction.N)));
        }
        if (this.board[1][2] == 0.2) {
            moves.add(new Move(List.of(Direction.E)));
        }
        if (this.board[2][0] == 0.2) {
            moves.add(new Move(List.of(Direction.SW)));
        }
        if (this.board[2][1] == 0.2) {
            moves.add(new Move(List.of(Direction.S)));
        }
        if (this.board[2][2] == 0.2) {
            moves.add(new Move(List.of(Direction.SE)));
        }
        return moves;
    }

    @Override
    public List<Move> moveHistory() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public List<Direction> allMoves() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public boolean isMoveAllowed(final Direction destination) {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public int getBallPosition() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public Point getBallAPI() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public boolean isGoal() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public Player getPlayer() {
//        return winner == 0.5 ? Player.FIRST : Player.SECOND;
        return currentPlayer == 0.5 ? Player.FIRST : Player.SECOND;
    }

    @Override
    public boolean isGameOver() {
        if (noMoreMoves()) {
            return true;
        }
        return checkForWin(this.board[0][0], this.board[0][1], this.board[0][2]) || // row
                checkForWin(this.board[1][0], this.board[1][1], this.board[1][2]) || // row
                checkForWin(this.board[2][0], this.board[2][1], this.board[2][2]) || // row
                checkForWin(this.board[0][0], this.board[1][0], this.board[2][0]) || // col
                checkForWin(this.board[0][1], this.board[1][1], this.board[2][1]) || // col
                checkForWin(this.board[0][2], this.board[1][2], this.board[2][2]) || // col
                checkForWin(this.board[0][0], this.board[1][1], this.board[2][2]) || // cors
                checkForWin(this.board[0][2], this.board[1][1], this.board[2][0]); // cors
    }

    private boolean checkForWin(final double first, final double second, final double third) {
        if (first == second && second == third && first != 0.2) {
            this.winner = first;
            return true;
        }
        return false;
    }

    boolean isFirstPlayerWon() {
        return privateCheckForWin(this.board[0][0], this.board[0][1], this.board[0][2]) || // row
                privateCheckForWin(this.board[1][0], this.board[1][1], this.board[1][2]) || // row
                privateCheckForWin(this.board[2][0], this.board[2][1], this.board[2][2]) || // row
                privateCheckForWin(this.board[0][0], this.board[1][0], this.board[2][0]) || // col
                privateCheckForWin(this.board[0][1], this.board[1][1], this.board[2][1]) || // col
                privateCheckForWin(this.board[0][2], this.board[1][2], this.board[2][2]) || // col
                privateCheckForWin(this.board[0][0], this.board[1][1], this.board[2][2]) || // cors
                privateCheckForWin(this.board[0][2], this.board[1][1], this.board[2][0]); // cors
    }

    private boolean privateCheckForWin(final double firstSquare, final double secondSquare, final double thirdSquare) {
        return firstSquare == secondSquare && secondSquare == thirdSquare && firstSquare == 0.5;
    }

    boolean noMoreMoves() {
        return Arrays.stream(this.board)
                .flatMapToDouble(Arrays::stream)
                .filter(x -> x == 0.2)
                .count() == 0;
    }

    @Override
    public BoardInterface nextPlayerToMove(final Player nextPlayerToMove) throws ChangePlayerIsNotAllowed {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public Optional<Player> takeTheWinner() {
        if (winner == 0.2) {
            return Optional.empty();
        }
        isGameOver();
        return winner == 0.5 ? Optional.of(Player.FIRST) : Optional.of(Player.SECOND);
    }

    @Override
    public int[] transform() {
        throw new IllegalStateException("not implemented yet");
    }

    @Override
    public double[] nonBinaryTransformation() {
        return Stream.of(this.board)
                .flatMapToDouble(Arrays::stream)
                .toArray();
    }
}

final class TicTacToeEvaluator implements BoardEvaluator {

    @Override
    public double evaluate(final BoardInterface board) {
        if (board instanceof TicTacToe && board.isGameOver()) {
            final TicTacToe game = (TicTacToe) board;
            return game.isFirstPlayerWon() ? new Random().nextDouble() : new Random().nextDouble() - 2.0;
        }
        return new Random().nextDouble();
    }
}
