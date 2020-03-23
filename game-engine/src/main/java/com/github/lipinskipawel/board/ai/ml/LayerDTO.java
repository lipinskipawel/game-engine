package com.github.lipinskipawel.board.ai.ml;

import com.github.lipinskipawel.board.ai.ml.activation.ActivationFunction;
import com.github.lipinskipawel.board.ai.ml.activation.Relu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class LayerDTO {

    private final Matrix weight;
    private final Matrix biases;
    private final ActivationFunction activationFunction;

    LayerDTO(final Matrix weight, final Matrix biases, final ActivationFunction activationFunction) {
        this.weight = weight;
        this.biases = biases;
        this.activationFunction = activationFunction;
    }

    /**
     * This method converts string into an instance of {@link LayerDTO}.
     * This method swallows every exception during parsing {@link ActivationFunction} and provides default class which
     * is {@link Relu}.
     *
     * @param encodedLayer output from {@link NeuralNetwork#fromModelToString()}
     * @return {@link LayerDTO}
     */
    public static LayerDTO parseStringDoubleWeightDoubleBiasClassActivationFunction(final String encodedLayer) {
        try {
            final var pieces = encodedLayer.split(":");

            var we = pieces[0];
            var ba = pieces[1];
            var af = pieces[2];

            final var weight = convert(we);
            final var biases = convert(ba);
            final var activationFunction = convertActivationFunction(af);

            return new LayerDTO(weight, biases, activationFunction);
        } catch (Exception ee) {
            throw new InvalidInputFormatException("Wrong format of input : " + encodedLayer);
        }
    }

    public Matrix getWeight() {
        return weight;
    }

    public Matrix getBiases() {
        return biases;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    private static ActivationFunction convertActivationFunction(final String classToString) {
        try {
            return (ActivationFunction) Class.forName(classToString).getConstructors()[0].newInstance(null);
        } catch (InstantiationException | ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return new Relu();
        }
    }

    private static Matrix convert(final String arraysDeepToString) {
        final var withoutRedundantBrackets = arraysDeepToString.substring(1, arraysDeepToString.length() - 1);
        final var pattern = Pattern.compile("\\[[-., 0-9]+]");
        final var matcher = pattern.matcher(withoutRedundantBrackets);

        final List<String> findAll = new ArrayList<>();
        while (matcher.find()) {
            findAll.add(matcher.group());
        }

        if (findAll.size() == 1) {
            return Matrix.of(toDoubleArray(findAll.get(0)));
        }
        return Matrix.of(toDoubleDoubleArray(findAll));
    }

    private static double[][] toDoubleDoubleArray(final List<String> arrayDoubleByEnter) {
        return arrayDoubleByEnter
                .stream()
                .map(LayerDTO::toDoubleArray)
                .toArray(double[][]::new);
    }

    private static double[] toDoubleArray(final String stringOfDoubles) {
        final var withoutBrackets = stringOfDoubles.substring(1, stringOfDoubles.length() - 1);
        return Arrays.stream(withoutBrackets.split(","))
                .map(String::trim)
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LayerDTO layerDTO = (LayerDTO) o;
        return Objects.equals(weight, layerDTO.weight) &&
                Objects.equals(biases, layerDTO.biases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weight, biases);
    }
}
