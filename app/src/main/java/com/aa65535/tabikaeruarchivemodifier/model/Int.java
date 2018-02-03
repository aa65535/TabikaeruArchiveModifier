package com.aa65535.tabikaeruarchivemodifier.model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Int extends Data<Void> {
    private int value;
    private boolean modified;

    Int(RandomAccessFile r) throws IOException {
        super(r, null);
    }

    @Override
    protected void initialize(Void arg) throws IOException {
        this.value = r.readInt();
    }

    public int value() {
        return value;
    }

    public Int value(int value) {
        this.modified = this.value != value;
        this.value = value;
        return this;
    }

    @Override
    public boolean write() {
        if (modified) {
            try {
                r.seek(offset());
                r.writeInt(value);
                modified = false;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Int anInt = (Int) o;
        return value == anInt.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
