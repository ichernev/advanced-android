package com.example.iskren.a07threading;

/**
 * Created by iskren on drugs.
 */

public class VarHolder<T> {
    private T t;

    public VarHolder() {
        this(null);
    }
    public VarHolder(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }
}
