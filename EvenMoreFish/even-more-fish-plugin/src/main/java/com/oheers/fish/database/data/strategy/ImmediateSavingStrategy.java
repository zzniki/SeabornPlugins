package com.oheers.fish.database.data.strategy;

import java.util.Collection;
import java.util.function.Consumer;

public class ImmediateSavingStrategy<T> implements DataSavingStrategy<T> {
    private final Consumer<T> saveFunction;

    public ImmediateSavingStrategy(Consumer<T> saveFunction) {
        this.saveFunction = saveFunction;
    }

    @Override
    public void save(T data) {
        saveFunction.accept(data); // Save immediately
    }

    @Override
    public void saveAll(Collection<T> data) {
        data.forEach(this::save); // Save each item immediately
    }
}