package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Primitive<T, V> extends Data<T> {
    protected V value;
    protected boolean modified;

    Primitive() {
    }

    Primitive(RandomAccessFile r, T arg) throws IOException {
        super(r, arg);
    }

    public V value() {
        return value;
    }

    public Primitive value(V value) {
        modified = !this.value.equals(value);
        if (modified) {
            this.value = value;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (value != null && value.equals(o))
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Primitive<?, ?> that = (Primitive<?, ?>) o;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
