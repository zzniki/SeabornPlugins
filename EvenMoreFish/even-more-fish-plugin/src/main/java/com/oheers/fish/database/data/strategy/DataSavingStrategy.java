package com.oheers.fish.database.data.strategy;

import java.util.Collection;

public interface DataSavingStrategy<T> {
    void save(T data); // Immediate save
    void saveAll(Collection<T> data); // Batch save
}
