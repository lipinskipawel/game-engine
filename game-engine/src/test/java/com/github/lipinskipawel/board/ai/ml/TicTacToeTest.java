package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.TicTacToe;
import com.github.lipinskipawel.board.ai.BoardEvaluator;
import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMax;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMaxAlphaBeta;
import com.github.lipinskipawel.board.ai.ml.activation.Relu;
import com.github.lipinskipawel.board.ai.ml.activation.Sigmoid;
import com.github.lipinskipawel.board.ai.ml.lossfunction.MSE;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Direction;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@DisplayName("Internal -- TicTacToe")
class TicTacToeTest {

    @Test
    void agentTraining() {
        var game = TicTacToe.createGame();
        var ticTacToeBruteForce = new MiniMaxAlphaBeta(board -> new Random().nextDouble());

        final var nnEvaluator = new NeuralNetworkEvaluator(game);
        final var agentStrategy = new MiniMax(nnEvaluator);

        MoveStrategy currentStrategy = ticTacToeBruteForce;

        do {
            final var move = currentStrategy.execute(game, 1);
            System.out.println("Best move for player " + game.getPlayer() + " is: " + move.getMove());
            game = game.executeMove(move);
            currentStrategy = game.getPlayer() == Player.FIRST ? ticTacToeBruteForce : agentStrategy;
        } while (!game.isGameOver());

        game
                .takeTheWinner()
                .ifPresentOrElse(player ->
                                System.out.println("The winner is: " + player),
                        () -> System.out.println("No winner"));
    }
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
