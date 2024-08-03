package com.gridnine.testing;

@FunctionalInterface
public interface Condition<T> {
    boolean isApplied(T t);
}
