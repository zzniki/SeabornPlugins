package com.oheers.fish.database.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class DirtyTracked {
    private boolean dirty = false;
    private final Set<String> dirtyFields = new HashSet<>();

    protected void markDirty() {
        this.dirty = true;
    }

    protected void markFieldDirty(String fieldName) {
        this.dirtyFields.add(fieldName);
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Set<String> getDirtyFields() {
        return Collections.unmodifiableSet(dirtyFields);
    }

    public void clearDirty() {
        this.dirty = false;
        this.dirtyFields.clear();
    }
}
