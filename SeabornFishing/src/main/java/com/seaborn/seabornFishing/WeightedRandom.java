package com.seaborn.seabornFishing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleFunction;

public class WeightedRandom {

    private static final float MAX_LUCK = 10f;

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
        double[] baseWeights = new double[elements.size()];
        double totalWeight = 0.0;

        // Normalize luck in [1, 10] -> [0, 1]
        double modLuck = (luck / (MAX_LUCK - 1f)) - (1.0 / (MAX_LUCK - 1f));

        // Calculate average
        double sum = 0;
        double count = elements.size();

        for (int i = 0; i < count; i++) {
            double base = getEffectiveWeight(elements.get(i), weightFunction);
            baseWeights[i] = base;
            if (base > 0.0) {
                sum += base;
            }
        }

        double avg = sum / count;

        // Modify weights according to luck
        for (int i = 0; i < count; i++) {
            double base = baseWeights[i];

            // Cap weights
            if (base <= 0.0) {
                effectiveWeights[i] = 0.0;
                continue;
            }

            // Apply luck
            double withLuck = (1.0 - modLuck) * base + modLuck * avg;
            // OLD FUNC: Math.pow(base, 1.0 - (luck - 1.0));
            effectiveWeights[i] = withLuck;
            totalWeight += withLuck;
        }

        // Pick element
        double r = rng.nextDouble() * totalWeight;
        for (int i = 0; i < elements.size(); i++) {
            r -= effectiveWeights[i];
            if (r <= 0.0) return elements.get(i);
        }

        return elements.getLast();

    }


}
