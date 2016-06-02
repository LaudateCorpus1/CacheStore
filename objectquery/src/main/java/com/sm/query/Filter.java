package com.sm.query;

/**
 * Created by mhsieh on 3/15/16.
 */
public interface Filter {
    public enum Impl {
        Serializer, Schema;
    }

    public boolean runPredicate(Object... source);
}
