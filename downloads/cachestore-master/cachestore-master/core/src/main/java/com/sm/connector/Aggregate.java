package com.sm.connector;

/**
 * Created by mhsieh on 2/29/16.
 */
public interface Aggregate<T> {
    public void aggregate(T t);
    public T get();
}
