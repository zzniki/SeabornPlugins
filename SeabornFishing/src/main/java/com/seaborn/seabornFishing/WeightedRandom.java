package com.seaborn.seabornFishing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleFunction;

public class WeightedRandom {

    public static <T> double getEffectiveWeight(
            T element,
            @NotNull ToDoubleFunction<T> weightFunction
    ) {
        return weightFunction.applyAsDouble(element);
    }

    public static <T> @Nullable T pick(
            @NotNull List<T> elements,
            ToDoubleFunction<T> weightFunction,
            double luck,
            Random random
    ) {
        if (elements.isEmpty()) return null;

        Random rng = (random != null) ? random : ThreadLocalRandom.current();

        double[] effectiveWeights = new double[elements.size()];
        double totalWeight = 0.0;

        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            double base = getEffectiveWeight(element, weightFunction);

            // Cap weights
            if (base <= 0.0) {
                effectiveWeights[i] = 0.0;
                continue;
            }

            // Apply luck
            double withLuck = Math.pow(base, 1.0 - (luck - 1.0));
            effectiveWeights[i] = withLuck;
            totalWeight += withLuck;
        }

        double r = rng.nextDouble() * totalWeight;
        for (int i = 0; i < elements.size(); i++) {
            r -= effectiveWeights[i];
            if (r <= 0.0) return elements.get(i);
        }

        return elements.getLast();

    }


}
