package org.java4me.gateway.mapper;

public interface Mapper<F, T> {
    T map(F obj);

    default T mapObject(F fromObj, T toObj) {
        return toObj;
    }
}
