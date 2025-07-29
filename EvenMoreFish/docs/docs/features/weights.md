---
title: Weight System
description: Explaining the weight system
---
# Weight System Explanation

This system uses weights to define the importance or likelihood of an object being selected. The weights are translated into percentages to reflect the probability of each object being chosen.

---

## How It Works

1. **Weights are assigned to each object** based on their importance or likelihood of being selected.
2. **The total weight** is calculated by adding up all the individual weights.
3. **The probability** of each object being selected is calculated by dividing its weight by the total weight.
4. **The probability is converted to a percentage** by multiplying by 100.

---

## Example with Three Items

### Given Weights:

- **Common**: Weight = 100
- **Epic**: Weight = 3
- **Junk**: Weight = 5

### Step 1: Calculate the Total Weight
```text
Total Weight = Weight of Common + Weight of Epic + Weight of Junk
             = 100 + 3 + 5
             = 108
```

---

### Step 2: Calculate the Probability for Each Item

#### Common

```text
Probability = Weight of Common / Total Weight
            = 100 / 108
            ≈ 0.9259

Percentage = 0.9259 × 100
           ≈ 92.59%
```

#### Epic

```text
Probability = Weight of Epic / Total Weight
            = 3 / 108
            ≈ 0.0278

Percentage = 0.0278 × 100
           ≈ 2.78%
```

#### Junk

```text
Probability = Weight of Junk / Total Weight
            = 5 / 108
            ≈ 0.0463

Percentage = 0.0463 × 100
           ≈ 4.63%
```

---

### Final Chances

- **Common**: ~92.59% chance of being selected.
- **Epic**: ~2.78% chance of being selected.
- **Junk**: ~4.63% chance of being selected.

---

## Summary

- The **Common** item is the most likely to be selected due to its high weight.
- The **Epic** item has a very low chance of being selected.
- The **Junk** item has a slightly higher chance than the **Epic** item but is still much less likely than the **Common** item.

This system ensures that objects with higher weights are prioritized in the selection process.

---

## Baits and Boosted Weights

Baits modify the selection chances of specific items by boosting their weights. When a bait is applied, it multiplies the base weight of the affected items, effectively increasing their likelihood of being selected.

---

### How It Works

1. **Each item has a base weight**, which defines its normal chance of being selected.
2. **Baits can boost specific items** by increasing their effective weight.
3. **The boost is applied** by multiplying the item's base weight by a `boostRate` if the item is in the list of `boosted` items.
4. **Items not boosted** retain their original base weight.

---

### Effective Weight Calculation

The effective weight of an item is determined using the following logic:

```java
private static <T> double getEffectiveWeight(
        T element,
        @NotNull ToDoubleFunction<T> weightFunction,
        double boostRate,
        Set<T> boosted
) {
    double baseWeight = weightFunction.applyAsDouble(element);
    if (baseWeight <= 0.0) return 0.0;

    return (boostRate != -1 && boosted.contains(element))
            ? baseWeight * boostRate
            : baseWeight;
}
```

* If the item’s base weight is `0` or less, it will not be considered.
* If the item is **in the `boosted` set** and `boostRate` is not `-1`, its weight is multiplied by the `boostRate`.
* Otherwise, the base weight remains unchanged.

---

### Example

Suppose you have the following bait configuration:

* **Boost Rate**: `2.0`
* **Boosted Items**: `{Epic}`

And the base weights are:

* **Common**: 100
* **Epic**: 3
* **Junk**: 5

The **effective weights** would be:

* **Common**: 100
* **Epic**: 3 × 2.0 = 6
* **Junk**: 5

### Recalculated Total Weight

```text
Total Weight = 100 (Common) + 6 (Epic) + 5 (Junk)
             = 111
```

### New Selection Chances

* **Common**: 100 / 111 ≈ 90.09%
* **Epic**: 6 / 111 ≈ 5.41%
* **Junk**: 5 / 111 ≈ 4.50%

---

### Summary

* Baits dynamically shift the probability distribution by increasing the weight of specific items.
* This system allows targeted promotion of certain items without altering the base configuration.
* It preserves the fairness and transparency of the weight system while offering additional control through boosting.

---
