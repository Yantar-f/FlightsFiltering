package com.gridnine.testing;

@FunctionalInterface
public interface Condition<T> {
    boolean apply(T t);
}
