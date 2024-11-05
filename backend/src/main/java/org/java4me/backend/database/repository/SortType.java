package org.java4me.backend.database.repository;

public enum SortType {
    ASC, DESC;

    @Override
    public String toString() {
        return name();
    }
}
